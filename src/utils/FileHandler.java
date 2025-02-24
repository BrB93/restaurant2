// Package containing models
package utils;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Ajout de l'import manquant
import java.util.*;
import models.*;

// FileHandler class for managing file operations
public class FileHandler {
    private static final String DATA_DIR = "src/data/";
    private static List<Restaurant> restaurants = new ArrayList<>();

    public static void saveRestaurant(Restaurant restaurant) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = DATA_DIR + "restaurant_" + restaurant.getId() + ".txt";
            PrintWriter writer = new PrintWriter(new FileWriter(fileName));

            // Section Restaurant
            writer.println("=== RESTAURANT INFORMATION ===");
            writer.println("ID: " + restaurant.getId());
            writer.println("Name: " + restaurant.getName());
            writer.println("Address: " + restaurant.getAddress());
            writer.println();

            // Section Menu - Correction du format de sauvegarde
            writer.println("=== MENU ===");
            for (Dish dish : restaurant.getMenu().getDishes()) {
                writer.printf("%s - %s : %.2f€ (%s, %d kcal, %d min)%n",
                    dish.getName(),
                    dish.getDescription(),
                    dish.getCurrentPrice(),
                    dish.getCategory(),
                    dish.getCalories(),
                    dish.getPreparationTime());
            }
            writer.println();

            // Section Employés
            writer.println("=== EMPLOYEES ===");
            for (Employee emp : restaurant.getEmployees()) {
                writer.printf("Employee #%d: %s %s, Role: %s, Salary: %.2f, Hire Date: %s%n",
                    emp.getId(),
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getRole(),
                    emp.getSalary(),
                    emp.getHireDate());
            }
            writer.println();

            // Section Orders - Format modifié
            writer.println("=== ORDERS ===");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Order order : restaurant.getOrders()) {
                writer.println("Order #" + order.getOrderNumber() + " (" + 
                             order.getOrderTime().format(formatter) + ") - " +
                             "Status: " + order.getStatus());
                             
                // Ajout des plats de la commande
                for (Dish dish : order.getDishes()) {
                    writer.println("  - " + dish.getName() + " - " + dish.getDescription() + 
                                 " : " + String.format("%.2f", dish.getCurrentPrice()) + "€");
                }
                writer.printf("Total: %.2f€%n", order.getTotal());
                writer.println();
            }

            writer.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du restaurant: " + e.getMessage());
        }
    }

    public static void saveEmployees(Restaurant restaurant) {
        String fileName = "employees_" + restaurant.getId() + ".txt";
        List<String> data = new ArrayList<>();
        for (Employee e : restaurant.getEmployees()) {
            data.add(e.toString());
        }
        writeToFile(fileName, data);
    }

    public static void saveOrders(Restaurant restaurant) {
        String fileName = "orders_" + restaurant.getId() + ".txt";
        List<String> data = new ArrayList<>();
        for (Order o : restaurant.getOrders()) {
            data.add(o.toString());
        }
        writeToFile(fileName, data);
    }

    public static Restaurant loadRestaurant(int id) {
        String fileName = DATA_DIR + "restaurant_" + id + ".txt";
        Restaurant restaurant = null;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            String section = "";
            String name = "";
            String address = "";
            Order currentOrder = null;
            StringBuilder orderBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("===")) {
                    if (currentOrder != null && orderBuilder.length() > 0) {
                        processOrderDetails(currentOrder, orderBuilder.toString());
                        orderBuilder.setLength(0);
                    }
                    section = line;
                    continue;
                }

                if (line.trim().isEmpty()) continue;

                switch (section) {
                    case "=== RESTAURANT INFORMATION ===":
                        if (line.startsWith("Name: ")) name = line.substring(6);
                        if (line.startsWith("Address: ")) {
                            address = line.substring(9);
                            if (restaurant == null) {
                                restaurant = new Restaurant(id, name, address);
                            }
                        }
                        break;

                    case "=== MENU ===":
                        if (restaurant != null && !line.equals("Menu: []")) {
                            // Supprimer "Menu: [" au début et "]" à la fin
                            String menuContent = line;
                            if (line.startsWith("Menu: [")) {
                                menuContent = line.substring(7, line.length() - 1);
                            }
                            if (!menuContent.isEmpty()) {
                                parseDishForMenu(restaurant, menuContent);
                            }
                        }
                        break;

                    case "=== EMPLOYEES ===":
                        if (line.startsWith("Employee #")) {
                            parseEmployee(restaurant, line);
                        }
                        break;

                    case "=== ORDERS ===":
                        if (line.startsWith("Commande #") || line.startsWith("Order #")) {
                            // Si on avait une commande en cours, la finaliser
                            if (currentOrder != null && orderBuilder.length() > 0) {
                                processOrderDetails(currentOrder, orderBuilder.toString());
                                orderBuilder.setLength(0);
                            }
                            currentOrder = parseOrder(restaurant, line);
                            if (currentOrder != null) {
                                restaurant.getOrders().add(currentOrder);
                                orderBuilder.append(line).append("\n");
                            }
                        } else if (currentOrder != null && !line.trim().isEmpty()) {
                            orderBuilder.append(line).append("\n");
                        }
                        break;
                }
            }
            
            // Traiter la dernière commande si elle existe
            if (currentOrder != null && orderBuilder.length() > 0) {
                processOrderDetails(currentOrder, orderBuilder.toString());
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du restaurant: " + e.getMessage());
            return null;
        }

        return restaurant;
    }

    private static void processOrderDetails(Order order, String orderText) {
        try {
            String[] lines = orderText.split("\n");
            double total = 0.0;
            
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("  - ")) {
                    Dish dish = parseDishFromOrderLine(line);
                    if (dish != null) {
                        order.addDish(dish);
                    }
                } else if (line.startsWith("Total: ")) {
                    total = Double.parseDouble(line.substring(7, line.indexOf("€")).replace(",", "."));
                    // Utiliser la réflexion pour mettre à jour le total si nécessaire
                    try {
                        java.lang.reflect.Field totalField = Order.class.getDeclaredField("total");
                        totalField.setAccessible(true);
                        totalField.set(order, total);
                    } catch (Exception e) {
                        System.err.println("Erreur lors de la définition du total : " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement des détails de la commande : " + e.getMessage());
        }
    }

    private static Dish parseDishFromOrderLine(String line) {
        try {
            // Format: "  - NomPlat - Description : Prix€ (Catégorie, Calories kcal, Temps min)"
            String dishInfo = line.substring(4); // Remove "  - "
            String[] parts = dishInfo.split(" : ");
            String[] nameAndDesc = parts[0].split(" - ", 2);
            String name = nameAndDesc[0];
            String description = nameAndDesc[1];
            
            // Parse price
            String priceStr = parts[1].substring(0, parts[1].indexOf("€")).trim();
            double price = Double.parseDouble(priceStr.replace(",", "."));
            
            return new Dish(name, description, price);
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing d'un plat : " + e.getMessage());
            return null;
        }
    }

    private static void parseAndAddEmployee(Restaurant restaurant, String line) {
        try {
            String[] parts = line.split(", ");
            String[] idAndName = parts[0].split(": ");
            int empId = Integer.parseInt(idAndName[1].replace("#", ""));
            String[] names = idAndName[2].split(" ");
            String firstName = names[0];
            String lastName = names[1];
            String role = parts[1].substring(parts[1].indexOf(": ") + 2);
            // Remplacer la virgule par un point pour le parsing
            double salary = Double.parseDouble(parts[2].substring(parts[2].indexOf(": ") + 2).replace(",", "."));
            String hireDate = parts[3].substring(parts[3].indexOf(": ") + 2);

            Employee employee = new Employee(empId, firstName, lastName, role, hireDate, salary);
            restaurant.addEmployee(employee);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement d'un employé : " + e.getMessage());
        }
    }

    private static Order parseAndAddOrder(Restaurant restaurant, String line) {
        try {
            // Format: "Commande #1 (23/02/2025 21:32) - Total: 16.00€ - Status: En cours"
            String[] parts = line.split(" - ");
            String[] orderInfo = parts[0].split(" ");
            int orderNum = Integer.parseInt(orderInfo[1].substring(1)); // Remove '#'
            
            // Parse date and time
            String dateTimeStr = parts[0].substring(parts[0].indexOf("(") + 1, parts[0].indexOf(")"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime orderTime = LocalDateTime.parse(dateTimeStr, formatter);
            
            // Create new order
            Order order = new Order(orderNum);
            // Set status from the last part
            String status = parts[parts.length - 1].split(": ")[1];
            order.setStatus(status);
            
            // Return the order for further processing of dishes
            return order;
        } catch (Exception e) {
            System.out.println("Erreur lors du parsing d'une commande : " + e.getMessage());
            return null;
        }
    }

    public static void loadEmployees(Restaurant restaurant) {
        String fileName = "employees_" + restaurant.getId() + ".txt";
        List<String> data = readFromFile(fileName);
        for (String line : data) {
            // Assuming the format: "Employee #123: John Doe, Role: Chef, Salary: 2500.0, Hire Date: 2024-01-01"
            String[] parts = line.split(", ");
            int id = Integer.parseInt(parts[0].split("#")[1]);
            String[] nameParts = parts[1].split(" ");
            String firstName = nameParts[1];
            String lastName = nameParts[2];
            String role = parts[2].split(": ")[1];
            double salary = Double.parseDouble(parts[3].split(": ")[1]);
            String hireDate = parts[4].split(": ")[1];
            restaurant.addEmployee(new Employee(id, firstName, lastName, role, hireDate, salary));
        }
    }

    public static void loadOrders(Restaurant restaurant) {
        String fileName = "orders_" + restaurant.getId() + ".txt";
        List<String> data = readFromFile(fileName);
        for (String line : data) {
            // Parsing logic for orders can be implemented as per the format used
        }
    }

    private static void writeToFile(String fileName, List<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> readFromFile(String fileName) {
        List<String> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static void loadExistingRestaurants() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            return;
        }

        File[] files = dataDir.listFiles((dir, name) -> name.startsWith("restaurant_") && name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                try {
                    int id = Integer.parseInt(file.getName().split("_")[1].split("\\.")[0]);
                    List<String> lines = Files.readAllLines(file.toPath());
                    
                    String name = "";
                    String address = "";
                    Restaurant restaurant = null;
                    
                    boolean inMenuSection = false;
                    boolean inEmployeesSection = false;
                    boolean inOrdersSection = false;
                    
                    for (String line : lines) {
                        if (line.startsWith("Name: ")) {
                            name = line.substring(6);
                        } else if (line.startsWith("Address: ")) {
                            address = line.substring(9);
                        } else if (line.equals("=== MENU ===")) {
                            inMenuSection = true;
                            inEmployeesSection = false;
                            inOrdersSection = false;
                            continue;
                        } else if (line.equals("=== EMPLOYEES ===")) {
                            if (restaurant == null && !name.isEmpty() && !address.isEmpty()) {
                                restaurant = new Restaurant(id, name, address);
                            }
                            inMenuSection = false;
                            inEmployeesSection = true;
                            inOrdersSection = false;
                            continue;
                        } else if (line.equals("=== ORDERS ===")) {
                            inMenuSection = false;
                            inEmployeesSection = false;
                            inOrdersSection = true;
                            continue;
                        }

                        if (inEmployeesSection && !line.isEmpty() && line.startsWith("Employee:")) {
                            try {
                                // Format: "Employee: #1: Fernand Ferrei, Role: Manager, Salary: 1950.00, Hire Date: 2025-01-05"
                                String[] parts = line.split(", ");
                                
                                // Parse employee ID and name
                                String[] idAndName = parts[0].split(": ");
                                int empId = Integer.parseInt(idAndName[1].replace("#", ""));
                                String[] names = idAndName[2].split(" ");
                                String firstName = names[0];
                                String lastName = names[1];
                                
                                // Parse role, salary, and hire date
                                String role = parts[1].substring(parts[1].indexOf(": ") + 2);
                                double salary = Double.parseDouble(parts[2].substring(parts[2].indexOf(": ") + 2).replace(",", "."));
                                String hireDate = parts[3].substring(parts[3].indexOf(": ") + 2);

                                Employee employee = new Employee(empId, firstName, lastName, role, hireDate, salary);
                                restaurant.addEmployee(employee);
                            } catch (Exception e) {
                                System.out.println("Erreur lors du chargement d'un employé : " + e.getMessage());
                            }
                        }

                        if (inOrdersSection && !line.isEmpty() && line.startsWith("Order #")) {
                            try {
                                // Format: "Order #1 (24/02/2024 09:41) - Total: 16.00€ - Status: Terminée"
                                String[] parts = line.split(" - ");
                                int orderNum = Integer.parseInt(parts[0].split("#")[1].trim().split(" ")[0]);
                                
                                Order order = new Order(orderNum);
                                order.setStatus(parts[2].split(": ")[1]);
                                
                                restaurant.getOrders().add(order);
                            } catch (Exception e) {
                                System.out.println("Erreur lors du chargement d'une commande : " + e.getMessage());
                            }
                        }
                    }
                    
                    if (restaurant != null) {
                        restaurants.add(restaurant);
                    }
                } catch (Exception e) {
                    System.out.println("Erreur lors du chargement d'un restaurant : " + e.getMessage());
                }
            }
        }
    }

    private static void parseEmployee(Restaurant restaurant, String line) {
        try {
            // Format: "Employee #1: Fernand Ferreir, Role: Manager, Salary: 1950,00, Hire Date: 2025-01-04"
            String[] parts = line.split(", ");
            
            // Parse ID and name from first part: "Employee #1: Fernand Ferreir"
            String[] idPart = parts[0].split("#");
            String[] namePart = idPart[1].split(": ");
            int empId = Integer.parseInt(namePart[0]);
            String[] names = namePart[1].split(" ", 2); // Split only in 2 parts to handle multiple word last names
            
            // Parse role from: "Role: Manager"
            String role = parts[1].substring(6);
            
            // Parse salary from: "Salary: 1950,00"
            double salary = Double.parseDouble(parts[2].substring(8).replace(",", "."));
            
            // Parse hire date from: "Hire Date: 2025-01-04"
            String hireDate = parts[3].substring(11);
            
            Employee employee = new Employee(empId, names[0], names[1], role, hireDate, salary);
            restaurant.addEmployee(employee);
            
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing d'un employé: " + e.getMessage());
            System.err.println("Ligne problématique: " + line);
        }
    }

    private static Order parseOrder(Restaurant restaurant, String line) {
        try {
            String[] mainParts = line.split(" - ");
            
            // Parse order number
            String orderNumStr = mainParts[0].substring(mainParts[0].indexOf("#") + 1);
            orderNumStr = orderNumStr.substring(0, orderNumStr.indexOf(" "));
            int orderNum = Integer.parseInt(orderNumStr);
            
            // Parse date and time
            String dateTimeStr = mainParts[0].substring(
                mainParts[0].indexOf("(") + 1,
                mainParts[0].indexOf(")")
            );
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime orderTime = LocalDateTime.parse(dateTimeStr, formatter);
            
            // Create new order with the correct number
            Order order = new Order(orderNum);
            
            // Set status (remove duplicate "Status:" if present)
            String status = mainParts[1].replace("Status: Status:", "Status:");
            status = status.replace("Status:", "").trim();
            order.setStatus(status);
            
            // Set order time using reflection
            try {
                java.lang.reflect.Field field = Order.class.getDeclaredField("orderTime");
                field.setAccessible(true);
                field.set(order, orderTime);
            } catch (Exception e) {
                System.err.println("Erreur lors de la définition de l'heure : " + e.getMessage());
            }

            // Mettre à jour le nextOrderNumber du restaurant
            restaurant.updateNextOrderNumber();
            
            return order;
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing d'une commande : " + e.getMessage());
            return null;
        }
    }

    private static void parseDishForOrder(Order order, String line) {
        try {
            // Format: "  - bolo - pate avec sauce bolognaise : 16,00€ (Plat, 457 kcal, 16 min)"
            String dishLine = line.substring(4); // Enlever "  - "
            
            // Séparer le nom et les détails
            String[] parts = dishLine.split(" : ");
            String name = parts[0];
            
            // Traiter les détails (prix et description)
            String details = parts[1];
            double price = Double.parseDouble(details.substring(0, details.indexOf("€")).replace(",", "."));
            
            // Extraire la description entre parenthèses si elle existe
            String description = "";
            if (details.contains("(")) {
                description = details.substring(
                    details.indexOf("(") + 1,
                    details.indexOf(")")
                );
            }
            
            // Créer et ajouter le plat
            Dish dish = new Dish(name, description, price);
            order.addDish(dish);
            
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing d'un plat: " + e.getMessage());
            System.err.println("Ligne problématique: " + line);
        }
    }

    private static void parseDishForMenu(Restaurant restaurant, String line) {
        try {
            // Format: "NomPlat - Description : Prix€ (Catégorie, Calories kcal, Temps min)"
            String[] mainParts = line.split(" : ");
            String[] nameDesc = mainParts[0].split(" - ", 2);
            String name = nameDesc[0];
            String description = nameDesc[1];

            // Parser le prix et les détails
            String priceAndDetails = mainParts[1];
            double price = Double.parseDouble(
                priceAndDetails.substring(0, priceAndDetails.indexOf("€")).trim().replace(",", ".")
            );

            // Créer le plat avec les informations de base
            Dish dish = new Dish(name, description, price);

            // Parser les détails additionnels entre parenthèses
            if (priceAndDetails.contains("(") && priceAndDetails.contains(")")) {
                String details = priceAndDetails.substring(
                    priceAndDetails.indexOf("(") + 1,
                    priceAndDetails.indexOf(")")
                );
                String[] detailParts = details.split(", ");
                for (String detail : detailParts) {
                    if (detail.endsWith("kcal")) {
                        dish.setCalories(Integer.parseInt(detail.replace(" kcal", "")));
                    } else if (detail.endsWith("min")) {
                        dish.setPreparationTime(Integer.parseInt(detail.replace(" min", "")));
                    } else {
                        dish.setCategory(detail);
                    }
                }
            }

            restaurant.getMenu().addDish(dish);
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing d'un plat du menu: " + e.getMessage());
            System.err.println("Ligne problématique: " + line);
        }
    }
}
