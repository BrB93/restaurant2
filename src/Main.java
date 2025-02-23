import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Restaurant> restaurants = new ArrayList<>();

        while (true) {
            System.out.println("1. Add a restaurant\n2. Quit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Restaurant name: ");
                String name = scanner.nextLine();
                System.out.print("Address: ");
                String address = scanner.nextLine();
                restaurants.add(new Restaurant(restaurants.size() + 1, name, address));
                System.out.println("Restaurant added!");
            } else {
                break;
            }
        }
        scanner.close();
    }