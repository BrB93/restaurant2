package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Restaurant {
    // Attributs existants
    private int id;
    private String name;            // Renommé de nomRestaurant pour cohérence
    private String address;         // Renommé de adresse pour cohérence
    private String postalCode;      // Changé en String pour gérer les codes postaux avec des zéros devant
    private String city;
    private Menu menu;
    private List<Order> orders;     // Renommé de commandes pour cohérence
    private List<Employee> employees; // Renommé de employes pour cohérence
    private int nextOrderNumber;
    private static Restaurant lastCreated;

    // Constructeur existant
    public Restaurant(int id, String name, String address, String postalCode, String city) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.menu = new Menu();
        this.orders = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.nextOrderNumber = 1;
        lastCreated = this;
    }

    // Méthodes existantes
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
        sb.append("=== RESTAURANT INFORMATION ===\n");
        sb.append(String.format("ID: %d\n", id));
        sb.append(String.format("Name: %s\n", name));
        sb.append(String.format("Address: %s\n", address));
        sb.append(String.format("PostalCode: %s\n", postalCode));
        sb.append(String.format("City: %s\n", city));
        
        if (!employees.isEmpty()) {
            sb.append("Employés :\n");
            Map<String, List<Employee>> employeesByRole = employees.stream()
                .collect(Collectors.groupingBy(Employee::getRole));
            employeesByRole.forEach((role, emps) -> {
                emps.forEach(emp -> sb.append(String.format("- %s : %s %s\n", 
                    role, emp.getFirstName(), emp.getLastName())));
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

    // Nouveaux getters
    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    // Nouvelles méthodes requises
    public void ajouterCommande(Order commande) {
        orders.add(commande);
    }

    public void afficherCommandes() {
        System.out.println("Liste des commandes du restaurant " + name + ":");
        for (Order order : orders) {
            System.out.println(order.toString());
        }
    }

    public void supprimerEmploye(Employee employe) {
        employees.remove(employe);
    }

    public List<Employee> chercherEmployeParRole(String role) {
        return employees.stream()
                       .filter(emp -> emp.getRole().equalsIgnoreCase(role))
                       .collect(Collectors.toList());
    }

    public void afficherEmployes() {
        System.out.println("Liste des employés du restaurant " + name + ":");
        Map<String, List<Employee>> parRole = employees.stream()
            .collect(Collectors.groupingBy(Employee::getRole));
        
        parRole.forEach((role, emps) -> {
            System.out.println("\n" + role + ":");
            emps.forEach(emp -> System.out.println("- " + emp.toString()));
        });
    }

    public double totalSalaireEmployes() {
        return employees.stream()
                       .mapToDouble(Employee::getSalary)
                       .sum();
    }

    public double totalChiffreAffaire() {
        return orders.stream()
                    .mapToDouble(Order::getTotal)
                    .sum();
    }

    public double totalChiffreAffaire(LocalDate date) {
        return orders.stream()
                    .filter(order -> order.getOrderTime().toLocalDate().equals(date))
                    .mapToDouble(Order::getTotal)
                    .sum();
    }

    public void afficherRestaurant() {
        System.out.println("=== Détails du restaurant ===");
        System.out.printf("ID: %d\nNom: %s\nAdresse: %s, %s %s\n",
            id, name, address, postalCode, city);
        System.out.printf("Nombre d'employés: %d\n", employees.size());
        System.out.printf("Nombre de commandes: %d\n", orders.size());
        System.out.printf("Chiffre d'affaires total: %.2f\u20AC\n", totalChiffreAffaire());
        System.out.printf("Total des dépenses en salaires: %.2f\u20AC\n", totalSalaireEmployes());
        
        // Afficher le détail des salaires par rôle
        Map<String, List<Employee>> parRole = employees.stream()
            .collect(Collectors.groupingBy(Employee::getRole));
        
        if (!employees.isEmpty()) {
            System.out.println("\nDétail des salaires par rôle :");
            parRole.forEach((role, emps) -> {
                double totalRole = emps.stream()
                    .mapToDouble(Employee::getSalary)
                    .sum();
                System.out.printf("- %s : %.2f\u20AC (%d employés)\n", 
                    role, totalRole, emps.size());
            });
        }
    }

    // Les méthodes de sauvegarde et chargement sont déjà gérées par FileHandler
    // Mais on peut ajouter des méthodes déléguées pour respecter la spécification
    public void sauvegarderCommandes(String fichier) {
        utils.FileHandler.saveOrders(this);
    }

    public void chargerCommandes(String fichier) {
        utils.FileHandler.loadOrders(this);
    }

    public static Restaurant getLastCreated() {
        return lastCreated;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Ajouter setter pour name
    public void setName(String name) {
        this.name = name;
    }
}
