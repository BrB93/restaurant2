package models;

class Dish {
    private String name;
    private String description;
    private double price;

    public Dish(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String toString() {
        return name + " - " + description + " : " + price + "€";
    }
}