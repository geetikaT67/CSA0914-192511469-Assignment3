package Task05;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Task 05 Benchmark and Race Condition Analysis
 * 1. Executes 10 runs of unsynchronized booking to quantify double bookings.
 * 2. Measures performance across 10,000 bookings under high contention.
 * 3. Compares Synchronized Method, Synchronized Block, AtomicBoolean, ReentrantLock, and ConcurrentHashMap.
 */
public class SeatBookingBenchmark {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("=========================================================");
        System.out.println("   TASK 05: SEAT BOOKING RACE CONDITIONS & BENCHMARK      ");
        System.out.println("=========================================================");

        // --- Part 1: Record Double Bookings Over 10 Repeated Runs ---
        System.out.println("\n--- Part 1: Unsynchronized Booking Double-Booking over 10 Runs ---");
        int seats = 10;
        int totalUsers = 100; // 10 users per seat

        try (FileWriter fwRuns = new FileWriter("ExperimentalData/task05_double_booking_10runs.csv")) {
            fwRuns.write("RunNumber,TotalSeats,Competitors,SeatsReportedSold,DoubleBookingsCount\n");

            for (int run = 1; run <= 10; run++) {
                UnsynchronizedSeatBooking unsync = new UnsynchronizedSeatBooking(seats);
                Thread[] threads = new Thread[totalUsers];
                for (int u = 0; u < totalUsers; u++) {
                    final int s = u % seats;
                    threads[u] = new Thread(() -> unsync.book(s));
                    threads[u].start();
                }
                for (Thread t : threads) t.join();

                int sold = unsync.getBookingsMade();
                int doubleBookings = Math.max(0, sold - seats);
                System.out.printf("Run %2d: Seats Sold: %2d / 10 | Double Bookings: %2d\n", run, sold, doubleBookings);
                fwRuns.write(String.format("%d,%d,%d,%d,%d\n", run, seats, totalUsers, sold, doubleBookings));
            }
        }
        System.out.println("Saved 10-run race data to ExperimentalData/task05_double_booking_10runs.csv");

        // --- Part 2: 10,000 Booking Performance Benchmark ---
        System.out.println("\n--- Part 2: High Contention Throughput Benchmark (10,000 Bookings) ---");
        int benchmarkRequests = 10_000;
        int benchmarkSeats = 50;
        int threadsCount = 16;

        // 1. Synchronized Method
        long timeSyncMethod = testThroughput(benchmarkRequests, threadsCount, new SyncMethodRunner(benchmarkSeats));
        // 2. Synchronized Block (Per-seat fine grained)
        long timeSyncBlock = testThroughput(benchmarkRequests, threadsCount, new SyncBlockRunner(benchmarkSeats));
        // 3. AtomicBoolean CAS
        long timeAtomic = testThroughput(benchmarkRequests, threadsCount, new AtomicRunner(benchmarkSeats));
        // 4. ReentrantLock
        long timeLock = testThroughput(benchmarkRequests, threadsCount, new LockRunner(benchmarkSeats));
        // 5. ConcurrentHashMap
        long timeMap = testThroughput(benchmarkRequests, threadsCount, new MapRunner(benchmarkSeats));

        System.out.printf("1. Synchronized Method:   %8.2f ms (Avg: %6.1f ns/op)\n", timeSyncMethod / 1e6, (double) timeSyncMethod / benchmarkRequests);
        System.out.printf("2. Synchronized Block:    %8.2f ms (Avg: %6.1f ns/op)\n", timeSyncBlock / 1e6, (double) timeSyncBlock / benchmarkRequests);
        System.out.printf("3. AtomicBoolean (CAS):   %8.2f ms (Avg: %6.1f ns/op)\n", timeAtomic / 1e6, (double) timeAtomic / benchmarkRequests);
        System.out.printf("4. ReentrantLock:         %8.2f ms (Avg: %6.1f ns/op)\n", timeLock / 1e6, (double) timeLock / benchmarkRequests);
        System.out.printf("5. ConcurrentHashMap:     %8.2f ms (Avg: %6.1f ns/op)\n", timeMap / 1e6, (double) timeMap / benchmarkRequests);

        try (FileWriter fwBench = new FileWriter("ExperimentalData/task05_booking_benchmark.csv")) {
            fwBench.write("Strategy,TotalRequests,SeatsCount,Threads,TotalTimeMs,AvgTimeNs\n");
            fwBench.write(String.format("SynchronizedMethod,%d,%d,%d,%.3f,%.1f\n", benchmarkRequests, benchmarkSeats, threadsCount, timeSyncMethod / 1e6, (double) timeSyncMethod / benchmarkRequests));
            fwBench.write(String.format("SynchronizedBlock,%d,%d,%d,%.3f,%.1f\n", benchmarkRequests, benchmarkSeats, threadsCount, timeSyncBlock / 1e6, (double) timeSyncBlock / benchmarkRequests));
            fwBench.write(String.format("AtomicBooleanCAS,%d,%d,%d,%.3f,%.1f\n", benchmarkRequests, benchmarkSeats, threadsCount, timeAtomic / 1e6, (double) timeAtomic / benchmarkRequests));
            fwBench.write(String.format("ReentrantLock,%d,%d,%d,%.3f,%.1f\n", benchmarkRequests, benchmarkSeats, threadsCount, timeLock / 1e6, (double) timeLock / benchmarkRequests));
            fwBench.write(String.format("ConcurrentHashMap,%d,%d,%d,%.3f,%.1f\n", benchmarkRequests, benchmarkSeats, threadsCount, timeMap / 1e6, (double) timeMap / benchmarkRequests));
            System.out.println("Saved benchmark data to ExperimentalData/task05_booking_benchmark.csv");
        }
    }

    interface BookingTest {
        void book(int seat, int userId);
        int getSeatsSold();
    }

    static class SyncMethodRunner implements BookingTest {
        private final SynchronizedMethodSeatBooking booking;
        SyncMethodRunner(int seats) { booking = new SynchronizedMethodSeatBooking(seats); }
        public void book(int seat, int u) { booking.book(seat); }
        public int getSeatsSold() { return booking.getBookingsMade(); }
    }

    static class SyncBlockRunner implements BookingTest {
        private final SynchronizedBlockSeatBooking booking;
        SyncBlockRunner(int seats) { booking = new SynchronizedBlockSeatBooking(seats); }
        public void book(int seat, int u) { booking.book(seat); }
        public int getSeatsSold() { return booking.getBookingsMade(); }
    }

    static class AtomicRunner implements BookingTest {
        private final CinemaAlternativesDemo.AtomicSeatBooking booking;
        AtomicRunner(int seats) { booking = new CinemaAlternativesDemo.AtomicSeatBooking(seats); }
        public void book(int seat, int u) { booking.book(seat); }
        public int getSeatsSold() { return booking.getBookingsCount(); }
    }

    static class LockRunner implements BookingTest {
        private final CinemaAlternativesDemo.LockSeatBooking booking;
        LockRunner(int seats) { booking = new CinemaAlternativesDemo.LockSeatBooking(seats); }
        public void book(int seat, int u) { booking.book(seat); }
        public int getSeatsSold() { return booking.getBookingsCount(); }
    }

    static class MapRunner implements BookingTest {
        private final CinemaAlternativesDemo.MapSeatBooking booking = new CinemaAlternativesDemo.MapSeatBooking();
        MapRunner(int seats) {}
        public void book(int seat, int u) { booking.book(seat, "User-" + u); }
        public int getSeatsSold() { return booking.getBookingsCount(); }
    }

    private static long testThroughput(int requests, int threadsCount, BookingTest test) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threadsCount);
        long start = System.nanoTime();
        for (int i = 0; i < requests; i++) {
            final int seat = i % 50;
            final int uid = i;
            pool.submit(() -> test.book(seat, uid));
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        return System.nanoTime() - start;
    }
}
