package Task03;

import java.io.IOException;

/**
 * Task 03 Demonstration: Suppressed Exceptions in try-with-resources.
 * Student B Requirement: Explain what a suppressed exception is and when it occurs.
 */
public class SuppressedExceptionDemo {

    static class FaultyResource implements AutoCloseable {
        private final String name;

        FaultyResource(String name) {
            this.name = name;
        }

        public void doWork() throws IOException {
            System.out.println("Resource " + name + " performing operation...");
            throw new IOException("Primary failure inside try block during file processing");
        }

        @Override
        public void close() throws IOException {
            System.out.println("Resource " + name + " close() invoked automatically.");
            throw new IOException("Secondary failure while closing resource " + name);
        }
    }

    public static void demonstrateSuppression() {
        System.out.println("=== TRY-WITH-RESOURCES SUPPRESSED EXCEPTION DEMONSTRATION ===");
        try (FaultyResource res = new FaultyResource("SalesDatabaseConnection")) {
            res.doWork();
        } catch (IOException e) {
            System.out.println("\n[CAUGHT PRIMARY EXCEPTION]: " + e.getMessage());

            Throwable[] suppressed = e.getSuppressed();
            System.out.println("Number of suppressed exceptions attached: " + suppressed.length);
            for (int i = 0; i < suppressed.length; i++) {
                System.out.println("  [SUPPRESSED EXCEPTION " + (i + 1) + "]: " + suppressed[i].getMessage());
            }

            System.out.println("\nFull stack trace showing suppressed block:");
            e.printStackTrace(System.out);
        }
    }

    public static void main(String[] args) {
        demonstrateSuppression();
    }
}
