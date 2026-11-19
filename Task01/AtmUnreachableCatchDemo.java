package Task01;

/**
 * Task 01 Demonstration:
 * Demonstrates the compilation failure when a broader catch block (e.g. Exception)
 * is placed before a narrower/subclass catch block (e.g. InsufficientFundsException).
 *
 * The Java compiler rejects this with:
 * "error: exception InsufficientFundsException has already been caught"
 */
public class AtmUnreachableCatchDemo {

    public static void testUnreachableCatch() {
        /*
        // UNCOMMENTING THE BLOCK BELOW CAUSES A COMPILE-TIME ERROR:
        try {
            double balance = 100.0;
            double amount = 500.0;
            if (amount > balance) {
                throw new InsufficientFundsException("Overdraft", balance, amount);
            }
        } catch (Exception e) {
            // Broader catch block placed first!
            System.out.println("Caught by generic Exception catch block: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            // Narrower catch block placed second!
            // COMPILE-TIME ERROR:
            // error: exception InsufficientFundsException has already been caught
            System.out.println("Caught by InsufficientFundsException: " + e.getMessage());
        }
        */
        System.out.println("AtmUnreachableCatchDemo: Demonstrated in comments and documentation.");
    }

    public static void main(String[] args) {
        testUnreachableCatch();
    }
}
