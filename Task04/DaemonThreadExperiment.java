package Task04;

/**
 * Task 04: Daemon Thread Experiment
 * Converts one station to daemon (setDaemon(true)) and observes behavior when main() finishes.
 */
public class DaemonThreadExperiment {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== DAEMON THREAD EXPERIMENT ===");

        // User thread (Station 1)
        Thread userStation = new Thread(() -> {
            System.out.println("  [UserStation-1] Started. Will scan 3 scripts and finish cleanly.");
            for (int i = 1; i <= 3; i++) {
                try {
                    Thread.sleep(100);
                    System.out.println("  [UserStation-1] Scanned script " + i);
                } catch (InterruptedException ignored) {}
            }
            System.out.println("  [UserStation-1] COMPLETED normally.");
        }, "UserStation-1");

        // Daemon thread (Station 2 - Background Auto-Archiver)
        Thread daemonStation = new Thread(() -> {
            System.out.println("  [DaemonStation-2] (DAEMON) Started infinite background backup service...");
            int script = 1;
            while (true) {
                try {
                    Thread.sleep(150);
                    System.out.println("  [DaemonStation-2] (DAEMON) Archiving script batch #" + script++);
                } catch (InterruptedException ignored) {}
            }
        }, "DaemonStation-2");

        daemonStation.setDaemon(true); // Marked as DAEMON before start()

        userStation.start();
        daemonStation.start();

        System.out.println("Main thread waiting ONLY for userStation.join()...");
        userStation.join(); // Main thread only joins the user thread!

        System.out.println("[Main Thread] Exiting now. JVM will immediately terminate all active daemon threads!");
        // As soon as main thread exits and userStation has ended, JVM terminates daemonStation immediately
    }
}
