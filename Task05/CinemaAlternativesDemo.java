package Task05;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Task 05: Alternative Concepts for Atomic Seat Booking
 * 1. AtomicBoolean array using lock-free CAS (compareAndSet)
 * 2. ReentrantLock per seat (explicit lock with fairness / tryLock options)
 * 3. ConcurrentHashMap using putIfAbsent()
 */
public class CinemaAlternativesDemo {

    // Approach 1: AtomicBoolean array (Lock-free CAS)
    public static class AtomicSeatBooking {
        private final AtomicBoolean[] seats;
        private final AtomicInteger bookingsCount = new AtomicInteger(0);

        public AtomicSeatBooking(int totalSeats) {
            seats = new AtomicBoolean[totalSeats];
            for (int i = 0; i < totalSeats; i++) {
                seats[i] = new AtomicBoolean(false);
            }
        }

        public boolean book(int seat) {
            // Atomic compare-and-swap from false (free) to true (booked)
            if (seats[seat].compareAndSet(false, true)) {
                bookingsCount.incrementAndGet();
                return true;
            }
            return false;
        }

        public int getBookingsCount() { return bookingsCount.get(); }
    }

    // Approach 2: ReentrantLock
    public static class LockSeatBooking {
        private final boolean[] booked;
        private final ReentrantLock[] locks;
        private final AtomicInteger count = new AtomicInteger(0);

        public LockSeatBooking(int totalSeats) {
            booked = new boolean[totalSeats];
            locks = new ReentrantLock[totalSeats];
            for (int i = 0; i < totalSeats; i++) {
                locks[i] = new ReentrantLock();
            }
        }

        public boolean book(int seat) {
            locks[seat].lock();
            try {
                if (!booked[seat]) {
                    booked[seat] = true;
                    count.incrementAndGet();
                    return true;
                }
                return false;
            } finally {
                locks[seat].unlock();
            }
        }

        public int getBookingsCount() { return count.get(); }
    }

    // Approach 3: ConcurrentHashMap.putIfAbsent()
    public static class MapSeatBooking {
        private final ConcurrentHashMap<Integer, String> seatMap = new ConcurrentHashMap<>();

        public boolean book(int seat, String userId) {
            return seatMap.putIfAbsent(seat, userId) == null;
        }

        public int getBookingsCount() { return seatMap.size(); }
    }
}
