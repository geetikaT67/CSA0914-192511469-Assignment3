package Task04;

import java.util.Random;

/**
 * Task 04: Scanner Station implementation by extending java.lang.Thread.
 */
public class ScannerStationThread extends Thread {

    private final int stationId;
    private final int scriptsToScan;
    private int scriptsScanned = 0;
    private long finishTime = 0;

    public ScannerStationThread(int stationId, int scriptsToScan) {
        super("StationThread-" + stationId);
        this.stationId = stationId;
        this.scriptsToScan = scriptsToScan;
    }

    @Override
    public void run() {
        System.out.println("  [" + getName() + "] Started scanning " + scriptsToScan + " scripts. State=" + getState());
        Random random = new Random(stationId * 31L);

        for (int i = 1; i <= scriptsToScan; i++) {
            try {
                // Simulate physical scanning delay (TIMED_WAITING state)
                Thread.sleep(10 + random.nextInt(15));
                scriptsScanned++;
            } catch (InterruptedException e) {
                System.err.println("  [" + getName() + "] Interrupted!");
                Thread.currentThread().interrupt();
                return;
            }
        }
        finishTime = System.currentTimeMillis();
        System.out.println("  >>> [" + getName() + "] COMPLETED scanning " + scriptsScanned + " scripts.");
    }

    public int getStationId() { return stationId; }
    public int getScriptsScanned() { return scriptsScanned; }
    public long getFinishTime() { return finishTime; }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== EXAM SCANNER STATIONS (EXTENDING THREAD) ===");
        int totalStations = 4;
        int scriptsPerStation = 50;

        ScannerStationThread[] stations = new ScannerStationThread[totalStations];

        // 1. Creation (State: NEW)
        for (int i = 0; i < totalStations; i++) {
            stations[i] = new ScannerStationThread(i + 1, scriptsPerStation);
            System.out.println("Created " + stations[i].getName() + " | State: " + stations[i].getState());
        }

        // 2. Start (State: RUNNABLE)
        System.out.println("\nStarting all stations...");
        for (ScannerStationThread s : stations) {
            s.start();
        }

        // 3. Inspect isAlive() while running
        Thread.sleep(20);
        System.out.println("\nChecking isAlive() mid-execution:");
        for (ScannerStationThread s : stations) {
            System.out.println("  " + s.getName() + " isAlive=" + s.isAlive() + " | State=" + s.getState());
        }

        // 4. Synchronization via join()
        System.out.println("\nMain thread waiting for all scanner stations to join()...");
        for (ScannerStationThread s : stations) {
            s.join();
        }

        // 5. Aggregate final total (guaranteed only after all join())
        int totalScripts = 0;
        for (ScannerStationThread s : stations) {
            totalScripts += s.getScriptsScanned();
            System.out.println("  " + s.getName() + " isAlive=" + s.isAlive() + " | Final State=" + s.getState());
        }

        System.out.println("\n[SUMMARY]: All stations completed. Total scripts scanned: " + totalScripts + " / " + (totalStations * scriptsPerStation));
    }
}
