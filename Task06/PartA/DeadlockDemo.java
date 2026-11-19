package Task06.PartA;

/**
 * Task 06 Part A: Deadlock Demonstration in Hostel Wallet Transfers.
 * Demonstrates how acquiring locks in arbitrary order produces deadlock.
 */
public class DeadlockDemo {

    public static void transfer(Wallet from, Wallet to, double amount) {
        String threadName = Thread.currentThread().getName();
        // Lock 1: Source wallet
        synchronized (from) {
            System.out.println("  [" + threadName + "] ACQUIRED lock on " + from + ". Waiting for lock on " + to + "...");
            try {
                // Micro-pause to guarantee that the other thread acquires its first lock
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}

            // Lock 2: Destination wallet
            synchronized (to) {
                System.out.println("  [" + threadName + "] ACQUIRED lock on " + to + ". Transferring Rs " + amount + "...");
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== PART A: DEADLOCK REPRODUCTION DEMO ===");
        Wallet walletA = new Wallet(101, 1000.0);
        Wallet walletB = new Wallet(102, 1000.0);

        // Thread 1: Transfers from Wallet A -> Wallet B (Locks A then B)
        Thread t1 = new Thread(() -> transfer(walletA, walletB, 200.0), "TransferThread-1(A->B)");

        // Thread 2: Transfers from Wallet B -> Wallet A (Locks B then A)
        Thread t2 = new Thread(() -> transfer(walletB, walletA, 300.0), "TransferThread-2(B->A)");

        t1.start();
        t2.start();

        // Deadlock Watchdog: Inspects states after 500ms
        Thread.sleep(500);
        System.out.println("\n[WATCHDOG INSPECTION]:");
        System.out.println("  " + t1.getName() + " State: " + t1.getState());
        System.out.println("  " + t2.getName() + " State: " + t2.getState());

        if (t1.getState() == Thread.State.BLOCKED && t2.getState() == Thread.State.BLOCKED) {
            System.out.println(">>> DEADLOCK CONFIRMED: Both threads are permanently BLOCKED waiting on each other's locks!");
        }

        // Interrupt / terminate demo cleanly
        t1.interrupt();
        t2.interrupt();
        System.out.println("Exiting DeadlockDemo cleanly.");
        System.exit(0);
    }
}
