package models;

class Order {
    private int orderNumber;
    private List<Dish> dishes;
    private double total;

    public Order(int orderNumber) {
        this.orderNumber = orderNumber;
        this.dishes = new ArrayList<>();
        this.total = 0;
    }

    public void addDish(Dish dish) {
        dishes.add(dish);
        total += dish.price;
    }

    public String toString() {
        return "Order #" + orderNumber + " : " + dishes + " Total: " + total + "€";
    }
}