package Task04;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Task 04: Ten-Run Concurrency Experiment
 * Runs 4 scanner stations 10 consecutive times to capture the non-deterministic
 * finish order influenced by the OS thread scheduler.
 */
public class TenRunsOrderExperiment {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("=========================================================");
        System.out.println("   TASK 04: TEN-RUN COMPLETION ORDER EXPERIMENT          ");
        System.out.println("=========================================================");

        List<String> records = new ArrayList<>();

        for (int run = 1; run <= 10; run++) {
            List<Integer> finishOrder = Collections.synchronizedList(new ArrayList<>());
            Thread[] stations = new Thread[4];

            for (int s = 0; s < 4; s++) {
                final int stationId = s + 1;
                stations[s] = new Thread(() -> {
                    // Small random jitter simulating scanner mechanical delays
                    Random rand = new Random(System.nanoTime() ^ (stationId * 7919L));
                    for (int i = 0; i < 20; i++) {
                        try {
                            Thread.sleep(1 + rand.nextInt(3));
                        } catch (InterruptedException ignored) {}
                    }
                    finishOrder.add(stationId);
                }, "Station-" + stationId);
            }

            for (Thread t : stations) t.start();
            for (Thread t : stations) t.join();

            String orderStr = finishOrder.toString().replaceAll("[\\[\\] ]", "").replace(",", " -> ");
            records.add("Run " + run + ": " + orderStr);
            System.out.println(String.format("Run %2d Finish Order: Station %s", run, orderStr));
        }

        // Export to CSV
        try (FileWriter fw = new FileWriter("ExperimentalData/task04_ten_runs.csv")) {
            fw.write("RunNumber,Station1st,Station2nd,Station3rd,Station4th\n");
            for (int r = 0; r < records.size(); r++) {
                String raw = records.get(r).split(": ")[1];
                String[] parts = raw.split(" -> ");
                fw.write(String.format("%d,%s,%s,%s,%s\n", r + 1, parts[0], parts[1], parts[2], parts[3]));
            }
            System.out.println("\nSaved ten-run observations to ExperimentalData/task04_ten_runs.csv");
        }
    }
}
