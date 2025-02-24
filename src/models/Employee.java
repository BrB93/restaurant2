package models;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private String role;
    private String hireDate;
    private double salary;

    public Employee(int id, String firstName, String lastName, String role, String hireDate, double salary) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.hireDate = hireDate;
        this.salary = salary;
    }

    public String toString() {
        return "Employee #" + id + ": " + firstName + " " + lastName + ", Role: " + role + ", Salary: " + salary + ", Hire Date: " + hireDate;
    }

    // Ajout des getters nécessaires
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }

    // Compléter les getters manquants
    public String getHireDate() {
        return hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public int getId() {
        return id;
    }
}