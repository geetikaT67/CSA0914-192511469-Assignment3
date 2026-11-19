package Task04;

import java.util.Random;

/**
 * Task 04: Scanner Station implementation by implementing java.lang.Runnable.
 * Demonstrates separation of task logic from thread execution.
 */
public class ScannerStationRunnable implements Runnable {

    private final int stationId;
    private final int scriptsToScan;
    private int scriptsScanned = 0;

    public ScannerStationRunnable(int stationId, int scriptsToScan) {
        this.stationId = stationId;
        this.scriptsToScan = scriptsToScan;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println("  [" + threadName + "] (Runnable) Processing station " + stationId + "...");
        Random random = new Random(stationId * 17L);

        for (int i = 1; i <= scriptsToScan; i++) {
            try {
                Thread.sleep(8 + random.nextInt(12));
                scriptsScanned++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println("  >>> [" + threadName + "] (Runnable) FINISHED station " + stationId + " with " + scriptsScanned + " scripts.");
    }

    public int getStationId() { return stationId; }
    public int getScriptsScanned() { return scriptsScanned; }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== EXAM SCANNER STATIONS (IMPLEMENTING RUNNABLE) ===");
        int totalStations = 4;
        int scriptsPerStation = 40;

        ScannerStationRunnable[] tasks = new ScannerStationRunnable[totalStations];
        Thread[] threads = new Thread[totalStations];

        for (int i = 0; i < totalStations; i++) {
            tasks[i] = new ScannerStationRunnable(i + 1, scriptsPerStation);
            threads[i] = new Thread(tasks[i], "StationWorker-" + (i + 1));
        }

        for (Thread t : threads) {
            t.start();
        }

        // Wait for all to finish
        for (Thread t : threads) {
            t.join();
        }

        int grandTotal = 0;
        for (ScannerStationRunnable r : tasks) {
            grandTotal += r.getScriptsScanned();
        }
        System.out.println("\n[SUMMARY - RUNNABLE]: All stations scanned. Total: " + grandTotal);
    }
}
