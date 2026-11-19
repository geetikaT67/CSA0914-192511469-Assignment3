package Task06.PartA;

/**
 * Hostel Wallet Account model.
 */
public class Wallet {
    private final int accountId;
    private double balance;

    public Wallet(int accountId, double initialBalance) {
        this.accountId = accountId;
        this.balance = initialBalance;
    }

    public int getAccountId() { return accountId; }
    public synchronized double getBalance() { return balance; }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) {
        this.balance -= amount;
    }

    @Override
    public String toString() {
        return "Wallet-" + accountId + "[Rs " + balance + "]";
    }
}
