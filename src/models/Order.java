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
    private static Order lastCreated;

    public Order(int orderNumber) {
        this.orderNumber = orderNumber;
        this.dishes = new ArrayList<>();
        this.total = 0;
        this.orderTime = LocalDateTime.now();
        this.status = "En cours";
        lastCreated = this;
    }

    public void addDish(Dish dish) {
        dishes.add(dish);
        // Mettre à jour le total automatiquement
        this.total += dish.getCurrentPrice();
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
        // Ne pas recalculer le total à partir des plats
        // car nous chargeons une commande existante
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public static Order getLastCreated() {
        return lastCreated;
    }

    // Nouvelle méthode pour recalculer le total
    public void recalculateTotal() {
        this.total = dishes.stream()
            .mapToDouble(Dish::getCurrentPrice)
            .sum();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("Order #%d (%s) - Total: %.2f euros - Status: %s", 
            orderNumber, orderTime.format(formatter), total, status);
    }
}