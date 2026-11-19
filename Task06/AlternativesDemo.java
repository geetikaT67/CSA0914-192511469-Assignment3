package Task06;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Task 06 Alternative Concepts:
 * 1. ArrayBlockingQueue of capacity 5 replacing manual wait()/notifyAll()
 * 2. tryLock() with timeout replacing strict lock-ordering
 */
public class AlternativesDemo {

    // Alternative 1: ArrayBlockingQueue<Integer>
    public static class QueueTokenCounter {
        private final ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

        public void put(int token) throws InterruptedException {
            queue.put(token); // Automatically blocks when full!
        }

        public int take() throws InterruptedException {
            return queue.take(); // Automatically blocks when empty!
        }
    }

    // Alternative 2: tryLock() with Timeout for Deadlock Prevention
    public static class SafeLockTransfer {
        public static boolean transfer(ReentrantLock lockA, ReentrantLock lockB,
                                       double[] balA, double[] balB, double amount, long timeoutMs)
                throws InterruptedException {

            if (lockA.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
                try {
                    if (lockB.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
                        try {
                            if (balA[0] >= amount) {
                                balA[0] -= amount;
                                balB[0] += amount;
                                return true;
                            }
                        } finally {
                            lockB.unlock();
                        }
                    }
                } finally {
                    lockA.unlock();
                }
            }
            return false; // Back off and retry if lock could not be acquired within timeout
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== TASK 06 ALTERNATIVE CONCEPTS DEMO ===");

        // Test ArrayBlockingQueue
        QueueTokenCounter qCounter = new QueueTokenCounter();
        Thread prod = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) qCounter.put(i);
            } catch (InterruptedException ignored) {}
        });

        int[] sum = new int[1];
        Thread cons = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) sum[0] += qCounter.take();
            } catch (InterruptedException ignored) {}
        });

        prod.start();
        cons.start();
        prod.join();
        cons.join();
        System.out.println("ArrayBlockingQueue Collected Sum: " + sum[0] + " (Expected 55)");

        // Test tryLock()
        ReentrantLock l1 = new ReentrantLock();
        ReentrantLock l2 = new ReentrantLock();
        double[] b1 = {500.0};
        double[] b2 = {500.0};
        boolean success = SafeLockTransfer.transfer(l1, l2, b1, b2, 100.0, 100);
        System.out.println("tryLock Transfer Success: " + success + " | New Balances: Bal1=" + b1[0] + ", Bal2=" + b2[0]);
    }
}
