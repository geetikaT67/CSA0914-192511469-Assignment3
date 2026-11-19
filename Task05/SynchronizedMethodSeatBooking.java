package Task05;

/**
 * Task 05: Synchronized Method Seat Booking
 * Locks the entire method using the screen object's intrinsic lock (this monitor).
 */
public class SynchronizedMethodSeatBooking {
    private final boolean[] booked;
    private int bookingsMade = 0;

    public SynchronizedMethodSeatBooking(int seats) {
        this.booked = new boolean[seats];
    }

    public synchronized boolean book(int seat) {
        if (!booked[seat]) {
            booked[seat] = true;
            bookingsMade++;
            return true;
        }
        return false;
    }

    public synchronized int getBookingsMade() { return bookingsMade; }
    public synchronized boolean[] getBookedArray() { return booked.clone(); }
}
