import java.util.*;
import managers.*;
import models.*;
import utils.RestaurantFileHandler;

public class Main {
    private static List<Restaurant> restaurants = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static boolean running = false; // Add this class field

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        Locale.setDefault(Locale.FRANCE);
        RestaurantManager.loadExistingRestaurants(restaurants);
        
        while (true) {
            showMainMenu();
            String input = scanner.nextLine();
            int choice;
            
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un numéro valide !");
                continue;
            }

            switch (choice) {
                case 1:
                    if (restaurants.isEmpty()) {
                        System.out.println("Aucun restaurant n'existe encore !");
                        continue;
                    }
                    selectRestaurant();
                    break;
                case 2:
                    RestaurantManager.createNewRestaurant(restaurants);
                    break;
                case 3:
                    RestaurantManager.deleteRestaurant(restaurants);
                    break;
                case 4:
                    System.out.println("Au revoir !");
                    scanner.close();
                    return;
                default:
                    System.out.println("Option invalide !");
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== Gestion des Restaurants ===");
        System.out.println("1. Sélectionner un restaurant existant");
        System.out.println("2. Créer un nouveau restaurant");
        System.out.println("3. Supprimer un restaurant");
        System.out.println("4. Quitter");
        System.out.print("Votre choix : ");
    }

    private static void selectRestaurant() {
        System.out.println("\n=== Restaurants disponibles ===");
        for (int i = 0; i < restaurants.size(); i++) {
            System.out.println((i + 1) + ". " + restaurants.get(i).toString());
        }
        
        int index = -1;
        boolean validInput = false;
        
        while (!validInput) {
            System.out.print("Sélectionnez un restaurant (1-" + restaurants.size() + ") : ");
            try {
                index = Integer.parseInt(scanner.nextLine()) - 1;
                if (index >= 0 && index < restaurants.size()) {
                    validInput = true;
                } else {
                    System.out.println("Veuillez entrer un numéro entre 1 et " + restaurants.size());
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un numéro valide");
            }
        }
        
        manageRestaurant(restaurants.get(index));
    }

    private static void manageRestaurant(Restaurant restaurant) {
        Restaurant updatedRestaurant = RestaurantFileHandler.loadRestaurant(restaurant.getId());
        if (updatedRestaurant != null) {
            int index = restaurants.indexOf(restaurant);
            if (index != -1) {
                restaurants.set(index, updatedRestaurant);
            }
            restaurant = updatedRestaurant;
        }

        running = true; // Initialize the running variable
        while (running) {
            showRestaurantMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                handleRestaurantChoice(choice, restaurant);
                if (choice != 12) { // Si ce n'est pas "Retour"
                    RestaurantFileHandler.saveRestaurant(restaurant);
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un numéro valide");
            }
        }
    }

    private static void showRestaurantMenu() {
        System.out.println("\n=== Menu principal ===");
        System.out.println("1. Afficher les détails du restaurant");
        System.out.println("2. Ajouter un plat au menu");
        System.out.println("3. Supprimer un plat du menu");
        System.out.println("4. Afficher le menu");
        System.out.println("5. Rechercher un plat");
        System.out.println("6. Ajouter un plat à la commande");
        System.out.println("7. Afficher la commande");
        System.out.println("8. Gérer les employés");
        System.out.println("9. Finaliser la commande");
        System.out.println("10. Historique des commandes");
        System.out.println("11. Quitter");
        System.out.println("12. Retour à la liste des restaurants");
        System.out.print("Votre choix : ");
    }

    private static void handleRestaurantChoice(int choice, Restaurant restaurant) {
        switch (choice) {
            case 1:
                RestaurantManager.displayRestaurantDetails(restaurant);
                break;
            case 2:
                MenuManager.addDishToMenu(restaurant);
                break;
            case 3:
                MenuManager.removeDishFromMenu(restaurant);
                break;
            case 4:
                MenuManager.displayMenu(restaurant);
                break;
            case 5:
                MenuManager.searchDish(restaurant);
                break;
            case 6:
                OrderManager.addDishToOrder(restaurant);
                break;
            case 7:
                OrderManager.displayOrder(restaurant);
                break;
            case 8:
                EmployeeManager.manageEmployees(restaurant);
                break;
            case 9:
                OrderManager.finalizeOrder(restaurant);
                break;
            case 10:
                OrderManager.displayOrderHistory(restaurant);
                break;
            case 11:
                System.exit(0);
                break;
            case 12:
                running = false;
                break;
            default:
                System.out.println("Option invalide !");
                break;
        }
    }
}