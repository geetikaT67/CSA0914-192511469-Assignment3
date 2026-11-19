package Task05;

/**
 * Task 05: Synchronized Block Seat Booking
 * Locks only the critical section using synchronized block,
 * supporting fine-grained per-seat locks to maximize throughput.
 */
public class SynchronizedBlockSeatBooking {
    private final boolean[] booked;
    private final Object[] seatLocks;
    private int bookingsMade = 0;
    private final Object countLock = new Object();

    public SynchronizedBlockSeatBooking(int seats) {
        this.booked = new boolean[seats];
        this.seatLocks = new Object[seats];
        for (int i = 0; i < seats; i++) {
            seatLocks[i] = new Object();
        }
    }

    public boolean book(int seat) {
        // Validation outside synchronized block
        if (seat < 0 || seat >= booked.length) return false;

        // Synchronize ONLY on the specific seat being booked (fine-grained lock!)
        synchronized (seatLocks[seat]) {
            if (!booked[seat]) {
                booked[seat] = true;
                synchronized (countLock) {
                    bookingsMade++;
                }
                return true;
            }
        }
        return false;
    }

    public int getBookingsMade() {
        synchronized (countLock) {
            return bookingsMade;
        }
    }
}
