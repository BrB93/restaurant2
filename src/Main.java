import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.*;  // Import all models
import utils.FileHandler;

public class Main {
    private static List<Restaurant> restaurants = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static final String DATA_DIR = "src/data/";

    public static void main(String[] args) {
        loadExistingRestaurants();
        while (true) {
            showMainMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    if (restaurants.isEmpty()) {
                        System.out.println("Aucun restaurant n'existe encore !");
                        continue;
                    }
                    selectRestaurant();
                    break;
                case 2:
                    createNewRestaurant();
                    break;
                case 3:
                    System.out.println("Au revoir !");
                    scanner.close();
                    return;
                default:
                    System.out.println("Option invalide !");
            }
        }
    }

    private static void loadExistingRestaurants() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            return;
        }

        File[] files = dataDir.listFiles((dir, name) -> name.startsWith("restaurant_") && name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                try {
                    // Extraire l'ID du nom du fichier (restaurant_X.txt)
                    int id = Integer.parseInt(file.getName().split("_")[1].split("\\.")[0]);
                    List<String> lines = Files.readAllLines(file.toPath());
                    
                    // Lire les informations de base du restaurant
                    String name = "";
                    String address = "";
                    for (String line : lines) {
                        if (line.startsWith("Name: ")) {
                            name = line.substring(6);
                        } else if (line.startsWith("Address: ")) {
                            address = line.substring(9);
                        }
                    }
                    
                    if (!name.isEmpty() && !address.isEmpty()) {
                        Restaurant restaurant = new Restaurant(id, name, address);
                        
                        // Charger le menu
                        boolean inMenuSection = false;
                        boolean inEmployeesSection = false;
                        
                        for (String line : lines) {
                            if (line.equals("=== MENU ===")) {
                                inMenuSection = true;
                                continue;
                            } else if (line.equals("=== EMPLOYEES ===")) {
                                inMenuSection = false;
                                inEmployeesSection = true;
                                continue;
                            } else if (line.equals("=== ORDERS ===")) {
                                inEmployeesSection = false;
                                continue;
                            }

                            if (inMenuSection && line.startsWith("Menu: [")) {
                                String menuContent = line.substring(7, line.length() - 1);
                                if (!menuContent.isEmpty()) {
                                    String[] dishes = menuContent.split("], \\[");
                                    for (String dishStr : dishes) {
                                        // Enlever les crochets restants
                                        dishStr = dishStr.replace("[", "").replace("]", "");
                                        
                                        // Format attendu: "nom - description : prix€ (catégorie, calories kcal, temps min)"
                                        String[] mainParts = dishStr.split(" - ");
                                        String dishName = mainParts[0];  // Changé name en dishName
                                        
                                        String[] descParts = mainParts[1].split(" : ");
                                        String description = descParts[0];
                                        
                                        // Parser le prix et les détails entre parenthèses
                                        String[] priceParts = descParts[1].split(" \\(");
                                        double price = Double.parseDouble(priceParts[0].replace("€", "").replace(",", "."));
                                        
                                        // Parser les détails entre parenthèses
                                        String details = priceParts[1].replace(")", "");
                                        String[] detailParts = details.split(", ");
                                        
                                        String category = detailParts[0];
                                        int calories = Integer.parseInt(detailParts[1].replace(" kcal", ""));
                                        int prepTime = Integer.parseInt(detailParts[2].replace(" min", ""));
                                        
                                        // Créer le plat avec les informations récupérées
                                        Dish dish = new Dish(dishName, description, price, calories,  // Utilisation de dishName
                                                           category, "Normale", true,
                                                           new ArrayList<>(), "Non spécifié",
                                                           prepTime, 0.0, "");
                                        
                                        restaurant.getMenu().addDish(dish);
                                    }
                                }
                            }
                            
                            if (inEmployeesSection && !line.isEmpty() && !line.equals("=== EMPLOYEES ===")) {
                                // Parser les employés
                                // TODO: Implémenter le chargement des employés
                            }
                        }
                        
                        restaurants.add(restaurant);
                        System.out.println("Restaurant chargé : " + restaurant.getName());
                    }
                } catch (Exception e) {
                    System.out.println("Erreur lors du chargement d'un restaurant : " + e.getMessage());
                }
            }
        }
        
        if (!restaurants.isEmpty()) {
            System.out.println("\n" + restaurants.size() + " restaurant(s) chargé(s).");
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== Gestion des Restaurants ===");
        System.out.println("1. Sélectionner un restaurant existant");
        System.out.println("2. Créer un nouveau restaurant");
        System.out.println("3. Quitter");
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

    private static void createNewRestaurant() {
        System.out.print("Nom du restaurant : ");
        String name = scanner.nextLine();
        System.out.print("Adresse : ");
        String address = scanner.nextLine();
        
        Restaurant newRestaurant = new Restaurant(restaurants.size() + 1, name, address);
        restaurants.add(newRestaurant);
        FileHandler.saveRestaurant(newRestaurant);
        System.out.println("Restaurant ajouté et sauvegardé !");
    }

    private static void manageRestaurant(Restaurant restaurant) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Menu principal ===");
            System.out.println("1. Ajouter un plat au menu");
            System.out.println("2. Supprimer un plat du menu");
            System.out.println("3. Afficher le menu");
            System.out.println("4. Rechercher un plat");
            System.out.println("5. Ajouter un plat à la commande");
            System.out.println("6. Afficher la commande");
            System.out.println("7. Ajouter un employé");
            System.out.println("8. Quitter");
            System.out.println("9. Retour à la liste des restaurants");
            System.out.print("Votre choix : ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        addDishToMenu(restaurant);
                        break;
                    case 2:
                        removeDishFromMenu(restaurant);
                        break;
                    case 3:
                        displayMenu(restaurant);
                        break;
                    case 4:
                        searchDish(restaurant);
                        break;
                    case 5:
                        addDishToOrder(restaurant);
                        break;
                    case 6:
                        displayOrder(restaurant);
                        break;
                    case 7:
                        addEmployee(restaurant);
                        break;
                    case 8:
                        System.out.println("Au revoir !");
                        System.exit(0);
                        break;
                    case 9:
                        running = false;
                        break;
                    default:
                        System.out.println("Option invalide !");
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un numéro valide");
            }
            FileHandler.saveRestaurant(restaurant);
        }
    }

    private static void addDishToMenu(Restaurant restaurant) {
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

    private static void removeDishFromMenu(Restaurant restaurant) {
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

    private static void displayMenu(Restaurant restaurant) {
        System.out.println("\n=== Menu du restaurant ===");
        System.out.println(restaurant.getMenu().toString());
    }

    private static void searchDish(Restaurant restaurant) {
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

    private static void addDishToOrder(Restaurant restaurant) {
        List<Dish> menuDishes = restaurant.getMenu().getDishes();
        if (menuDishes.isEmpty()) {
            System.out.println("Le menu est vide ! Impossible de créer une commande.");
            return;
        }

        // Afficher ou créer une commande
        List<Order> activeOrders = restaurant.getOrders().stream()
            .filter(o -> "En cours".equals(o.getStatus()))
            .toList();

        Order currentOrder;
        if (!activeOrders.isEmpty()) {
            System.out.println("\nCommandes en cours :");
            for (int i = 0; i < activeOrders.size(); i++) {
                System.out.println((i + 1) + ". " + activeOrders.get(i));
            }
            System.out.println((activeOrders.size() + 1) + ". Créer une nouvelle commande");
            
            System.out.print("Choisissez une commande : ");
            int orderChoice = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (orderChoice == activeOrders.size()) {
                currentOrder = restaurant.createNewOrder();
                System.out.println("Nouvelle commande créée.");
            } else if (orderChoice >= 0 && orderChoice < activeOrders.size()) {
                currentOrder = activeOrders.get(orderChoice);
            } else {
                System.out.println("Choix invalide !");
                return;
            }
        } else {
            currentOrder = restaurant.createNewOrder();
            System.out.println("Nouvelle commande créée.");
        }

        // Afficher le menu et sélectionner un plat
        System.out.println("\n=== Menu disponible ===");
        for (int i = 0; i < menuDishes.size(); i++) {
            System.out.println((i + 1) + ". " + menuDishes.get(i));
        }

        try {
            System.out.print("Sélectionnez un plat (1-" + menuDishes.size() + ") : ");
            int dishChoice = Integer.parseInt(scanner.nextLine()) - 1;

            if (dishChoice >= 0 && dishChoice < menuDishes.size()) {
                Dish selectedDish = menuDishes.get(dishChoice);
                currentOrder.addDish(selectedDish);
                System.out.println("Plat ajouté à la commande !");
                System.out.println("\nCommande actuelle :");
                System.out.println(currentOrder);
            } else {
                System.out.println("Numéro de plat invalide !");
            }
        } catch (NumberFormatException e) {
            System.out.println("Veuillez entrer un numéro valide.");
        }
    }

    private static void displayOrder(Restaurant restaurant) {
        System.out.println("\n=== Commandes en cours ===");
        for (Order order : restaurant.getOrders()) {
            System.out.println(order.toString());
        }
    }

    private static void addEmployee(Restaurant restaurant) {
        System.out.print("Prénom : ");
        String firstName = scanner.nextLine();
        System.out.print("Nom : ");
        String lastName = scanner.nextLine();
        System.out.print("Rôle : ");
        String role = scanner.nextLine();
        System.out.print("Salaire : ");
        double salary = Double.parseDouble(scanner.nextLine());
        System.out.print("Date d'embauche (YYYY-MM-DD) : ");
        String hireDate = scanner.nextLine();

        Employee newEmployee = new Employee(
            restaurant.getEmployees().size() + 1,
            firstName,
            lastName,
            role,
            hireDate,
            salary
        );
        restaurant.addEmployee(newEmployee);
        System.out.println("Employé ajouté !");
    }

    private static void manageMenu(Restaurant restaurant) {
        System.out.println("\n=== Gestion du menu ===");
        // TODO: Implémenter la gestion du menu
        System.out.println("Fonctionnalité à venir...");
    }

    private static void manageEmployees(Restaurant restaurant) {
        System.out.println("\n=== Gestion des employés ===");
        // TODO: Implémenter la gestion des employés
        System.out.println("Fonctionnalité à venir...");
    }

    private static void manageOrders(Restaurant restaurant) {
        System.out.println("\n=== Gestion des commandes ===");
        // TODO: Implémenter la gestion des commandes
        System.out.println("Fonctionnalité à venir...");
    }
}