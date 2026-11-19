package Task03;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task 03: Version 2 - try-with-resources.
 * Guarantees prompt, automatic closure of all resources in reverse order of declaration,
 * even when runtime exceptions or corrupt file data occur.
 */
public class TryWithResourcesSalesReportGenerator {

    // Instrumented counter tracking open file handles
    public static final AtomicInteger openHandlesCount = new AtomicInteger(0);

    public static class TrackedFileReader extends java.io.FileReader {
        private boolean closed = false;

        public TrackedFileReader(File file) throws IOException {
            super(file);
            openHandlesCount.incrementAndGet();
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                super.close();
                closed = true;
                openHandlesCount.decrementAndGet();
            }
        }
    }

    public static class TrackedFileWriter extends java.io.FileWriter {
        private boolean closed = false;

        public TrackedFileWriter(File file) throws IOException {
            super(file);
            openHandlesCount.incrementAndGet();
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                super.close();
                closed = true;
                openHandlesCount.decrementAndGet();
            }
        }
    }

    public void generateReport(String inputDir, String summaryOutputFile, int numFiles) throws IOException {
        File dir = new File(inputDir);
        File out = new File(summaryOutputFile);

        // Try-with-resources on summary output writer
        try (TrackedFileWriter fw = new TrackedFileWriter(out);
             BufferedWriter writer = new BufferedWriter(fw)) {

            writer.write("StoreFile,TotalSalesAmount\n");

            for (int i = 1; i <= numFiles; i++) {
                File storeFile = new File(dir, "store_" + i + ".csv");
                if (!storeFile.exists()) continue;

                // Try-with-resources on each individual store file reader
                try (TrackedFileReader fr = new TrackedFileReader(storeFile);
                     BufferedReader br = new BufferedReader(fr)) {

                    String line = br.readLine(); // Header
                    double storeTotal = 0.0;

                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        String itemName = parts[0];
                        int qty = Integer.parseInt(parts[1]);
                        double price = Double.parseDouble(parts[2]);
                        storeTotal += (qty * price);
                    }

                    writer.write(storeFile.getName() + "," + String.format("%.2f", storeTotal) + "\n");

                } catch (NumberFormatException nfe) {
                    System.err.println("[LOG - TWR]: Format error in " + storeFile.getName() + " - " + nfe.getMessage());
                    // Even if we propagate or rethrow, try-with-resources has ALREADY closed the file reader!
                    throw new IOException("Aborting report due to corrupt file: " + storeFile.getName(), nfe);
                }
            }
        }
    }

    public static void main(String[] args) {
        TryWithResourcesSalesReportGenerator generator = new TryWithResourcesSalesReportGenerator();
        try {
            System.out.println("Running TryWithResourcesSalesReportGenerator on TestData/stores/ (10 files)...");
            generator.generateReport("TestData/stores", "TestData/twr_summary.csv", 10);
            System.out.println("Report generated successfully.");
        } catch (Exception e) {
            System.err.println("Execution failed: " + e.getMessage());
        } finally {
            System.out.println("Final Open File Handles (Try-With-Resources Version): " + openHandlesCount.get());
        }
    }
}
