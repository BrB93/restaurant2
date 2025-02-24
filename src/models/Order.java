package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderNumber;
    private List<Dish> dishes;
    private double total;
    private LocalDateTime orderTime;
    private String status;  // "En cours", "Préparation", "Terminée"

    public Order(int orderNumber) {
        this.orderNumber = orderNumber;
        this.dishes = new ArrayList<>();
        this.total = 0;
        this.orderTime = LocalDateTime.now();
        this.status = "En cours";
    }

    public void addDish(Dish dish) {
        dishes.add(dish);
        total += dish.getCurrentPrice();
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public double getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Ajout des getters manquants
    public int getOrderNumber() {
        return orderNumber;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Order #%d (%s) - Total: %.2f€ - Status: %s", 
                  orderNumber, orderTime.format(formatter), total, status));
        for (Dish dish : dishes) {
            sb.append("\n  - ").append(dish.toString());
        }
        return sb.toString();
    }
}