package models;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private int id;
    private String name;
    private String address;
    private Menu menu;
    private List<Order> orders;
    private List<Employee> employees;
    private int nextOrderNumber = 1;

    public Restaurant(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.menu = new Menu();
        this.orders = new ArrayList<>();
        this.employees = new ArrayList<>();
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public Order createNewOrder() {
        Order order = new Order(nextOrderNumber++);
        orders.add(order);
        return order;
    }

    public String toString() {
        return "Restaurant #" + id + " : " + name + ", Address: " + address;
    }

    // Add getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Menu getMenu() {
        return menu;
    }
}
