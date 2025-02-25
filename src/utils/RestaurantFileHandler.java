package utils;

import models.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantFileHandler extends BaseFileHandler {
    public static void saveRestaurant(Restaurant restaurant) {
        createDataDirectory();
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
        String filename = String.format("%srestaurant_%d.txt", DATA_DIR, id);
        Restaurant restaurant = null;
        Menu menu = new Menu();
        List<Employee> employees = new ArrayList<>();
        List<Order> orders = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String currentSection = "";
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                if (line.startsWith("===")) {
                    currentSection = line;
                    continue;
                }

                switch (currentSection) {
                    case "=== RESTAURANT INFORMATION ===":
                        if (restaurant == null && line.startsWith("ID:")) {
                            int restId = Integer.parseInt(line.split(": ")[1]);
                            restaurant = new Restaurant(restId, "", "", "", "");
                        } else if (line.startsWith("Name:")) {
                            restaurant.setName(line.split(": ")[1]);
                        } else if (line.startsWith("Address:")) {
                            restaurant.setAddress(line.split(": ")[1]);
                        } else if (line.startsWith("PostalCode:")) {
                            restaurant.setPostalCode(line.split(": ")[1]);
                        } else if (line.startsWith("City:")) {
                            restaurant.setCity(line.split(": ")[1]);
                        }
                        break;

                    case "=== MENU ===":
                        Dish dish = MenuFileHandler.parseDishFromString(line);
                        if (dish != null) {
                            menu.addDish(dish);
                        }
                        break;

                    case "=== EMPLOYEES ===":
                        Employee employee = EmployeeFileHandler.parseEmployeeFromString(line);
                        if (employee != null) {
                            employees.add(employee);
                        }
                        break;

                    case "=== ORDERS ===":
                        Order order = OrderFileHandler.parseOrderFromString(line);
                        if (order != null) {
                            orders.add(order);
                        }
                        break;
                }
            }

            if (restaurant != null) {
                restaurant.getMenu().getDishes().addAll(menu.getDishes());
                restaurant.getEmployees().addAll(employees);
                restaurant.getOrders().addAll(orders);
                restaurant.updateNextOrderNumber();
            }

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du restaurant: " + e.getMessage());
        }

        return restaurant;
    }
}
