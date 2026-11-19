package Task03;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task 03: Version 1 - Manual try-catch-finally closing.
 * Demonstrates how manual closing leads to resource leaks under failure.
 */
public class ManualSalesReportGenerator {

    // Instrumented counter tracking open file handles
    public static final AtomicInteger openHandlesCount = new AtomicInteger(0);

    /**
     * Trackable FileReader wrapper to observe open/close lifecycles
     */
    public static class TrackedFileReader extends FileReader {
        private boolean closed = false;
        private final String fileName;

        public TrackedFileReader(File file) throws IOException {
            super(file);
            this.fileName = file.getName();
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

        public boolean isClosed() {
            return closed;
        }

        public String getFileName() {
            return fileName;
        }
    }

    /**
     * Generates report using manual try-finally.
     * Leaks file handles if parsing fails before close() is invoked on the reader!
     */
    public void generateReport(String inputDir, String summaryOutputFile, int numFiles) throws IOException {
        File dir = new File(inputDir);
        File out = new File(summaryOutputFile);

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(out));
            openHandlesCount.incrementAndGet();
            writer.write("StoreFile,TotalSalesAmount\n");

            for (int i = 1; i <= numFiles; i++) {
                File storeFile = new File(dir, "store_" + i + ".csv");
                if (!storeFile.exists()) continue;

                TrackedFileReader fr = null;
                BufferedReader br = null;
                try {
                    fr = new TrackedFileReader(storeFile);
                    br = new BufferedReader(fr);

                    String line = br.readLine(); // Header
                    double storeTotal = 0.0;

                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        String itemName = parts[0];
                        // Potential failure point: NumberFormatException
                        int qty = Integer.parseInt(parts[1]);
                        double price = Double.parseDouble(parts[2]);
                        storeTotal += (qty * price);
                    }

                    writer.write(storeFile.getName() + "," + String.format("%.2f", storeTotal) + "\n");

                    // In flawed manual version: close is placed at bottom of loop body
                    br.close();
                } catch (NumberFormatException nfe) {
                    // Logging which file caused failure
                    System.err.println("[LOG - MANUAL]: File format corruption detected in: " + storeFile.getName() + " - " + nfe.getMessage());
                    // FLAW: If exception occurs here, br.close() is never called for this file!
                    // The reader remains open in memory!
                    throw new IOException("Aborting report due to corrupt file: " + storeFile.getName(), nfe);
                }
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                    openHandlesCount.decrementAndGet();
                } catch (IOException ignored) {}
            }
        }
    }

    public static void main(String[] args) {
        ManualSalesReportGenerator generator = new ManualSalesReportGenerator();
        try {
            System.out.println("Running ManualSalesReportGenerator on TestData/stores/ (10 files)...");
            generator.generateReport("TestData/stores", "TestData/manual_summary.csv", 10);
            System.out.println("Report generated successfully.");
        } catch (Exception e) {
            System.err.println("Execution failed: " + e.getMessage());
        } finally {
            System.out.println("Final Open File Handles (Manual Version): " + openHandlesCount.get());
        }
    }
}
