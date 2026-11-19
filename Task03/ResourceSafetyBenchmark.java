package Task03;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Task 03: Experimental Benchmark & Resource Verification
 * 1. Verifies that both versions produce identical reports for valid inputs.
 * 2. Compares open file handles remaining under partial failure.
 * 3. Measures execution times and exports data to CSV.
 */
public class ResourceSafetyBenchmark {

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("   TASK 03: RESOURCE SAFETY AND DEBUGGING BENCHMARK      ");
        System.out.println("=========================================================");

        String validDir = "TestData/stores_valid";
        String failDir = "TestData/stores_fail";

        try {
            // 1. Generate purely valid data (5 files)
            StoreFileGenerator.generateFiles(validDir, 5, -1);
            // 2. Generate failing data (10 files, file 4 is corrupt)
            StoreFileGenerator.generateFiles(failDir, 10, 4);

            // --- PART 1: Identical Report Verification on Valid Input ---
            System.out.println("\n--- Step 1: Verification of Identical Output on Valid Input ---");
            ManualSalesReportGenerator manualGen = new ManualSalesReportGenerator();
            TryWithResourcesSalesReportGenerator twrGen = new TryWithResourcesSalesReportGenerator();

            manualGen.generateReport(validDir, "TestData/manual_valid_summary.csv", 5);
            twrGen.generateReport(validDir, "TestData/twr_valid_summary.csv", 5);

            List<String> manualLines = Files.readAllLines(new File("TestData/manual_valid_summary.csv").toPath());
            List<String> twrLines = Files.readAllLines(new File("TestData/twr_valid_summary.csv").toPath());

            boolean identical = manualLines.equals(twrLines);
            System.out.println("Are reports from both versions identical? " + (identical ? "YES (VERIFIED)" : "NO (MISMATCH)"));
            if (identical) {
                System.out.println("Report Content:");
                manualLines.forEach(l -> System.out.println("  " + l));
            }

            // --- PART 2: Resource Leak Comparison on Failing Input ---
            System.out.println("\n--- Step 2: Open File Handle Comparison Under Failure ---");

            // Reset counters
            ManualSalesReportGenerator.openHandlesCount.set(0);
            TryWithResourcesSalesReportGenerator.openHandlesCount.set(0);

            long startManual = System.nanoTime();
            int manualOpenHandles = 0;
            try {
                manualGen.generateReport(failDir, "TestData/manual_fail_summary.csv", 10);
            } catch (Exception e) {
                manualOpenHandles = ManualSalesReportGenerator.openHandlesCount.get();
                System.out.println("Manual version threw exception: " + e.getMessage());
                System.out.println("Leaked Open File Handles in Manual Version: " + manualOpenHandles);
            }
            long timeManual = System.nanoTime() - startManual;

            long startTwr = System.nanoTime();
            int twrOpenHandles = 0;
            try {
                twrGen.generateReport(failDir, "TestData/twr_fail_summary.csv", 10);
            } catch (Exception e) {
                twrOpenHandles = TryWithResourcesSalesReportGenerator.openHandlesCount.get();
                System.out.println("Try-with-resources threw exception: " + e.getMessage());
                System.out.println("Leaked Open File Handles in TWR Version:    " + twrOpenHandles);
            }
            long timeTwr = System.nanoTime() - startTwr;

            // --- PART 3: Export CSV Experimental Data ---
            try (FileWriter fw = new FileWriter("ExperimentalData/task03_resource_comparison.csv")) {
                fw.write("Version,TotalFiles,FailingFileIndex,Status,OpenHandlesRemaining,TimeMs\n");
                fw.write(String.format("ManualClose,10,4,FAILED,%d,%.3f\n", manualOpenHandles, timeManual / 1e6));
                fw.write(String.format("TryWithResources,10,4,FAILED,%d,%.3f\n", twrOpenHandles, timeTwr / 1e6));
                System.out.println("\nExported experimental comparison to ExperimentalData/task03_resource_comparison.csv");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
