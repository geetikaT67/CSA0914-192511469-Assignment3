package Task06.PartB;

/**
 * Task 06 Part B Demonstration: Why notify() is unsafe compared to notifyAll().
 * Demonstrates signal starvation / deadlock when notify() wakes the wrong thread type.
 */
public class NotifyVsNotifyAllExperiment {

    static class FlawedTokenCounter {
        private final int[] buffer = new int[2];
        private int count = 0;

        public synchronized void put(int val) throws InterruptedException {
            while (count == buffer.length) {
                wait();
            }
            buffer[count++] = val;
            System.out.println("  [Producer] Put token " + val + ". Count=" + count);
            notify(); // FLAW: might wake another producer instead of consumer
        }

        public synchronized int take() throws InterruptedException {
            while (count == 0) {
                wait();
            }
            int val = buffer[--count];
            System.out.println("  [Consumer] Took token " + val + ". Count=" + count);
            notify(); // FLAW: might wake another consumer instead of producer
            return val;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== NOTIFY() VS NOTIFYALL() EXPERIMENT ===");
        System.out.println("Theoretical & Empirical Explanation:");
        System.out.println("1. With multiple consumers (C1, C2) and producer (P):");
        System.out.println("   - If buffer is empty, C1 and C2 both enter wait().");
        System.out.println("   - P puts 1 token and calls notify(). JVM wakes C1.");
        System.out.println("   - C1 consumes token (count=0). C1 calls notify().");
        System.out.println("   - CRITICAL FLAW: Instead of waking P, JVM wakes C2!");
        System.out.println("   - C2 wakes up, checks 'while(count == 0)' -> buffer is empty! C2 calls wait() again.");
        System.out.println("   - Now P is waiting, C1 is waiting, C2 is waiting -> COMPLETE SIGNAL DEADLOCK!");
        System.out.println("2. notifyAll() wakes ALL waiting threads on the monitor:");
        System.out.println("   - Both P and consumers wake up; the eligible thread makes progress while others re-check their condition loop.");
    }
}
