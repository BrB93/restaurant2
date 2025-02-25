package managers;

import models.*;
import utils.RestaurantFileHandler;
import java.util.Map;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class EmployeeManager {
    private static Scanner scanner = new Scanner(System.in);

    public static void manageEmployees(Restaurant restaurant) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n=== Gestion des employés ===");
            System.out.println("1. Afficher la liste des employés");
            System.out.println("2. Ajouter un employé");
            System.out.println("3. Retour au menu principal");
            System.out.print("Votre choix : ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        displayEmployees(restaurant);
                        break;
                    case 2:
                        addEmployee(restaurant);
                        break;
                    case 3:
                        managing = false;
                        break;
                    default:
                        System.out.println("Option invalide !");
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un numéro valide");
            }
        }
    }


    public static void addEmployee(Restaurant restaurant) {
        System.out.print("Prénom : ");
        String firstName = scanner.nextLine();
        System.out.print("Nom : ");
        String lastName = scanner.nextLine();
        System.out.print("Rôle : ");
        String role = scanner.nextLine();
        System.out.print("Salaire : ");
        double salary = Double.parseDouble(scanner.nextLine());
        System.out.print("Date d'embauche (YYYY-MM-DD) : ");
        String hireDate = scanner.nextLine();

        Employee newEmployee = new Employee(
            restaurant.getEmployees().size() + 1,
            firstName,
            lastName,
            role,
            hireDate,
            salary
        );
        restaurant.addEmployee(newEmployee);
        RestaurantFileHandler.saveRestaurant(restaurant);  // Modifié ici
        System.out.println("Employé ajouté et sauvegardé !");
    }

    public static void displayEmployees(Restaurant restaurant) {
        if (restaurant.getEmployees().isEmpty()) {
            System.out.println("Aucun employé dans ce restaurant !");
            return;
        }

        System.out.println("\n=== Liste des employés ===");
        Map<String, List<Employee>> employeesByRole = restaurant.getEmployees().stream()
            .collect(Collectors.groupingBy(Employee::getRole));

        employeesByRole.forEach((role, employees) -> {
            System.out.println("\n" + role + " :");
            employees.forEach(emp -> 
                System.out.printf("- %s %s (Depuis le %s, Salaire: %.2f euros)%n",
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getHireDate(),
                    emp.getSalary())
            );
        });

        double totalMonthly = restaurant.totalSalaireEmployes();
        System.out.printf("%nTotal des dépenses en salaires : %.2f euros/mois%n", totalMonthly);
        System.out.printf("Total des dépenses en salaires : %.2f euros/an%n", totalMonthly * 12);
    }
}
