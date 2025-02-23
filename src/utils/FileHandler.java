// Package containing models
package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import models.*;

// FileHandler class for managing file operations
public class FileHandler {
    private static final String DATA_DIR = "src/data/";

    public static void saveRestaurant(Restaurant restaurant) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = DATA_DIR + "restaurant_" + restaurant.getId() + ".txt";
        List<String> data = new ArrayList<>();

        // Section Restaurant
        data.add("=== RESTAURANT INFORMATION ===");
        data.add("ID: " + restaurant.getId());
        data.add("Name: " + restaurant.getName());
        data.add("Address: " + restaurant.getAddress());

        // Section Menu
        data.add("\n=== MENU ===");
        List<Dish> dishes = restaurant.getMenu().getDishes();
        StringBuilder menuStr = new StringBuilder("Menu: [");
        for (int i = 0; i < dishes.size(); i++) {
            if (i > 0) menuStr.append(", ");
            menuStr.append(dishes.get(i).toString());
        }
        menuStr.append("]");
        data.add(menuStr.toString());

        // Section Employés
        data.add("\n=== EMPLOYEES ===");
        for (Employee e : restaurant.getEmployees()) {
            data.add(e.toString());
        }

        // Section Commandes
        data.add("\n=== ORDERS ===");
        for (Order o : restaurant.getOrders()) {
            data.add(o.toString());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(fileName));
            String name = "";
            String address = "";
            for (String line : lines) {
                if (line.startsWith("Name: ")) {
                    name = line.substring(6);
                } else if (line.startsWith("Address: ")) {
                    address = line.substring(9);
                }
            }
            if (!name.isEmpty() && !address.isEmpty()) {
                return new Restaurant(id, name, address);
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du restaurant : " + e.getMessage());
        }
        return null;
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
}
