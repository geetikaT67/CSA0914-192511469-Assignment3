# Task 01: Handling Failure in a Bank ATM Simulation

**Subject:** Programming in Java (CSA0914)  
**Bloom's Level:** K4 – Analyse  
**Marks:** 6 Marks  
**Repository:** `CSA0914-192511469-Assignment3`

---

## 1. Problem Overview
The ATM simulation processes cash withdrawals by reading the user's account number and withdrawal amount, verifying the hardware card-reader configuration file, checking account balance, and dispensing funds. Failures occur when:
1. Card-reader device configuration file is missing (`FileNotFoundException`).
2. User enters letters or alphanumeric characters instead of numbers (`NumberFormatException` / `InputMismatchException`).
3. Withdrawal amount is zero or negative (`IllegalArgumentException`).
4. Requested withdrawal exceeds available balance (`InsufficientFundsException`).

---

## 2. Shared Group Work
### Failure Identification and Classification

| Failure Point | Real-World Event | Java Exception / Error Class | Classification | Recovery / Action |
|---|---|---|---|---|
| Card Reader Verification | Missing card reader config | `java.io.FileNotFoundException` | **Checked Exception** | Halt withdrawal, prompt admin maintenance |
| Account Number Input | User enters letters | `java.lang.NumberFormatException` | **Unchecked Exception** (`RuntimeException`) | Prompt re-entry of 10-digit number |
| Amount Input | User types "one-thousand" | `java.lang.NumberFormatException` | **Unchecked Exception** (`RuntimeException`) | Prompt numeric input |
| Non-positive Amount | User types -500 or 0 | `java.lang.IllegalArgumentException` | **Unchecked Exception** (`RuntimeException`) | Reject transaction, alert positive value |
| Balance Check | Amount > Balance | `Task01.InsufficientFundsException` | **Custom Checked Exception** | Decline transaction, display balance |
| Hardware Memory Failure | Out of system memory | `java.lang.OutOfMemoryError` | **JVM Error** (`java.lang.Error`) | Fatal: ATM shuts down gracefully |

---

## 3. Student A – Exception Design

### 3.1 Relevant Throwable Hierarchy
```
                       java.lang.Throwable
                               |
               +---------------+---------------+
               |                               |
       java.lang.Error                java.lang.Exception
               |                               |
     (OutOfMemoryError)       +----------------+----------------+
                              |                                 |
                     java.io.IOException             java.lang.RuntimeException
                              |                                 |
                 java.io.FileNotFoundException     +------------+------------+
                              |                    |                         |
               Task01.InsufficientFundsException   NumberFormatException   IllegalArgumentException
                 (User Checked Exception)
```

### 3.2 Failure Point to Catch-Block Mapping Table

| Failure Point | Exception Thrown | Checked / Unchecked | Catch Block Responsible | Handled In |
|---|---|---|---|---|
| Missing card config | `FileNotFoundException` | Checked | `catch (FileNotFoundException e)` | `AtmSimulation.withdraw()` |
| Account non-digits | `NumberFormatException` | Unchecked | `catch (NumberFormatException e)` (multi-catch) | `AtmSimulation.withdraw()` |
| Non-numeric amount | `NumberFormatException` | Unchecked | `catch (NumberFormatException e)` (multi-catch) | `AtmSimulation.withdraw()` |
| Negative amount | `IllegalArgumentException` | Unchecked | `catch (IllegalArgumentException e)` | `AtmSimulation.withdraw()` |
| Overdraft | `InsufficientFundsException` | Checked | `catch (InsufficientFundsException e)` | `AtmSimulation.withdraw()` |

### 3.3 Catch Block Ordering Justification
Java enforces that catch blocks are evaluated sequentially from top to bottom. Specific subclasses must appear before general superclasses:
1. `NumberFormatException` & `IllegalArgumentException` are distinct subclasses of `RuntimeException`.
2. `InsufficientFundsException` extends `Exception`.
3. `FileNotFoundException` extends `IOException` which extends `Exception`.
4. If a generic `catch (Exception e)` were placed before `catch (InsufficientFundsException e)`, the compiler halts with `error: exception InsufficientFundsException has already been caught`.

### 3.4 The Guarantee of the `finally` Block
In `AtmSimulation.java`, the `finally` block guarantees:
- Secure ejection of the user's ATM debit/credit card.
- Immediate closure of the active banking session.
- Committing the audit trail to disk.
This executes unconditionally—whether the withdrawal succeeded, threw a checked exception, threw an unchecked exception, or executed a `return` statement.

---

## 4. Student B – Testing and Tracing

### 4.1 Test Execution Table (5 Test Inputs)

| Test ID | Input Account | Input Amount | Card Config File | Expected Outcome | Catch Block Executed | Did `finally` Execute? | Status |
|---|---|---|---|---|---|---|---|
| TC-01 | `1234567890` | `1500.00` | Valid (`card_reader.dat`) | Dispensed Rs 1500, Balance: 3500 | None (Success) | **YES** | PASS |
| TC-02 | `1234567890` | `one-thousand` | Valid (`card_reader.dat`) | Invalid format rejected | `catch (NumberFormatException)` | **YES** | PASS |
| TC-03 | `1234567890` | `-500.00` | Valid (`card_reader.dat`) | Negative amount rejected | `catch (IllegalArgumentException)` | **YES** | PASS |
| TC-04 | `1234567890` | `10000.00` | Valid (`card_reader.dat`) | Overdraft declined | `catch (InsufficientFundsException)` | **YES** | PASS |
| TC-05 | `1234567890` | `500.00` | Missing (`missing.dat`) | Hardware error reported | `catch (FileNotFoundException)` | **YES** | PASS |

### 4.2 Uncaught Exception Stack Trace Analysis
Captured in `Logs/task01_uncaught_stacktrace.txt`:
```
Exception in thread "main" java.lang.NumberFormatException: For input string: "Rs.5000"
    at java.base/jdk.internal.math.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:2054)
    at java.base/jdk.internal.math.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
    at java.base/java.lang.Double.parseDouble(Double.java:792)
    at Task01.AtmUncaughtDemo.processWithdrawal(AtmUncaughtDemo.java:11)
    at Task01.AtmUncaughtDemo.main(AtmUncaughtDemo.java:17)
```
**Line-by-Line Breakdown:**
- **Header:** Uncaught exception in the thread `"main"` of type `java.lang.NumberFormatException` with detail message `"For input string: \"Rs.5000\""`.
- **Frame 1 (`FloatingDecimal.readJavaFormatString:2054`):** Internal JDK parser reached an unexpected non-digit character `'R'`.
- **Frame 2 (`FloatingDecimal.parseDouble:110`):** FloatingDecimal wrapper delegating parsing.
- **Frame 3 (`Double.parseDouble:792`):** Public Java API method called by application code.
- **Frame 4 (`AtmUncaughtDemo.processWithdrawal:11`):** User method that initiated the parse without enclosing try-catch.
- **Frame 5 (`AtmUncaughtDemo.main:17`):** Program entry point where the unhandled exception terminated the thread.

### 4.3 Broader Catch Before Narrower Demonstration
When `catch (Exception e)` is placed above `catch (InsufficientFundsException e)` in `AtmUnreachableCompileFail.java.demo`, `javac` emits:
```
Task01\AtmUnreachableCompileFail.java:14: error: exception InsufficientFundsException has already been caught
        } catch (InsufficientFundsException e) {
          ^
1 error
```
The compiler detects dead code because `InsufficientFundsException` is an `is-a` subtype of `Exception`.

---

## 5. Joint Design Question
**Should the ATM program catch every exception with a single `catch (Exception e)`?**
- **Under what conditions is it acceptable?** Only at the outermost boundary of an application (e.g. top-level UI loop or thread runner) as a safety net to prevent process death and log unexpected errors.
- **What is lost by doing so?**
  1. **Granularity:** Cannot distinguish between a user input error, a business constraint violation (overdraft), and an infrastructural failure (card reader missing).
  2. **Appropriate Recovery:** Each error requires a distinct user response (e.g., prompt for re-entry vs. alert bank technician).
  3. **Masking Bugs:** Catches unintended programming bugs such as `NullPointerException` or `ArrayIndexOutOfBoundsException`, obscuring defect detection.

---

## 6. Alternative Concept & Complexity Analysis: Error Codes vs Exceptions

Implemented in `Task01/AtmWithCodes.java` and benchmarked in `Task01/AtmBenchmark.java`.

### 6.1 Readability and Number of Checks
- **Exception Approach:** Separates the happy path from error handling. 5 checks are expressed cleanly; errors propagate up the call stack automatically.
- **Error Code Approach:** Every method invocation requires immediate conditional checks (`if (code != 0)`). Deeply nested call stacks lead to repetitive checking ("arrow anti-pattern"), increasing cognitive load.

### 6.2 Complexity Comparison
| Metric | Exception Handling | Error Codes |
|---|---|---|
| **Time Complexity (Happy Path)** | $O(1)$ zero overhead (table-based JVM unwinding) | $O(1)$ constant overhead of conditional branches |
| **Time Complexity (Failure Path)** | $O(D)$ where $D$ is stack depth (native `fillInStackTrace()`) | $O(1)$ primitive integer return |
| **Space Overhead** | Heap allocation of `Throwable` + stack trace frames | Zero heap allocation (passed via register / stack primitive) |

### 6.3 Empirical Benchmark Results (`ExperimentalData/task01_benchmark.csv`)
- **Error Code Approach:** **65.10 ns/op** (Total time: 13.02 ms for 200,000 iterations)
- **Exception Handling Approach:** **666.70 ns/op** (Total time: 133.34 ms for 200,000 iterations)
- **Slowdown Factor:** **10.24x** slower when exceptions are thrown in a hot loop due to JVM stack-trace capture.
- **Conclusion:** Exceptions must be reserved for exceptional and failure conditions, never used for standard control flow.
