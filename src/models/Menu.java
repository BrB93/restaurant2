package models;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<Dish> dishes;

    public Menu() {
        this.dishes = new ArrayList<>();
    }

    public void addDish(Dish dish) {
        dishes.add(dish);
    }

    public void removeDish(Dish dish) {
        dishes.remove(dish);
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public String toString() {
        return "Menu: " + dishes;
    }
}