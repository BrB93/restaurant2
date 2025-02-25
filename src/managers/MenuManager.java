package managers;

import models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuManager {
    private static Scanner scanner = new Scanner(System.in);

    public static void addDishToMenu(Restaurant restaurant) {
        try {
            System.out.print("Nom du plat : ");
            String name = scanner.nextLine();
            
            System.out.print("Description : ");
            String description = scanner.nextLine();
            
            System.out.print("Prix : ");
            double price = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Calories : ");
            int calories = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Catégorie (Entrée/Plat/Dessert) : ");
            String category = scanner.nextLine();
            
            System.out.print("Taille portion (Petite/Normale/Grande) : ");
            String portionSize = scanner.nextLine();
            
            System.out.print("Disponible (true/false) : ");
            boolean available = Boolean.parseBoolean(scanner.nextLine());
            
            List<String> ingredients = new ArrayList<>();
            System.out.println("Entrez les ingrédients (ligne vide pour terminer) :");
            while (true) {
                String ingredient = scanner.nextLine();
                if (ingredient.isEmpty()) break;
                ingredients.add(ingredient);
            }
            
            System.out.print("Type de cuisine : ");
            String cuisineType = scanner.nextLine();
            
            System.out.print("Temps de préparation (minutes) : ");
            int preparationTime = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Prix spécial (0 si aucun) : ");
            double specialPrice = Double.parseDouble(scanner.nextLine());
            
            System.out.print("URL de l'image : ");
            String imageURL = scanner.nextLine();

            Dish newDish = new Dish(name, description, price, calories, 
                                  category, portionSize, available, 
                                  ingredients, cuisineType, preparationTime, 
                                  specialPrice, imageURL);
            
            restaurant.getMenu().addDish(newDish);
            System.out.println("Plat ajouté au menu avec succès !");
            
        } catch (NumberFormatException e) {
            System.out.println("Erreur : Veuillez entrer un nombre valide.");
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout du plat : " + e.getMessage());
        }
    }

    public static void removeDishFromMenu(Restaurant restaurant) {
        List<Dish> dishes = restaurant.getMenu().getDishes();
        if (dishes.isEmpty()) {
            System.out.println("Le menu est vide !");
            return;
        }

        System.out.println("\n=== Plats disponibles ===");
        for (int i = 0; i < dishes.size(); i++) {
            System.out.println((i + 1) + ". " + dishes.get(i).toString());
        }

        try {
            System.out.print("Sélectionnez le numéro du plat à supprimer (1-" + dishes.size() + ") : ");
            int choice = Integer.parseInt(scanner.nextLine()) - 1;

            if (choice >= 0 && choice < dishes.size()) {
                Dish dishToRemove = dishes.get(choice);
                restaurant.getMenu().removeDish(dishToRemove);
                System.out.println("Plat supprimé avec succès !");
            } else {
                System.out.println("Numéro de plat invalide !");
            }
        } catch (NumberFormatException e) {
            System.out.println("Veuillez entrer un numéro valide.");
        }
    }


    public static void displayMenu(Restaurant restaurant) {
        System.out.println("\n=== Menu du restaurant ===");
        System.out.println(restaurant.getMenu().toString());
    }
    public static void searchDish(Restaurant restaurant) {
        if (restaurant.getMenu().getDishes().isEmpty()) {
            System.out.println("Le menu est vide !");
            return;
        }

        System.out.println("\n=== Recherche de plat ===");
        System.out.println("1. Rechercher par nom");
        System.out.println("2. Rechercher par catégorie");
        System.out.println("3. Rechercher par prix maximum");
        System.out.println("4. Rechercher par calories maximum");
        System.out.print("Votre choix : ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            List<Dish> results = new ArrayList<>();

            switch (choice) {
                case 1:
                    System.out.print("Entrez le nom à rechercher : ");
                    String searchName = scanner.nextLine().toLowerCase();
                    results = restaurant.getMenu().getDishes().stream()
                        .filter(dish -> dish.getName().toLowerCase().contains(searchName))
                        .toList();
                    break;

                case 2:
                    System.out.print("Entrez la catégorie (Entrée/Plat/Dessert) : ");
                    String searchCategory = scanner.nextLine();
                    results = restaurant.getMenu().getDishes().stream()
                        .filter(dish -> dish.getCategory().equalsIgnoreCase(searchCategory))
                        .toList();
                    break;

                case 3:
                    System.out.print("Entrez le prix maximum : ");
                    double maxPrice = Double.parseDouble(scanner.nextLine());
                    results = restaurant.getMenu().getDishes().stream()
                        .filter(dish -> dish.getCurrentPrice() <= maxPrice)
                        .toList();
                    break;

                case 4:
                    System.out.print("Entrez le nombre maximum de calories : ");
                    int maxCalories = Integer.parseInt(scanner.nextLine());
                    results = restaurant.getMenu().getDishes().stream()
                        .filter(dish -> dish.getCalories() <= maxCalories)
                        .toList();
                    break;

                default:
                    System.out.println("Option invalide !");
                    return;
            }

            // Afficher les résultats
            if (results.isEmpty()) {
                System.out.println("Aucun plat trouvé !");
            } else {
                System.out.println("\nPlats trouvés :");
                for (int i = 0; i < results.size(); i++) {
                    System.out.println((i + 1) + ". " + results.get(i));
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Erreur : Veuillez entrer un nombre valide.");
        } catch (Exception e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
    }
}
