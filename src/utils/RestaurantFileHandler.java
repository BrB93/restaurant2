package utils;

import models.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RestaurantFileHandler extends BaseFileHandler {
    private static final String DATA_DIR = "src/data/";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void saveRestaurant(Restaurant restaurant) {
        createDataDirectory(DATA_DIR);
        String filename = String.format("%srestaurant_%d.txt", DATA_DIR, restaurant.getId());
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // En-tête du restaurant
            writer.println("=== RESTAURANT INFORMATION ===");
            writer.println("ID: " + restaurant.getId());
            writer.println("Name: " + restaurant.getName());
            writer.println("Address: " + restaurant.getAddress());
            writer.println("PostalCode: " + restaurant.getPostalCode());
            writer.println("City: " + restaurant.getCity());
            writer.println();

            // Menu
            MenuFileHandler.saveMenu(writer, restaurant.getMenu());

            // Employés
            EmployeeFileHandler.saveEmployees(writer, restaurant.getEmployees());

            // Commandes
            OrderFileHandler.saveOrders(writer, restaurant.getOrders());

        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du restaurant: " + e.getMessage());
        }
    }

    public static Restaurant loadRestaurant(int id) {
        String filename = DATA_DIR + "restaurant_" + id + ".txt";
        Restaurant restaurant = null;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String section = "";
            Map<String, String> restaurantInfo = new HashMap<>();
            List<Employee> employees = new ArrayList<>();
            List<Order> orders = new ArrayList<>();
            Menu menu = new Menu();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                if (line.startsWith("===")) {
                    section = line;
                    continue;
                }

                switch (section) {
                    case "=== RESTAURANT INFORMATION ===":
                        if (line.contains(":")) {
                            String[] parts = line.split(":", 2);
                            restaurantInfo.put(parts[0].trim(), parts[1].trim());
                        }
                        break;

                    case "=== EMPLOYEES ===":
                        if (line.startsWith("Employee #")) {
                            Employee emp = parseEmployee(line);
                            if (emp != null) {
                                employees.add(emp);
                                System.out.println("Debug - Parsing employé: " + emp.getFirstName() + " " + emp.getLastName());
                            }
                        }
                        break;

                    case "=== ORDERS ===":
                        if (line.startsWith("Order #")) {
                            Order order = parseOrder(line, reader);  // Pass the reader here
                            if (order != null) {
                                orders.add(order);
                            }
                        }
                        break;

                    case "=== MENU ===":
                        Dish dish = parseDish(line);
                        if (dish != null) {
                            menu.addDish(dish);
                        }
                        break;
                }
            }

            // Créer le restaurant avec les informations chargées
            if (!restaurantInfo.isEmpty()) {
                restaurant = new Restaurant(
                    Integer.parseInt(restaurantInfo.get("ID")),
                    restaurantInfo.get("Name"),
                    restaurantInfo.get("Address"),
                    restaurantInfo.get("PostalCode"),
                    restaurantInfo.get("City")
                );
                restaurant.setMenu(menu);
                restaurant.getEmployees().addAll(employees);
                restaurant.getOrders().addAll(orders);
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }

        return restaurant;
    }

    private static Order parseOrder(String orderLine, BufferedReader reader) throws IOException {
        try {
            // Format attendu: "Order #1 (24/02/2025 11:19) - Status: Terminée"
            int orderNumber = Integer.parseInt(orderLine.substring(orderLine.indexOf("#") + 1, orderLine.indexOf(" (")));
            
            String dateStr = orderLine.substring(orderLine.indexOf("(") + 1, orderLine.indexOf(")"));
            LocalDateTime orderTime = LocalDateTime.parse(dateStr, formatter);
            
            String status = orderLine.substring(orderLine.lastIndexOf(":") + 2);
            
            // Lire la ligne suivante pour le total
            String totalLine = reader.readLine();
            double total = 0.0;
            if (totalLine != null && totalLine.contains("Total:")) {
                total = Double.parseDouble(totalLine.substring(totalLine.indexOf(":") + 2)
                                                  .replace("€", "")
                                                  .replace(",", ".")
                                                  .trim());
            }
            
            Order order = new Order(orderNumber);
            order.setOrderTime(orderTime);
            order.setStatus(status);
            order.setTotal(total);
            
            return order;
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing de la commande : " + orderLine + " - " + e.getMessage());
            return null;
        }
    }

    private static Employee parseEmployee(String line) {
        try {
            // Format: "Employee #1: Fernand Ferreir, Role: Manager, Salary: 1950,00, Hire Date: 2025-01-04"
            
            // Extraire l'ID et le nom
            String[] mainParts = line.split(", Role:");
            String[] idAndName = mainParts[0].split(": ");
            int id = Integer.parseInt(idAndName[0].substring(idAndName[0].indexOf("#") + 1).trim());
            String[] names = idAndName[1].trim().split(" ");
            String firstName = names[0];
            String lastName = names[1];
            
            // Extraire le rôle, le salaire et la date d'embauche
            String[] remainingParts = mainParts[1].split(", ");
            String role = remainingParts[0].trim();
            double salary = Double.parseDouble(remainingParts[1].split(": ")[1].trim()
                                                               .replace("€", "")
                                                               .replace(",", "."));
            String hireDate = remainingParts[2].split(": ")[1].trim();
            
            Employee emp = new Employee(id, firstName, lastName, role, hireDate, salary);
            System.out.println("Debug - Employee parsed successfully: " + emp);
            return emp;
            
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing de l'employé : " + line);
            System.err.println("Message d'erreur : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static Dish parseDish(String line) {
        try {
            // Format: "Carbo - pate sauce creme : 17,00€ (Plat, 458 kcal, 15 min)"
            String[] mainParts = line.split(" : ");
            String[] nameParts = mainParts[0].split(" - ", 2);
            String name = nameParts[0];
            String description = nameParts[1];
            
            String[] pricePart = mainParts[1].split(" \\(");
            double price = Double.parseDouble(pricePart[0].replace("€", "").replace(",", "."));
            
            String[] details = pricePart[1].replace(")", "").split(", ");
            String category = details[0];
            int calories = Integer.parseInt(details[1].replace(" kcal", ""));
            int prepTime = Integer.parseInt(details[2].replace(" min", ""));
            
            // Valeurs par défaut pour les autres champs
            return new Dish(name, description, price, calories, category, "Normale", true, 
                          new ArrayList<>(), "Standard", prepTime, 0.0, "");
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing du plat : " + line);
            e.printStackTrace();
            return null;
        }
    }
}
