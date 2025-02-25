package utils;

import models.*;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeeFileHandler extends BaseFileHandler {
    public static void saveEmployees(PrintWriter writer, List<Employee> employees) {
        writer.println("=== EMPLOYEES ===");
        for (Employee emp : employees) {
            writer.printf("Employee #%d: %s %s, Role: %s, Salary: %.2f, Hire Date: %s%n",
                emp.getId(),
                emp.getFirstName(),
                emp.getLastName(),
                emp.getRole(),
                emp.getSalary(),
                emp.getHireDate());
        }
        writer.println();
    }

    public static Employee parseEmployeeFromString(String line) {
        try {
            // Format: "Employee #1: Fernand Ferreir, Role: Manager, Salary: 1950,00, Hire Date: 2025-01-04"
            Pattern pattern = Pattern.compile("Employee #(\\d+): ([\\w]+) ([\\w]+), Role: ([\\w]+), Salary: ([\\d,]+), Hire Date: ([\\d-]+)");
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                int id = Integer.parseInt(matcher.group(1));
                String firstName = matcher.group(2);
                String lastName = matcher.group(3);
                String role = matcher.group(4);
                double salary = Double.parseDouble(matcher.group(5).replace(",", "."));
                String hireDate = matcher.group(6);

                System.out.println("Debug - Parsing employé: " + firstName + " " + lastName);
                return new Employee(id, firstName, lastName, role, hireDate, salary);
            } else {
                System.err.println("Format de ligne invalide: " + line);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing d'un employé: " + e.getMessage() + " (ligne: " + line + ")");
            return null;
        }
    }
}
