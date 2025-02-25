package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Dish {
    private String name;
    private String description;
    private double price;
    private int calories;
    private String category;
    private String portionSize;
    private LocalDate dateAdded;
    private boolean available;
    private List<String> ingredients;
    private String cuisineType;
    private int preparationTime; // en minutes
    private double specialPrice;
    private String imageURL;

    public Dish(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.dateAdded = LocalDate.now();
        this.available = true;
        this.ingredients = new ArrayList<>();
        // Valeurs par défaut
        this.calories = 0;
        this.category = "Non catégorisé";
        this.portionSize = "Normal";
        this.cuisineType = "Non spécifié";
        this.preparationTime = 30;
        this.specialPrice = 0.0;
        this.imageURL = "";
    }

    // Constructeur complet
    public Dish(String name, String description, double price, int calories, 
                String category, String portionSize, boolean available, 
                List<String> ingredients, String cuisineType, int preparationTime, 
                double specialPrice, String imageURL) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.calories = calories;
        this.category = category;
        this.portionSize = portionSize;
        this.dateAdded = LocalDate.now();
        this.available = available;
        this.ingredients = ingredients;
        this.cuisineType = cuisineType;
        this.preparationTime = preparationTime;
        this.specialPrice = specialPrice;
        this.imageURL = imageURL;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getCalories() { return calories; }
    public String getCategory() { return category; }
    public String getPortionSize() { return portionSize; }
    public LocalDate getDateAdded() { return dateAdded; }
    public boolean isAvailable() { return available; }
    public List<String> getIngredients() { return ingredients; }
    public String getCuisineType() { return cuisineType; }
    public int getPreparationTime() { return preparationTime; }
    public double getSpecialPrice() { return specialPrice; }
    public String getImageURL() { return imageURL; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setCategory(String category) { this.category = category; }
    public void setPortionSize(String portionSize) { this.portionSize = portionSize; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
    public void setPreparationTime(int preparationTime) { this.preparationTime = preparationTime; }
    public void setSpecialPrice(double specialPrice) { this.specialPrice = specialPrice; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    // Méthodes utilitaires
    public void addIngredient(String ingredient) {
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(String ingredient) {
        this.ingredients.remove(ingredient);
    }

    public double getCurrentPrice() {
        return specialPrice > 0 ? specialPrice : price;
    }

    @Override
    public String toString() {
        return String.format("%s - %s : %.2f euros (%s, %d kcal, %d min)", 
            name, description, getCurrentPrice(), category, calories, preparationTime);
    }
}