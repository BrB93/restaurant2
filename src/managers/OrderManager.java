package managers;

import models.*;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

public class OrderManager {
    private static Scanner scanner = new Scanner(System.in);

    public static void addDishToOrder(Restaurant restaurant) {
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

    public static void displayOrder(Restaurant restaurant) {
        System.out.println("\n=== Commandes en cours ===");
        for (Order order : restaurant.getOrders()) {
            System.out.println(order.toString());
        }
    }

    public static void finalizeOrder(Restaurant restaurant) {
        List<Order> activeOrders = restaurant.getOrders().stream()
            .filter(o -> "En cours".equals(o.getStatus()))
            .toList();

        if (activeOrders.isEmpty()) {
            System.out.println("Aucune commande en cours !");
            return;
        }

        System.out.println("\n=== Commandes en cours ===");
        for (int i = 0; i < activeOrders.size(); i++) {
            System.out.println((i + 1) + ". " + activeOrders.get(i));
        }

        try {
            System.out.print("Sélectionnez la commande à finaliser (1-" + activeOrders.size() + ") : ");
            int choice = Integer.parseInt(scanner.nextLine()) - 1;

            if (choice >= 0 && choice < activeOrders.size()) {
                Order orderToFinalize = activeOrders.get(choice);
                System.out.println("\nRécapitulatif de la commande :");
                System.out.println(orderToFinalize);
                
                System.out.print("\nMode de paiement (CB/Espèces/Chèque) : ");
                String paymentMethod = scanner.nextLine();
                
                System.out.print("Confirmer le paiement de " + 
                    String.format("%.2f", orderToFinalize.getTotal()) + 
                    " euros par " + paymentMethod + " ? (oui/non) : ");
                
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("oui")) {
                    orderToFinalize.setStatus("Terminée");
                    System.out.println("Commande finalisée avec succès !");
                    System.out.println("Bon appétit !");
                } else {
                    System.out.println("Paiement annulé.");
                }
            } else {
                System.out.println("Numéro de commande invalide !");
            }
        } catch (NumberFormatException e) {
            System.out.println("Veuillez entrer un numéro valide.");
        }
    }

    public static void displayOrderHistory(Restaurant restaurant) {
        List<Order> completedOrders = restaurant.getOrders().stream()
            .filter(o -> "Terminée".equals(o.getStatus()))
            .toList();

        if (completedOrders.isEmpty()) {
            System.out.println("Aucune commande finalisée !");
            return;
        }

        double totalRevenue = 0;
        System.out.println("\n=== Historique des commandes ===");
        
        // Utilisation de la bonne locale pour l'affichage des montants
        Locale.setDefault(Locale.FRANCE);
        
        for (Order order : completedOrders) {
            // Utilisation de String.format avec la locale française
            System.out.printf("\nOrder #%d (%s) - Total: %.2f euros - Status: %s\n",
                order.getOrderNumber(),
                order.getOrderTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                order.getTotal(),
                order.getStatus());
                
            for (Dish dish : order.getDishes()) {
                System.out.printf("  - %s - %s : %.2f euros\n", 
                    dish.getName(),
                    dish.getDescription(),
                    dish.getCurrentPrice());
            }
            totalRevenue += order.getTotal();
        }
        
        System.out.println("\nRécapitulatif :");
        System.out.println("Nombre de commandes finalisées : " + completedOrders.size());
        System.out.printf("Chiffre d'affaires total : %.2f euros\n", totalRevenue);
        System.out.printf("Moyenne par commande : %.2f euros\n", totalRevenue / completedOrders.size());
    }
}
