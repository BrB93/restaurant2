package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BaseFileHandler {
    protected static final String DATA_DIR = "src/data/";

    protected static void writeToFile(String fileName, List<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static List<String> readFromFile(String fileName) {
        List<String> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    protected static void createDataDirectory(String directory) {
        File dataDir = new File(directory);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
}
