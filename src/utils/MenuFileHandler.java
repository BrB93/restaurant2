package utils;

import models.*;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuFileHandler extends BaseFileHandler {
    public static void saveMenu(PrintWriter writer, Menu menu) {
        writer.println("=== MENU ===");
        for (Dish dish : menu.getDishes()) {
            writer.printf(Locale.FRANCE,
                "%s - %s : %.2f€ (%s, %d kcal, %d min)%n",
                dish.getName(),
                dish.getDescription(),
                dish.getCurrentPrice(),
                dish.getCategory(),
                dish.getCalories(),
                dish.getPreparationTime());
        }
        writer.println();
    }

    public static Dish parseDishFromString(String line) {
        try {
            // Format: "Carbo - pate sauce creme : 17,00€ (Plat, 458 kcal, 15 min)"
            Pattern pattern = Pattern.compile("([^-]+) - ([^:]+) : ([\\d,]+)€ \\(([^,]+), (\\d+) kcal, (\\d+) min\\)");
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                String name = matcher.group(1).trim();
                String description = matcher.group(2).trim();
                double price = Double.parseDouble(matcher.group(3).replace(",", "."));
                String category = matcher.group(4).trim();
                int calories = Integer.parseInt(matcher.group(5));
                int prepTime = Integer.parseInt(matcher.group(6));

                // Créer le plat avec les valeurs par défaut pour les autres champs
                return new Dish(
                    name,
                    description,
                    price,
                    calories,
                    category,
                    "Normale",  // taille portion par défaut
                    true,      // disponible par défaut
                    new ArrayList<>(),  // liste d'ingrédients vide
                    "Non spécifié",  // type de cuisine par défaut
                    prepTime,
                    0.0,      // pas de prix spécial
                    ""        // pas d'URL d'image
                );
            } else {
                System.err.println("Format de plat invalide: " + line);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing d'un plat : " + e.getMessage() + " (ligne: " + line + ")");
            return null;
        }
    }
}
