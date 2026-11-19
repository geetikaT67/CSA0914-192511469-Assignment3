# Task 03: Resource Safety, Debugging and Robust Code

**Subject:** Programming in Java (CSA0914)  
**Bloom's Level:** K4 – Analyse  
**Marks:** 7 Marks  
**Repository:** `CSA0914-192511469-Assignment3`

---

## 1. Problem Overview
A daily sales report generator aggregates transactions from multiple retail store CSV files into a centralized summary CSV file. In legacy production deployments, the system occasionally halted with the operating system error:
```
java.io.IOException: Too many open files
```
producing incomplete or truncated summary reports. The goal of this task is to diagnose the root cause of this resource leak, compare manual `try-catch-finally` against Java's `try-with-resources`, and verify safe file closure under error conditions.

---

## 2. Shared Group Work
### Implementation and Output Verification
Two versions were implemented:
1. `Task03/ManualSalesReportGenerator.java`: Classic `try-finally` with manual `close()` calls.
2. `Task03/TryWithResourcesSalesReportGenerator.java`: Automatic resource management using `try-with-resources`.

**Verification on Valid Input:**  
Both versions were executed against 5 valid store files (`TestData/stores_valid/`).
- Output files `TestData/manual_valid_summary.csv` and `TestData/twr_valid_summary.csv` were compared programmatically.
- **Result:** Exact identical match:
  ```csv
  StoreFile,TotalSalesAmount
  store_1.csv,4550.00
  store_2.csv,4550.00
  store_3.csv,4550.00
  store_4.csv,4550.00
  store_5.csv,4550.00
  ```

---

## 3. Student A – Debugging and Experimental Analysis

### 3.1 Breakpoint & Step-Through Debugging Analysis
Using debugger breakpoints placed at the file parsing loop:
- **Breakpoint 1 (Line 80 in `ManualSalesReportGenerator.java`):** Inside the loop, `br = new BufferedReader(fr)` opens OS handle `FD #4`.
- **Breakpoint 2 (Line 92):** Parsing `store_4.csv` line 2: `Integer.parseInt("CORRUPT_QTY")` throws `NumberFormatException`.
- **Execution Step:** Control abruptly jumps out of the `try` block straight to `catch (NumberFormatException nfe)` on line 102.
- **Root Cause Identified:** The line `br.close();` on line 100 is completely bypassed! The `BufferedReader` and underlying OS file descriptor remain allocated in the JVM process table.

### 3.2 Constructed Failing Input
- **Dataset:** `TestData/stores_fail/` containing 10 store files.
- **Corrupt File:** `store_4.csv` contains an invalid non-integer string:
  ```csv
  ItemName,Quantity,UnitPrice
  Stationery_Pen,10,15.50
  Stationery_Notebook,CORRUPT_QTY,45.00
  Stationery_Folder,5,25.00
  ```

### 3.3 Comparative Open File Handle Counts
Measured by instrumented atomic tracking in `Task03/ResourceSafetyBenchmark.java`:

| Metric | Manual `try-catch-finally` | `try-with-resources` | Impact |
|---|---|---|---|
| **Input Files Processed** | 10 (Fails at file 4) | 10 (Fails at file 4) | Partial run |
| **Open Handles After Abort** | **1 Leaked Handle** (`store_4.csv`) | **0 Leaked Handles** | TWR cleanly closed FD |
| **Scalability over $f$ failures** | Leaks 1 FD per failed file | Leaks 0 FDs | Prevents "Too many open files" |

### 3.4 Failure Logging Evidence
Captured in `Logs/task03_benchmark.txt`:
```
[LOG - MANUAL]: File format corruption detected in: store_4.csv - For input string: "CORRUPT_QTY"
Manual version threw exception: Aborting report due to corrupt file: store_4.csv
Leaked Open File Handles in Manual Version: 1

[LOG - TWR]: Format error in store_4.csv - For input string: "CORRUPT_QTY"
Try-with-resources threw exception: Aborting report due to corrupt file: store_4.csv
Leaked Open File Handles in TWR Version:    0
```

---

## 4. Student B – Design and Correctness

### 4.1 Resource Lifecycle Inventory
| Resource | Class | Instantiated Where | Must Be Closed When / Where |
|---|---|---|---|
| Summary Writer | `BufferedWriter` / `FileWriter` | Start of report generation | Unconditionally at end of entire report |
| Store Reader | `BufferedReader` / `FileReader` | Start of each store file loop | Immediately after reading the file, even if parsing fails |

### 4.2 Why Manual Closing Leaks Resources
In manual code, closing multiple resources correctly is notoriously error-prone:
1. **Exception inside `try` block:** If an exception occurs before reaching `br.close()`, the reader is never closed.
2. **Exception inside `finally` block:** If `reader.close()` throws an `IOException`, the subsequent `writer.close()` statement is never reached, leaking the writer handle.
3. **Swallowed Exceptions:** A failure during closing in `finally` overwrites the original, more informative application error.

### 4.3 Guarantees of `try-with-resources`
- **`AutoCloseable` Contract:** Any object implementing `java.lang.AutoCloseable` declared in the `try (...)` specification is guaranteed to have its `close()` method invoked upon exiting the block.
- **Closing Order (LIFO):** Resources are closed in the **exact reverse order** of their declaration:
  1. `BufferedReader` closed first.
  2. `TrackedFileReader` closed second.
  3. `BufferedWriter` closed third.
  4. `TrackedFileWriter` closed last.

### 4.4 Suppressed Exceptions Explained
Captured in `Logs/task03_suppressed_exceptions.txt`:
```
[CAUGHT PRIMARY EXCEPTION]: Primary failure inside try block during file processing
Number of suppressed exceptions attached: 1
  [SUPPRESSED EXCEPTION 1]: Secondary failure while closing resource SalesDatabaseConnection

java.io.IOException: Primary failure inside try block during file processing
    at Task03.SuppressedExceptionDemo$FaultyResource.doWork(SuppressedExceptionDemo.java:20)
    Suppressed: java.io.IOException: Secondary failure while closing resource SalesDatabaseConnection
        at Task03.SuppressedExceptionDemo$FaultyResource.close(SuppressedExceptionDemo.java:26)
```
- When an exception $E_1$ occurs in the `try` block and a subsequent exception $E_2$ occurs during `close()`, Java attaches $E_2$ to $E_1$ via `Throwable.addSuppressed()`.
- The developer sees the primary root cause without losing the secondary closing failure.

---

## 5. Joint Engineering Decision
**Under what conditions is `try-with-resources` not sufficient on its own to make the report generator robust?**
1. **Partial / Corrupted Output File:** If an exception occurs half-way through writing the summary, `try-with-resources` closes the output file, but leaves a partially written, corrupt report on disk. **Solution:** Write to a temporary file (`summary.tmp`) and perform an atomic rename/move (`Files.move` with `ATOMIC_MOVE`) only upon complete success.
2. **Non-`AutoCloseable` Resources:** Legacy C-libraries, JNI handles, or thread pools (`ExecutorService`) do not implement `AutoCloseable` in older Java versions and require explicit shutdown procedures.
3. **Disk Space Exhaustion (`ENOSPC`):** Closing a stream on a full disk does not guarantee that buffered bytes were flushed successfully without error handling.

---

## 6. Alternative Concept & Complexity Analysis

### 6.1 Explicit `finally` vs. No Closing vs. `try-with-resources`
```java
// Alternative 1: Nested finally blocks (extremely verbose, 15+ lines per resource)
FileReader fr = null;
try {
    fr = new FileReader(file);
} finally {
    if (fr != null) {
        try { fr.close(); } catch (IOException ignored) {}
    }
}
```

### 6.2 Complexity for $f$ Store Files
| Strategy | Time Complexity | File Handles Open (Max Space) | Behavior on Failure |
|---|---|---|---|
| **No Closing** | $O(f)$ | $O(f)$ handles open (grows unbounded) | JVM crashes with "Too many open files" when $f > \text{FD\_LIMIT}$ (e.g. 1024) |
| **Flawed Manual Closing** | $O(f)$ | $O(k)$ leaked handles where $k$ is number of corrupt files | Progressive resource leak over time |
| **`try-with-resources`** | $O(f)$ | $O(1)$ constant handles open (at most 2: 1 writer + 1 reader) | Immediate deterministic release of file handles |
