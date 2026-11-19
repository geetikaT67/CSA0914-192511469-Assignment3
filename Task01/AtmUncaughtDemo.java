package Task01;

/**
 * Task 01: Demonstrates an uncaught exception to inspect its stack trace.
 * Student B Requirement: Show the stack trace for one uncaught exception and explain each line.
 */
public class AtmUncaughtDemo {

    public static void processWithdrawal(String rawAmount) {
        // Will throw an uncaught NumberFormatException if rawAmount is non-numeric
        double amount = Double.parseDouble(rawAmount);
        System.out.println("Parsed amount: " + amount);
    }

    public static void main(String[] args) {
        System.out.println("Starting AtmUncaughtDemo - attempting to parse non-numeric amount without try-catch...");
        processWithdrawal("Rs.5000"); // Uncaught NumberFormatException
    }
}
