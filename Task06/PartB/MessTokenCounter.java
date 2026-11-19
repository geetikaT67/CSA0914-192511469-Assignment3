package Task06.PartB;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Task 06 Part B: Mess Token Counter (Bounded Buffer Producer-Consumer).
 * Capacity: exactly 5 tokens.
 * Enforces wait() inside a while-loop and notifyAll().
 */
public class MessTokenCounter {

    private final int[] buffer = new int[5]; // Capacity = 5
    private int count = 0;
    private int in = 0;
    private int out = 0;

    // Buffer tracking history
    private final List<Integer> history = Collections.synchronizedList(new ArrayList<>());
    private boolean invariantViolated = false;

    public synchronized void putToken(int token) throws InterruptedException {
        // Must be in while loop to protect against spurious wakeups and state invalidation
        while (count == buffer.length) {
            wait();
        }

        buffer[in] = token;
        in = (in + 1) % buffer.length;
        count++;

        recordState("PRODUCE (Token " + token + ")");
        notifyAll(); // Wake all consumers and producers
    }

    public synchronized int takeToken() throws InterruptedException {
        while (count == 0) {
            wait();
        }

        int token = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        recordState("CONSUME (Token " + token + ")");
        notifyAll();
        return token;
    }

    private void recordState(String action) {
        history.add(count);
        if (count < 0 || count > 5) {
            invariantViolated = true;
            System.err.println("CRITICAL FAULT: Buffer invariant violated! Count = " + count);
        }
    }

    public synchronized int getCount() { return count; }
    public boolean isInvariantViolated() { return invariantViolated; }
    public List<Integer> getHistory() { return history; }

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("=== PART B: MESS TOKEN COUNTER (CAPACITY = 5) ===");
        MessTokenCounter counter = new MessTokenCounter();
        int totalTokens = 50;
        int numConsumers = 3;

        // Producer: Mess Cook
        Thread cook = new Thread(() -> {
            try {
                for (int t = 1; t <= totalTokens; t++) {
                    counter.putToken(t);
                    Thread.sleep(2);
                }
            } catch (InterruptedException ignored) {}
        }, "MessCook-Producer");

        // Consumers: Students
        Thread[] students = new Thread[numConsumers];
        int tokensPerConsumer = totalTokens / numConsumers;
        int remainder = totalTokens % numConsumers;

        for (int i = 0; i < numConsumers; i++) {
            final int cid = i + 1;
            final int target = tokensPerConsumer + (i == 0 ? remainder : 0);
            students[i] = new Thread(() -> {
                try {
                    int taken = 0;
                    while (taken < target) {
                        counter.takeToken();
                        taken++;
                        Thread.sleep(3);
                    }
                } catch (InterruptedException ignored) {}
            }, "StudentConsumer-" + cid);
        }

        cook.start();
        for (Thread s : students) s.start();

        cook.join();
        for (Thread s : students) s.join();

        System.out.println("Processing completed.");
        System.out.println("Total operations recorded: " + counter.getHistory().size());
        System.out.println("Buffer invariant violated? " + counter.isInvariantViolated() + " (Always between 0 and 5)");

        // Save CSV tracking data
        try (FileWriter fw = new FileWriter("ExperimentalData/task06_buffer_tracking.csv")) {
            fw.write("Step,BufferSize,Capacity\n");
            List<Integer> h = counter.getHistory();
            for (int i = 0; i < h.size(); i++) {
                fw.write(String.format("%d,%d,5\n", i + 1, h.get(i)));
            }
            System.out.println("Saved buffer tracking data to ExperimentalData/task06_buffer_tracking.csv");
        }
    }
}
