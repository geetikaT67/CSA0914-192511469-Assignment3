package Task01;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Task 01: Bank ATM Simulation
 * Demonstrates robust exception handling: try, specific catch, multi-catch, and finally.
 */
public class AtmSimulation {

    private static final String CARD_READER_FILE = "TestData/card_reader.dat";
    private double balance = 5000.00; // Initial simulated balance

    /**
     * Verifies that the ATM hardware card-reader hardware/file is present.
     * Checked exception: FileNotFoundException
     */
    public void verifyCardReader(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("ATM Hardware Error: Card-reader device config file missing at " + filePath);
        }
    }

    /**
     * Performs an ATM withdrawal.
     * Validates input format, non-negative amounts, and balance constraints.
     */
    public void withdraw(String rawAccNo, String rawAmount, String cardReaderPath)
            throws FileNotFoundException, InsufficientFundsException {
        System.out.println("\n[START] Processing Transaction: Account=" + rawAccNo + ", Requested Amount=" + rawAmount);

        try {
            // Check 1: Card-reader status (checked exception)
            verifyCardReader(cardReaderPath);

            // Check 2: Account number numeric format (unchecked exception)
            if (!rawAccNo.matches("\\d{10}")) {
                throw new NumberFormatException("Account number must be exactly 10 numeric digits. Received: '" + rawAccNo + "'");
            }

            // Check 3: Parse amount (unchecked exception)
            double amount;
            try {
                amount = Double.parseDouble(rawAmount);
            } catch (NumberFormatException nfe) {
                throw new NumberFormatException("Withdrawal amount must be a valid numeric value. Received: '" + rawAmount + "'");
            }

            // Check 4: Non-positive amount validation (unchecked exception)
            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be strictly positive. Entered: " + amount);
            }

            // Check 5: Overdraft check (custom checked exception)
            if (amount > balance) {
                throw new InsufficientFundsException(
                    "Insufficient funds! Current balance: Rs " + balance + ", Attempted: Rs " + amount,
                    balance, amount
                );
            }

            // Success path
            balance -= amount;
            System.out.println(">>> SUCCESS: Dispensed Rs " + amount + ". Remaining Balance: Rs " + balance);

        } catch (NumberFormatException e) {
            System.out.println("[CATCH - Multi-Catch/NumberFormat]: Invalid format encountered: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[CATCH - IllegalArgumentException]: Business rule violation: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println("[CATCH - InsufficientFundsException]: Overdraft declined: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("[CATCH - FileNotFoundException]: Hardware error: " + e.getMessage());
        } finally {
            // Finally block always executes regardless of whether an exception occurred or was caught
            System.out.println("[FINALLY]: Card ejected securely. ATM transaction session closed. Audit log committed.");
        }
    }

    public double getBalance() {
        return balance;
    }

    public static void main(String[] args) {
        AtmSimulation atm = new AtmSimulation();

        // Ensure valid test data card reader file exists for the normal cases
        File testDir = new File("TestData");
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
        File validCardFile = new File(CARD_READER_FILE);
        try {
            if (!validCardFile.exists()) {
                java.nio.file.Files.writeString(validCardFile.toPath(), "CARD_READER_HARDWARE_READY=TRUE\n");
            }
        } catch (Exception ignored) {}

        System.out.println("=========================================================");
        System.out.println("   TASK 01: ATM SIMULATION EXCEPTION HANDLING TEST RUN   ");
        System.out.println("=========================================================");

        // Test Scenario 1: Valid transaction (Normal Success Path)
        System.out.println("\n--- Test Case 1: Valid Withdrawal (Success) ---");
        try {
            atm.withdraw("1234567890", "1500.00", CARD_READER_FILE);
        } catch (Exception e) {
            System.out.println("Unexpected failure: " + e);
        }

        // Test Scenario 2: Non-numeric input (Letters entered instead of numbers)
        System.out.println("\n--- Test Case 2: Letters Entered Instead of Numbers ---");
        try {
            atm.withdraw("1234567890", "one-thousand", CARD_READER_FILE);
        } catch (Exception e) {
            System.out.println("Unexpected failure: " + e);
        }

        // Test Scenario 3: Negative withdrawal amount
        System.out.println("\n--- Test Case 3: Negative Withdrawal Amount ---");
        try {
            atm.withdraw("1234567890", "-500.00", CARD_READER_FILE);
        } catch (Exception e) {
            System.out.println("Unexpected failure: " + e);
        }

        // Test Scenario 4: Withdrawal exceeding available balance
        System.out.println("\n--- Test Case 4: Overdraft Exceeding Available Balance ---");
        try {
            atm.withdraw("1234567890", "10000.00", CARD_READER_FILE);
        } catch (Exception e) {
            System.out.println("Unexpected failure: " + e);
        }

        // Test Scenario 5: Missing card-reader file
        System.out.println("\n--- Test Case 5: Missing Card-Reader File ---");
        try {
            atm.withdraw("1234567890", "500.00", "TestData/missing_card_reader.dat");
        } catch (Exception e) {
            System.out.println("Unexpected failure: " + e);
        }
    }
}
