package managers;

import models.*;
import utils.RestaurantFileHandler;
import java.io.File;
import java.util.List;
import java.util.Scanner;

public class RestaurantManager {
    private static Scanner scanner = new Scanner(System.in);
    private static final String DATA_DIR = "src/data/";

    public static void createNewRestaurant(List<Restaurant> restaurants) {
        try {
            System.out.print("Nom du restaurant : ");
            String name = scanner.nextLine();
            
            System.out.print("Adresse : ");
            String address = scanner.nextLine();
            
            String postalCode = "";
            boolean validPostalCode = false;
            while (!validPostalCode) {
                System.out.print("Code postal : ");
                postalCode = scanner.nextLine();
                if (postalCode.matches("\\d{5}")) {
                    validPostalCode = true;
                } else {
                    System.out.println("Veuillez entrer un code postal valide (5 chiffres)");
                }
            }
            
            System.out.print("Ville : ");
            String city = scanner.nextLine();
            
            // Générer un nouvel ID unique
            int newId = 1;
            if (!restaurants.isEmpty()) {
                newId = restaurants.stream()
                    .mapToInt(Restaurant::getId)
                    .max()
                    .getAsInt() + 1;
            }
            
            Restaurant newRestaurant = new Restaurant(newId, name, address, postalCode, city);
            restaurants.add(newRestaurant);
            
            // Créer le répertoire data s'il n'existe pas
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            // Sauvegarder le nouveau restaurant
            RestaurantFileHandler.saveRestaurant(newRestaurant);
            System.out.println("Restaurant créé et sauvegardé avec succès !");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du restaurant : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteRestaurant(List<Restaurant> restaurants) {
        // ...existing code from Main.java deleteRestaurant method...
    }

    public static void loadExistingRestaurants(List<Restaurant> restaurants) {
        restaurants.clear();
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("Création du répertoire de données : " + dataDir.getAbsolutePath());
            return;
        }

        System.out.println("Chargement des restaurants depuis : " + dataDir.getAbsolutePath());
        File[] files = dataDir.listFiles((dir, name) -> name.startsWith("restaurant_") && name.endsWith(".txt"));
        
        if (files != null) {
            for (File file : files) {
                try {
                    String fileName = file.getName();
                    int id = Integer.parseInt(fileName.substring(11, fileName.length() - 4));
                    Restaurant restaurant = RestaurantFileHandler.loadRestaurant(id);
                    if (restaurant != null) {
                        restaurants.add(restaurant);
                        System.out.println("Restaurant chargé : " + restaurant.getName());
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement du fichier " + file.getName() + " : " + e.getMessage());
                }
            }
        }

        System.out.println("Nombre de restaurants chargés : " + restaurants.size());
    }

    private static void manageRestaurant(Restaurant restaurant) {
        // Recharger le restaurant avant de le gérer
        Restaurant updatedRestaurant = RestaurantFileHandler.loadRestaurant(restaurant.getId());  // Modifié ici
        if (updatedRestaurant != null) {
            // Mettre à jour la référence dans la liste des restaurants
            int index = restaurants.indexOf(restaurant);
            if (index != -1) {
                restaurants.set(index, updatedRestaurant);
            }
            restaurant = updatedRestaurant;
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=== Menu principal ===");
            System.out.println("1. Afficher les détails du restaurant"); // Nouvelle option
            System.out.println("2. Ajouter un plat au menu");
            System.out.println("3. Supprimer un plat du menu");
            System.out.println("4. Afficher le menu");
            System.out.println("5. Rechercher un plat");
            System.out.println("6. Ajouter un plat à la commande");
            System.out.println("7. Afficher la commande");
            System.out.println("8. Gérer les employés"); // Option modifiée
            System.out.println("9. Finaliser la commande");
            System.out.println("10. Historique des commandes");
            System.out.println("11. Quitter");
            System.out.println("12. Retour à la liste des restaurants");
            System.out.print("Votre choix : ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        displayRestaurantDetails(restaurant);
                        break;
                    case 2:
                        addDishToMenu(restaurant);
                        break;
                    case 3:
                        removeDishFromMenu(restaurant);
                        break;
                    case 4:
                        displayMenu(restaurant);
                        break;
                    case 5:
                        searchDish(restaurant);
                        break;
                    case 6:
                        addDishToOrder(restaurant);
                        break;
                    case 7:
                        displayOrder(restaurant);
                        break;
                    case 8:
                        manageEmployees(restaurant);
                        RestaurantFileHandler.saveRestaurant(restaurant);  // Modifié ici
                        break;
                    case 9:
                        finalizeOrder(restaurant);
                        break;
                    case 10:
                        displayOrderHistory(restaurant);
                        break;
                    case 11:
                        System.out.println("Au revoir !");
                        System.exit(0);
                        break;
                    case 12:
                        running = false;
                        break;
                    default:
                        System.out.println("Option invalide !");
                }
                // Sauvegarder après chaque action
                RestaurantFileHandler.saveRestaurant(restaurant);  // Modifié ici
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un numéro valide");
            }
        }
    }

    public static void displayRestaurantDetails(Restaurant restaurant) {  // Changed from private to public
        if (restaurant != null) {
            System.out.println(restaurant.toString());
        } else {
            System.out.println("Erreur : Restaurant non trouvé");
        }
    }
}
