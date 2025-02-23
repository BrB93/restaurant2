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

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Commande #%d (%s) - %s\n", 
                  orderNumber, orderTime.format(formatter), status));
        for (Dish dish : dishes) {
            sb.append("  - ").append(dish.toString()).append("\n");
        }
        sb.append(String.format("Total : %.2f€", total));
        return sb.toString();
    }
}