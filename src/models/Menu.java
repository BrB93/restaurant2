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

    @Override
    public String toString() {
        if (dishes.isEmpty()) {
            return "Le menu est vide";
        }

        StringBuilder sb = new StringBuilder();
        // Grouper les plats par catégorie
        dishes.stream()
            .collect(java.util.stream.Collectors.groupingBy(Dish::getCategory))
            .forEach((category, dishList) -> {
                sb.append("\n=== ").append(category).append(" ===\n");
                dishList.forEach(dish -> 
                    sb.append(String.format("- %s (%.2f€)\n  %s\n", 
                        dish.getName(), 
                        dish.getCurrentPrice(),
                        dish.getDescription()
                    ))
                );
            });
        return sb.toString();
    }
}