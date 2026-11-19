package Task04;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task 04: Alternative Concept - Fixed Thread Pool via ExecutorService.
 * Replaces manual Thread instantiation with managed thread pool.
 */
public class ScannerThreadPoolDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== SCANNER SYSTEM WITH EXECUTORSERVICE (FIXED POOL = 4) ===");

        int totalScripts = 1000;
        int batchSize = 25;
        int totalBatches = totalScripts / batchSize; // 40 batches

        ExecutorService executor = Executors.newFixedThreadPool(4);
        AtomicInteger totalScanned = new AtomicInteger(0);

        long startTime = System.nanoTime();

        for (int b = 1; b <= totalBatches; b++) {
            final int batchId = b;
            executor.submit(() -> {
                for (int s = 0; s < batchSize; s++) {
                    totalScanned.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        boolean completed = executor.awaitTermination(5, TimeUnit.SECONDS);
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        System.out.println("All batches completed cleanly? " + completed);
        System.out.println("Total scripts scanned: " + totalScanned.get() + " / " + totalScripts);
        System.out.println("Execution time: " + elapsedMs + " ms");
        System.out.println("\nAdvantage: Exactly 4 worker threads allocated regardless of number of batches (fixed 4MB thread stack total).");
    }
}
