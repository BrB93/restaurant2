package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // Initialiser nextOrderNumber à la plus grande valeur existante + 1
        this.nextOrderNumber = 1;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public Order createNewOrder() {
        Order order = new Order(nextOrderNumber++);
        orders.add(order);
        return order;
    }

    // Nouvelle méthode pour mettre à jour le nextOrderNumber
    public void updateNextOrderNumber() {
        if (!orders.isEmpty()) {
            nextOrderNumber = orders.stream()
                .mapToInt(Order::getOrderNumber)
                .max()
                .getAsInt() + 1;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Restaurant #%d : %s, Adresse : %s\n", id, name, address));
        
        // Regrouper les employés par rôle
        Map<String, List<String>> employeesByRole = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getRole,
                Collectors.mapping(
                    e -> e.getFirstName() + " " + e.getLastName(),
                    Collectors.toList()
                )
            ));
        
        if (!employees.isEmpty()) {
            sb.append("Employés : \n");
            employeesByRole.forEach((role, names) -> {
                sb.append("- ").append(role).append(" : ");
                sb.append(String.join(", ", names));
                sb.append("\n");
            });
        }
        
        return sb.toString();
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
