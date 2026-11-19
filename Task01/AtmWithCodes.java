package Task01;

import java.io.File;
import java.util.Scanner;

/**
 * Task 01: Alternative Concept
 * ATM simulation using pre-condition checks and integer error codes instead of exceptions.
 *
 * Return codes:
 *   0  : Success
 *  -1  : Missing card-reader file (Hardware fault)
 *  -2  : Invalid account number format (Non-numeric or length != 10)
 *  -3  : Non-numeric withdrawal amount
 *  -4  : Non-positive withdrawal amount (<= 0)
 *  -5  : Overdraft (Insufficient funds)
 */
public class AtmWithCodes {

    private double balance = 5000.00;

    public int withdraw(String rawAccNo, String rawAmount, String cardReaderPath) {
        // Check 1: Card-reader file existence
        File cardFile = new File(cardReaderPath);
        if (!cardFile.exists()) {
            return -1; // Hardware fault
        }

        // Check 2: Account number validation using manual scanner/check
        if (rawAccNo == null || rawAccNo.length() != 10) {
            return -2; // Format fault
        }
        Scanner accScanner = new Scanner(rawAccNo);
        if (!accScanner.hasNextLong()) {
            accScanner.close();
            return -2;
        }
        accScanner.close();

        // Check 3: Withdrawal amount numeric validation
        if (rawAmount == null || rawAmount.trim().isEmpty()) {
            return -3;
        }
        Scanner amtScanner = new Scanner(rawAmount);
        if (!amtScanner.hasNextDouble()) {
            amtScanner.close();
            return -3; // Non-numeric amount
        }
        double amount = amtScanner.nextDouble();
        amtScanner.close();

        // Check 4: Positive amount check
        if (amount <= 0) {
            return -4; // Non-positive amount
        }

        // Check 5: Overdraft check
        if (amount > balance) {
            return -5; // Overdraft
        }

        // Success
        balance -= amount;
        return 0;
    }

    public double getBalance() {
        return balance;
    }

    public static void main(String[] args) {
        AtmWithCodes atm = new AtmWithCodes();
        String cardFile = "TestData/card_reader.dat";

        System.out.println("=== ATM WITH ERROR CODES DEMONSTRATION ===");
        int code1 = atm.withdraw("1234567890", "1500.00", cardFile);
        System.out.println("Test 1 (Valid): Return Code = " + code1 + " (Remaining: Rs " + atm.getBalance() + ")");

        int code2 = atm.withdraw("1234567890", "invalid-amt", cardFile);
        System.out.println("Test 2 (Letters in Amount): Return Code = " + code2);

        int code3 = atm.withdraw("1234567890", "-500", cardFile);
        System.out.println("Test 3 (Negative Amount): Return Code = " + code3);

        int code4 = atm.withdraw("1234567890", "10000", cardFile);
        System.out.println("Test 4 (Overdraft): Return Code = " + code4);

        int code5 = atm.withdraw("1234567890", "500", "TestData/missing.dat");
        System.out.println("Test 5 (Missing Card Reader): Return Code = " + code5);
    }
}
