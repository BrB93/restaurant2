package models;

class Restaurant {
    private int id;
    private String name;
    private String address;
    private Menu menu;
    private List<Order> orders;
    private List<Employee> employees;

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

    public String toString() {
        return "Restaurant #" + id + " : " + name + ", Address: " + address;
    }
}
