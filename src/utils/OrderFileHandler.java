package utils;

import models.*;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderFileHandler extends BaseFileHandler {
    private static Order currentParsingOrder;

    public static void saveOrders(PrintWriter writer, List<Order> orders) {
        writer.println("=== ORDERS ===");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Order order : orders) {
            writer.println("Order #" + order.getOrderNumber() + " (" +
                         order.getOrderTime().format(formatter) + ") - " +
                         "Status: " + order.getStatus());
            
            for (Dish dish : order.getDishes()) {
                writer.printf("  - %s - %s : %.2f€\n",
                    dish.getName(),
                    dish.getDescription(),
                    dish.getCurrentPrice());
            }
            writer.printf("Total: %.2f€%n%n", order.getTotal());
        }
    }

    public static Order parseOrderFromString(String line) {
        try {
            if (line.startsWith("Order #")) {
                Pattern pattern = Pattern.compile("Order #(\\d+) \\((\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2})\\) - Status: (\\w+)");
                Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {
                    int orderNumber = Integer.parseInt(matcher.group(1));
                    String dateTimeStr = matcher.group(2);
                    String status = matcher.group(3);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    LocalDateTime orderTime = LocalDateTime.parse(dateTimeStr, formatter);

                    currentParsingOrder = new Order(orderNumber);
                    currentParsingOrder.setStatus(status);
                    currentParsingOrder.setOrderTime(orderTime);
                    
                    return currentParsingOrder;
                }
            } else if (line.startsWith("Total: ")) {
                if (currentParsingOrder != null) {
                    String totalStr = line.substring(7, line.indexOf("€")).replace(",", ".");
                    currentParsingOrder.setTotal(Double.parseDouble(totalStr));
                }
            } else if (line.trim().startsWith("-")) {
                if (currentParsingOrder != null) {
                    Pattern dishPattern = Pattern.compile("\\s*- ([^-]+) - ([^:]+) : ([\\d,]+)€");
                    Matcher dishMatcher = dishPattern.matcher(line);
                    
                    if (dishMatcher.find()) {
                        String name = dishMatcher.group(1).trim();
                        String description = dishMatcher.group(2).trim();
                        double price = Double.parseDouble(dishMatcher.group(3).replace(",", "."));
                        
                        Dish dish = new Dish(name, description, price);
                        currentParsingOrder.addDish(dish);
                    }
                }
            }

            return null;
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing d'une commande : " + e.getMessage() + " (ligne: " + line + ")");
            return null;
        }
    }
}
