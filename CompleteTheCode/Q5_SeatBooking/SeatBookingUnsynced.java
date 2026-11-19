package CompleteTheCode.Q5_SeatBooking;

/**
 * Supplied SeatBooking program executed WITHOUT synchronized keyword in TODO 1.
 * Demonstrates race condition and double-booking over 10 runs.
 */
public class SeatBookingUnsynced {
    private final boolean[] booked;
    private int bookingsMade = 0;

    SeatBookingUnsynced(int seats) { booked = new boolean[seats]; }

    // TODO 1 WITHOUT synchronized
    public void book(int seat) {
        if (!booked[seat]) {
            try {
                Thread.sleep(1); // Exposes race window between check and act
            } catch (InterruptedException ignored) {}
            booked[seat] = true;
            bookingsMade++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SeatBookingUnsynced screen = new SeatBookingUnsynced(10);
        Thread[] users = new Thread[100];
        for (int i = 0; i < users.length; i++) {
            final int seat = i % 10; // 10 users compete for every seat
            users[i] = new Thread(() -> screen.book(seat));
            users[i].start();
        }
        for (Thread t : users) t.join();
        System.out.println("Seats sold: " + screen.bookingsMade);
    }
}
