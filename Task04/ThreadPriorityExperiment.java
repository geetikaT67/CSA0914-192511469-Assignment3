package Task04;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Task 04: Thread Priority Experiment
 * Compares execution and completion order with varying priorities:
 * Thread.MIN_PRIORITY (1) vs Thread.NORM_PRIORITY (5) vs Thread.MAX_PRIORITY (10).
 */
public class ThreadPriorityExperiment {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== THREAD PRIORITY EXPERIMENT ===");
        List<String> finishOrder = Collections.synchronizedList(new ArrayList<>());

        Thread tLow = new Thread(() -> {
            doWork(100_000_000);
            finishOrder.add("LowPriorityThread(1)");
        }, "LowPriorityThread");
        tLow.setPriority(Thread.MIN_PRIORITY); // Priority 1

        Thread tNorm = new Thread(() -> {
            doWork(100_000_000);
            finishOrder.add("NormPriorityThread(5)");
        }, "NormPriorityThread");
        tNorm.setPriority(Thread.NORM_PRIORITY); // Priority 5

        Thread tHigh = new Thread(() -> {
            doWork(100_000_000);
            finishOrder.add("HighPriorityThread(10)");
        }, "HighPriorityThread");
        tHigh.setPriority(Thread.MAX_PRIORITY); // Priority 10

        System.out.println("Starting threads simultaneously with priorities: High(10), Norm(5), Low(1)...");
        tLow.start();
        tNorm.start();
        tHigh.start();

        tLow.join();
        tNorm.join();
        tHigh.join();

        System.out.println("Observed Finish Order: " + finishOrder);
        System.out.println("Note on Modern OS Schedulers: Thread priorities are advisory hints to the OS kernel.");
        System.out.println("They do NOT guarantee strict deterministic ordering due to OS time-slicing and priority boost algorithms.");
    }

    private static void doWork(int iterations) {
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            sum += (i * 3L) ^ 0x55;
        }
    }
}
