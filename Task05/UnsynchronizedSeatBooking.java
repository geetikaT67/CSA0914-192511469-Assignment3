package Task05;

/**
 * Task 05: Unsynchronized Seat Booking
 * Demonstrates race condition where check-then-act is non-atomic.
 */
public class UnsynchronizedSeatBooking {
    private final boolean[] booked;
    private int bookingsMade = 0;

    public UnsynchronizedSeatBooking(int seats) {
        this.booked = new boolean[seats];
    }

    public boolean book(int seat) {
        // Critical Section START (Unprotected!)
        if (!booked[seat]) {
            try {
                // Micro-pause exposes race condition interleaving
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
            booked[seat] = true;
            bookingsMade++;
            return true;
        }
        // Critical Section END
        return false;
    }

    public int getBookingsMade() { return bookingsMade; }
    public boolean[] getBookedArray() { return booked; }
}
