package Task06.PartA;

/**
 * Task 06 Part A: Deadlock-Free Hostel Wallet Transfers.
 * Implements strict global lock-ordering based on account ID.
 * Breaks Coffman's 4th condition: Circular Wait.
 */
public class DeadlockFreeDemo {

    public static void safeTransfer(Wallet from, Wallet to, double amount) {
        // Enforce consistent global lock hierarchy: lower account ID locked first
        Wallet firstLock = (from.getAccountId() < to.getAccountId()) ? from : to;
        Wallet secondLock = (from.getAccountId() < to.getAccountId()) ? to : from;

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (from.getBalance() >= amount) {
                    from.withdraw(amount);
                    to.deposit(amount);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== PART A: DEADLOCK-FREE LOCK-ORDERING DEMO ===");
        Wallet walletA = new Wallet(101, 1000.0);
        Wallet walletB = new Wallet(102, 1000.0);

        int totalTransfers = 500;
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < totalTransfers; i++) {
                safeTransfer(walletA, walletB, 1.0);
            }
        }, "SafeTransfer-A->B");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < totalTransfers; i++) {
                safeTransfer(walletB, walletA, 1.0);
            }
        }, "SafeTransfer-B->A");

        long start = System.currentTimeMillis();
        t1.start();
        t2.start();

        t1.join();
        t2.join();
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("Completed " + (totalTransfers * 2) + " reciprocal transfers in " + elapsed + " ms with ZERO deadlock!");
        System.out.println("Final State: " + walletA + ", " + walletB);
        System.out.println("Total Money Conserved: Rs " + (walletA.getBalance() + walletB.getBalance()) + " (Expected 2000.0)");
    }
}
