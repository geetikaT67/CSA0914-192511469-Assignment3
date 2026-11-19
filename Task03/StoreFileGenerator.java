package Task03;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Generates sample store sales files for testing Task 03.
 */
public class StoreFileGenerator {

    public static void generateFiles(String folderPath, int totalFiles, int corruptFileIndex) throws IOException {
        File dir = new File(folderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (int i = 1; i <= totalFiles; i++) {
            File file = new File(dir, "store_" + i + ".csv");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ItemName,Quantity,UnitPrice\n");
                if (i == corruptFileIndex) {
                    // Inject malformed data that will trigger a NumberFormatException part-way through processing
                    writer.write("Stationery_Pen,10,15.50\n");
                    writer.write("Stationery_Notebook,CORRUPT_QTY,45.00\n");
                    writer.write("Stationery_Folder,5,25.00\n");
                } else {
                    writer.write("Stationery_Pen,100,15.50\n");
                    writer.write("Stationery_Notebook,50,45.00\n");
                    writer.write("Stationery_Folder,30,25.00\n");
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            generateFiles("TestData/stores", 10, 4); // File 4 is corrupt
            System.out.println("Generated 10 store sales files in TestData/stores/ (store_4.csv is corrupt)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
