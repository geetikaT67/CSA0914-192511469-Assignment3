package Task07;

import Task07.models.ProcessingResult;
import Task07.models.ProcessingSummary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Task 07: High-Throughput Fault-Tolerant Result Processor.
 * Coordinates batch execution using ExecutorService, Callable, and Future.
 */
public class ResultProcessor {

    public static ProcessingSummary processBatch(List<String> filePaths, int threadPoolSize)
            throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        ProcessingSummary summary = new ProcessingSummary();

        // Concurrent collection: Thread-safe queue collecting individual results
        ConcurrentLinkedQueue<ProcessingResult> resultsQueue = new ConcurrentLinkedQueue<>();
        List<Future<ProcessingResult>> futures = new ArrayList<>(filePaths.size());

        for (String path : filePaths) {
            String studentId = new File(path).getName().replace(".csv", "").replace("score_", "");
            ScoreFileProcessorTask task = new ScoreFileProcessorTask(path, studentId);
            futures.add(executor.submit(task));
        }

        // Aggregate results from futures
        for (Future<ProcessingResult> future : futures) {
            ProcessingResult res = future.get();
            resultsQueue.add(res);
            summary.record(res);
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        return summary;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=========================================================");
        System.out.println("   TASK 07: CONCURRENT FAULT-TOLERANT PROCESSOR          ");
        System.out.println("=========================================================");

        // --- STEP 1: Verify on Hand-Checked Dataset (20 files) ---
        System.out.println("\n--- Step 1: Hand-Checked Dataset Ground-Truth Verification ---");
        List<String> handCheckedFiles = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            handCheckedFiles.add("TestData/hand_checked/score_CHK" + String.format("%03d", i) + ".csv");
        }

        ProcessingSummary handSummary = processBatch(handCheckedFiles, 4);
        System.out.println("Actual Hand-Checked Summary: " + handSummary);
        System.out.println("Expected Baseline: Total=20 | Valid=15 (Passed=12, Failed=3) | Corrupt=3 | Missing=2 | Unhandled=0");

        boolean handCheckPassed = (handSummary.getTotalFiles() == 20)
                && (handSummary.getSuccessCount() == 15)
                && (handSummary.getPassedCount() == 12)
                && (handSummary.getFailedCount() == 3)
                && (handSummary.getCorruptCount() == 3)
                && (handSummary.getMissingCount() == 2)
                && (handSummary.getUnhandledFailures() == 0);

        System.out.println("Ground Truth Accuracy Match: " + (handCheckPassed ? "100% PERFECT MATCH" : "FAILED"));

        // --- STEP 2: 10,000 File Scalability Benchmark (1, 2, 4, 8 Threads) ---
        System.out.println("\n--- Step 2: 10,000 Files Scalability Benchmark across Thread Pools ---");
        int total10k = 10_000;
        List<String> workload10k = new ArrayList<>(total10k);
        for (int i = 1; i <= total10k; i++) {
            workload10k.add("TestData/score_files/score_STU" + String.format("%06d", i) + ".csv");
        }

        int[] poolSizes = {1, 2, 4, 8};
        try (FileWriter fw = new FileWriter("ExperimentalData/task07_thread_pool_benchmark.csv")) {
            fw.write("ThreadPoolSize,TotalFiles,ProcessedValid,Passed,Failed,Corrupt,Missing,UnhandledFailures,ExecutionTimeMs,ThroughputFilesPerSec\n");

            for (int p : poolSizes) {
                System.out.printf("\nExecuting with Thread Pool Size = %d ...\n", p);
                long startTime = System.nanoTime();
                ProcessingSummary s = processBatch(workload10k, p);
                long durationMs = (System.nanoTime() - startTime) / 1_000_000;
                double throughput = (total10k / (durationMs / 1000.0));

                System.out.printf("  Completed in %d ms | Throughput: %.1f files/sec\n", durationMs, throughput);
                System.out.printf("  Summary: %s\n", s);

                fw.write(String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%.1f\n",
                    p, s.getTotalFiles(), s.getSuccessCount(), s.getPassedCount(), s.getFailedCount(),
                    s.getCorruptCount(), s.getMissingCount(), s.getUnhandledFailures(), durationMs, throughput));
            }
            System.out.println("\nSaved thread-pool scalability benchmarks to ExperimentalData/task07_thread_pool_benchmark.csv");
        }
    }
}
