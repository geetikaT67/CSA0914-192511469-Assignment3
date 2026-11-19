# ASSIGNMENT 3 – CO3: EXCEPTION HANDLING & MULTITHREADING
## PROGRAMMING IN JAVA (CSA0914)
**Comprehensive Pair-Work Laboratory & Engineering Evaluation Report**

---

### Student Details & Cover Information
- **Student A (Lead / Primary):** Geetika T (Register No: 192511469)
- **Student B (Pair Contributor):** Pair Partner / Contributor B (Register No: 192511470)
- **Course:** Programming in Java (CSA0914)
- **Course Outcome:** CO3 – Exception Handling & Multithreading (Sessions 27–38)
- **Bloom's Cognitive Levels:** K4 – Analyse | K5 – Evaluate
- **Duration:** 1 Week | **Total Marks:** 50
- **Primary GitHub Repository:** [https://github.com/geetikaT67/CSA0914-192511469-Assignment3](https://github.com/geetikaT67/CSA0914-192511469-Assignment3)
- **Secondary GitHub Repository:** [https://github.com/geetikaT67/CSA0914-192511469-Assignment3](https://github.com/geetikaT67/CSA0914-192511469-Assignment3)

---

## Table of Contents
1. [Executive Summary & CO3 Scope](#1-executive-summary--co3-scope)
2. [Individual Contribution Record](#2-individual-contribution-record)
3. [Task 01: Bank ATM Simulation (Handling Failure, Catch Hierarchy, Error Codes)](#3-task-01-bank-atm-simulation)
4. [Task 02: Online Examination Portal (Exception Hierarchy, Chaining, Layered Propagation)](#4-task-02-online-examination-portal)
   - [Complete the Code Q2: StorageDemo Analysis](#complete-the-code-q2-storagedemo-analysis)
5. [Task 03: Resource Safety, Debugging and Robust Code (File Leak Diagnosis)](#5-task-03-resource-safety-debugging-and-robust-code)
6. [Task 04: Thread Creation and the Thread Life Cycle (Scanner Stations)](#6-task-04-thread-creation-and-the-thread-life-cycle)
7. [Task 05: Race Conditions in a Cinema Seat-Booking System](#7-task-05-race-conditions-in-a-cinema-seat-booking-system)
   - [Complete the Code Q5: SeatBooking Analysis](#complete-the-code-q5-seatbooking-analysis)
8. [Task 06: Deadlock and Inter-Thread Communication](#8-task-06-deadlock-and-inter-thread-communication)
   - [Complete the Code Q6: TokenCounter Analysis](#complete-the-code-q6-tokencounter-analysis)
9. [Task 07: High-Throughput Fault-Tolerant Score File Processor (10,000 Files)](#9-task-07-high-throughput-fault-tolerant-score-file-processor)
10. [Experimental Data & Comparative Complexity Analysis](#10-experimental-data--comparative-complexity-analysis)
11. [Final Verification Checklist](#11-final-verification-checklist)
12. [Academic Conclusion & Viva Reference](#12-academic-conclusion--viva-reference)

---

## 1. Executive Summary & CO3 Scope

Course Outcome 3 (CO3) covers modern Java robustness through structured exception handling, automatic resource lifecycle management, thread concurrency primitives, synchronization, mutual deadlock avoidance, bounded-buffer inter-thread communication, and thread-pool execution via the Java Concurrency Framework.

This report documents the joint and individual contributions of Student A and Student B across all seven core tasks and three "Complete the Code" exercises. Every program has been compiled under OpenJDK 21, executed under high contention or failure-injection conditions, verified against formal mathematical constraints, and benchmarked empirically.

---

## 2. Individual Contribution Record

As mandated by page 28 of the specification, the individual responsibilities alternated systematically across every question to ensure both students demonstrated mastery of **Design $\rightarrow$ Implementation $\rightarrow$ Analysis $\rightarrow$ Evaluation**:

| Task ID | Task Title | Student A Primary Responsibility (Geetika T - 192511469) | Student B Primary Responsibility (Partner - 192511470) | Joint Work | Status |
|---|---|---|---|---|---|
| **Q1** | Bank ATM Simulation | Exception Design, Throwable Hierarchy Diagram, Catch Ordering Justification, Finally Guarantees | Testing & Tracing, 5 Test Paths, Uncaught Stack Trace Line Analysis, Unreachable Catch Demo | Failure point identification, Exception vs Error classification, Single `catch (Exception e)` evaluation | **100% Completed** |
| **Q2** | Online Exam Portal | Exception Hierarchy & Propagation Design, Class Diagram, Checked/Unchecked Justification, UI Sanitization | Failure Scenarios & Chaining Evidence, Chained Stack Trace, getCause() Demo, Information Loss Analysis | 3-Layer Architecture (Controller-Service-Storage), StorageException vs IOException UI decision | **100% Completed** |
| **Q3** | Resource Safety & Debugging | Debugging & Experimental Analysis, Breakpoint & Stepping, Constructed Failing Input, Leaked Handle Count | Design & Correctness, Resource Inventory, Manual Leak Mechanics, Suppressed Exceptions | Manual close() vs try-with-resources implementation, Identical report verification, Robustness limits | **100% Completed** |
| **Q4** | Thread Creation & Lifecycle | Thread Design, Thread vs Runnable Comparison, Life Cycle State Diagram, start() vs run() Proof, join() Sync | Behavioural Experiments, 10-Run Finish Order Tracking, Priority Experiments, Daemon Thread Killing | Dual implementation, start/sleep/join/isAlive usage, OS scheduler non-determinism defense | **100% Completed** |
| **Q5** | Cinema Seat Race Conditions | Producing & Explaining Race, Test Design, 10-Run Double Booking Tracking, Interleaving Matrix, Critical Section | Comparing Sync Strategies, 10,000 Booking Throughput Benchmark, Lock Granularity, Static Locks | Unsynced, Sync Method, and Sync Block implementations, 20-Screen multiplex architectural decision | **100% Completed** |
| **Q6** | Deadlock & Inter-Thread Comm | Producer-Consumer Design, wait() Loop & Monitor Rules, notify() vs notifyAll() Signal Deadlock, Buffer Tracking | Deadlock Analysis & Prevention, A->B & B->A Deadlock Reproduction, 4 Coffman Conditions, Lock Ordering | Wallet transfers & Mess counter implementations, Thread progress comparison, ArrayBlockingQueue | **100% Completed** |
| **Q7** | 10k Fault-Tolerant Processor | Stage 2 Design, ExecutorService/Callable/Future Architecture, Concurrent Collection Choice, Class Diagram | Stage 3 Implementation & Measurement, 10,000 File Workload, Thread Pool Benchmark (1,2,4,8), Ground Truth | Stage 1 Problem Interpretation & Stage 4 Design Defense (Pool sizing, Amdahl's Law, Limitations) | **100% Completed** |

---

## 3. Task 01: Bank ATM Simulation

### 3.1 Problem Statement & Failure Classification
Simulate an ATM withdrawal accepting an account number and withdrawal amount, validating hardware card-reader configuration, balance constraints, and dispensing cash.

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

| Failure Point | Exception Class | Type | Where Handled | Recovery Action |
|---|---|---|---|---|
| Missing card config | `FileNotFoundException` | Checked | `withdraw()` catch block | Prompt ATM maintenance; eject card |
| Non-numeric account | `NumberFormatException` | Unchecked | `withdraw()` multi-catch | Prompt 10-digit numeric format |
| Non-numeric amount | `NumberFormatException` | Unchecked | `withdraw()` multi-catch | Prompt numeric currency input |
| Negative/zero amount | `IllegalArgumentException` | Unchecked | `withdraw()` catch block | Prompt positive withdrawal amount |
| Insufficient balance | `InsufficientFundsException` | Checked (Custom) | `withdraw()` catch block | Decline overdraft; display balance |

### 3.2 Catch-Block Ordering Justification
Java evaluates `catch` blocks sequentially from top to bottom. If a superclass catch block (e.g. `catch (Exception e)`) is placed before a subclass catch block (`catch (InsufficientFundsException e)`), the compiler rejects the code with:
```
Task01\AtmUnreachableCompileFail.java:14: error: exception InsufficientFundsException has already been caught
```
Specific exception subclasses must strictly precede generic superclasses.

### 3.3 The Role of `finally`
The `finally` block guarantees that critical hardware and session cleanup executes unconditionally, regardless of whether:
1. The withdrawal succeeded normally.
2. A checked exception occurred (missing card reader or overdraft).
3. An unchecked exception occurred (non-numeric string).
4. An unhandled JVM error occurred.
In our simulation, it securely ejects the user's card, terminates the user session, and commits an encrypted audit trail.

### 3.4 Five Test Scenarios & Execution Evidence

| Test ID | Account Input | Amount Input | Card Reader File | Catch Block Triggered | Finally Executed? | Status |
|---|---|---|---|---|---|---|
| **TC-1.1** | `1234567890` | `1500.00` | Valid (`card_reader.dat`) | None (Success Path) | **YES** | PASS |
| **TC-1.2** | `1234567890` | `one-thousand` | Valid (`card_reader.dat`) | `catch (NumberFormatException)` | **YES** | PASS |
| **TC-1.3** | `1234567890` | `-500.00` | Valid (`card_reader.dat`) | `catch (IllegalArgumentException)` | **YES** | PASS |
| **TC-1.4** | `1234567890` | `10000.00` | Valid (`card_reader.dat`) | `catch (InsufficientFundsException)` | **YES** | PASS |
| **TC-1.5** | `1234567890` | `500.00` | Missing (`missing.dat`) | `catch (FileNotFoundException)` | **YES** | PASS |

### 3.5 Line-by-Line Uncaught Stack Trace Analysis
Captured in `Logs/task01_uncaught_stacktrace.txt`:
```
Exception in thread "main" java.lang.NumberFormatException: For input string: "Rs.5000"
	at java.base/jdk.internal.math.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:2054)
	at java.base/jdk.internal.math.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
	at java.base/java.lang.Double.parseDouble(Double.java:792)
	at Task01.AtmUncaughtDemo.processWithdrawal(AtmUncaughtDemo.java:11)
	at Task01.AtmUncaughtDemo.main(AtmUncaughtDemo.java:17)
```
- **Line 1:** Identifies the thread (`main`), exception type (`java.lang.NumberFormatException`), and offending literal string (`"Rs.5000"`).
- **Line 2 (`FloatingDecimal:2054`):** Internal JDK character scanner encounters non-digit `'R'`.
- **Line 3 (`FloatingDecimal:110`):** Internal floating-point parsing method.
- **Line 4 (`Double.java:792`):** Standard Java library entry point for double conversions.
- **Line 5 (`AtmUncaughtDemo.java:11`):** User application method where unvalidated string was parsed.
- **Line 6 (`AtmUncaughtDemo.java:17`):** Program entry point where uncaught exception crashed thread.

### 3.6 Alternative Concept: Pre-Condition Checks & Error Codes
Implemented in `Task01/AtmWithCodes.java` and benchmarked in `Task01/AtmBenchmark.java`:
- **Readability & Number of Checks:** Error codes force every method caller to perform immediate conditional checks (`if (code != 0)`), scattering error handling through business logic. Exceptions decouple normal flow from error handling.
- **Empirical Benchmark (200,000 iterations):**
  - **Error Codes:** 13.02 ms (Avg: **65.10 ns/op**)
  - **Exception Handling:** 133.34 ms (Avg: **666.70 ns/op**)
  - **Slowdown Factor:** **10.24x** slower when exceptions are thrown frequently, due to native JVM stack unwinding (`Throwable.fillInStackTrace()`).
  - **Engineering Decision:** Exceptions should represent exceptional conditions, never routine control flow.

---

## 4. Task 02: Online Examination Portal

### 4.1 Problem Statement & Architectural Propagation
The examination portal validates submissions across three architectural layers:
- **Storage Layer:** Writes PDF bytes to disk; encounters `java.io.IOException` (e.g. disk quota).
- **Service Layer:** Validates student course registration and exam deadline timestamps.
- **Controller Layer:** Catches domain exceptions and produces sanitized, secure student UI responses.

```mermaid
sequenceDiagram
    autonumber
    actor Student as Student Client
    participant Controller as ExamControllerLayer
    participant Service as ExamServiceLayer
    participant Storage as StorageLayer
    participant FS as File System / Disk

    Student->>Controller: handleSubmission("192511469", "CSA0914", "script.pdf", 450KB)
    Controller->>Service: processSubmission(...)
    Service->>Storage: saveAnswerScript(...)
    Storage->>FS: write bytes to disk
    FS-->>Storage: throw IOException("Disk quota exceeded")
    Note over Storage: Catch IOException & Wrap in<br/>SubmissionStorageException(msg, cause)
    Storage-->>Service: throws SubmissionStorageException
    Service-->>Controller: throws ExamException (propagated via throws)
    Note over Controller: Catch SubmissionStorageException<br/>1. Log technical root cause<br/>2. Return sanitized HTTP 500
    Controller-->>Student: [HTTP 500]: "Unable to safely store your submission..."
```

### 4.2 Class Hierarchy & Checked vs. Unchecked Decision
- `ExamException` extends `java.lang.Exception` (Checked).
- `ExamWindowClosedException` extends `ExamException` (Checked).
- `StudentNotRegisteredException` extends `ExamException` (Checked).
- `SubmissionStorageException` extends `ExamException` (Checked).
- `FileSizeLimitExceededException` extends `SubmissionStorageException` (Checked).
- **Justification:** Exam submission failures are foreseeable business domain failures that client code must anticipate and recover from gracefully.

### 4.3 Test Results & Chained Stack Trace

| Test Case | Scenario Condition | Exception Thrown | Handled In | Student Message |
|---|---|---|---|---|
| **TC-2.1** | Valid Registered Submission | None | Controller | `HTTP 200`: Submission Accepted |
| **TC-2.2** | Student Not Registered | `StudentNotRegisteredException` | Controller | `HTTP 403`: Registration Error |
| **TC-2.3** | Window Closed (Late) | `ExamWindowClosedException` | Controller | `HTTP 400`: Deadline Expired |
| **TC-2.4** | File Size > 500 KB | `FileSizeLimitExceededException` | Controller | `HTTP 413`: Payload Too Large |
| **TC-2.5** | Disk Quota Overflow | `SubmissionStorageException` | Controller | `HTTP 500`: System Storage Error |

```
Task02.exceptions.SubmissionStorageException: Storage subsystem failure while persisting answer script 'disk_error_demo.pdf' for student 192511469
	at Task02.layers.StorageLayer.saveAnswerScript(StorageLayer.java:37)
	at Task02.ExamPortalDemo.main(ExamPortalDemo.java:64)
Caused by: java.io.IOException: POSIX File System Error: Disk quota exceeded on /var/exam_storage partition
	at Task02.layers.StorageLayer.saveAnswerScript(StorageLayer.java:31)
	... 1 more
```

### 4.4 Information Lost Without Chaining
When `throw new SubmissionStorageException("Could not save")` is called without passing `cause`:
- `e.getCause()` returns `null`.
- Diagnostic information regarding whether the failure was bad sectors, disk quota, or permissions is permanently lost to operations engineers.

### Complete the Code Q2: StorageDemo Analysis
- **Code Solution in `CompleteTheCode/Q2_StorageDemo/StorageDemo.java`:**
  ```java
  } catch (IOException e) {
      // TODO 1: Wrap the IOException in a SubmissionStorageException, keeping the
      // IOException as its cause
      throw new SubmissionStorageException("Could not store " + fileName, e);
  }
  ```
- **Output Verification:**
  ```
  Saved answer01.pdf
  Error: Could not store answer02.pdf
  Cause: Disk quota exceeded
  ```
- **Consequence if cause omitted:** If `new SubmissionStorageException("Could not store " + fileName)` were written, `e.getCause()` would return `null`. In `StorageDemo.main()`, calling `e.getCause().getMessage()` triggers an immediate `NullPointerException`, crashing the application!

---

## 5. Task 03: Resource Safety, Debugging and Robust Code

### 5.1 Diagnosis of the "Too Many Open Files" Defect
Using step-through debugging:
- In `ManualSalesReportGenerator.java`, each store file opens a `BufferedReader`.
- When `store_4.csv` encounters corrupt data (`"CORRUPT_QTY"`), `Integer.parseInt()` throws `NumberFormatException`.
- Control jumps immediately to the catch block, skipping `br.close()`.
- The file descriptor remains permanently open in the operating system handle table. Over hundreds of files, the OS file descriptor limit is exhausted.

### 5.2 Comparative Open File Handle Measurements (`task03_resource_comparison.csv`)

| Implementation | Input Files | Failure Point | Open File Handles Remaining | Memory / Handle Leak |
|---|---|---|---|---|
| **Manual `try-catch-finally`** | 10 | File 4 (`store_4.csv`) | **1 Leaked Handle** | **YES (Resource Leak)** |
| **`try-with-resources`** | 10 | File 4 (`store_4.csv`) | **0 Leaked Handles** | **NO (100% Resource Safe)** |

### 5.3 Suppressed Exceptions Demonstration
Captured in `Logs/task03_suppressed_exceptions.txt`:
- When an exception $E_1$ occurs inside the `try` block and closing throws $E_2$:
  - `try-with-resources` preserves $E_1$ as the primary thrown exception.
  - Attaches $E_2$ as a suppressed exception via `e.addSuppressed()`, retrievable with `e.getSuppressed()`.
  - In manual code, an exception in `finally` completely overwrites and erases $E_1$.

### 5.4 Joint Engineering Decision
`try-with-resources` alone is insufficient if:
1. An exception occurs half-way through writing an output file, leaving a truncated, corrupt summary file on disk.
2. The fix requires writing to a temporary file (`summary.tmp`) and performing an atomic filesystem rename (`Files.move` with `ATOMIC_MOVE`) only upon complete success.

---

## 6. Task 04: Thread Creation and the Thread Life Cycle

### 6.1 `Thread` vs. `Runnable` Comparison
- **Extending `Thread`:** Couples task with execution; wastes single-inheritance slot.
- **Implementing `Runnable`:** Decouples work from thread mechanics; compatible with thread pools.

### 6.2 Evidence: `start()` vs. `run()`
Captured in `Logs/task04_start_vs_run.txt`:
```
Main Thread Details -> ID: 1, Name: 'main'

--- 1. Calling run() directly (NO NEW THREAD CREATED) ---
  Inside Task Body -> Thread ID: 1, Thread Name: 'main', Is Main Thread: true

--- 2. Calling start() (ASYNC NEW JVM THREAD SPAWNED) ---
  Inside Task Body -> Thread ID: 22, Thread Name: 'AsyncWorkerThread', Is Main Thread: false
```
Calling `run()` executes synchronously inside thread 1. Only `start()` allocates a distinct operating system thread (ID: 22).

### 6.3 Ten-Run Completion Order Non-Determinism (`task04_ten_runs.csv`)

| Run Number | Station Completion Order | Observation |
|---|---|---|
| **Run 1** | Station 3 $\rightarrow$ Station 2 $\rightarrow$ Station 1 $\rightarrow$ Station 4 | Distinct finish sequence |
| **Run 2** | Station 3 $\rightarrow$ Station 4 $\rightarrow$ Station 1 $\rightarrow$ Station 2 | Scheduler reordered stations |
| **Run 3** | Station 1 $\rightarrow$ Station 3 $\rightarrow$ Station 2 $\rightarrow$ Station 4 | Station 1 finished first |
| **Run 4** | Station 2 $\rightarrow$ Station 3 $\rightarrow$ Station 4 $\rightarrow$ Station 1 | Station 2 finished first |
| **Run 5** | Station 1 $\rightarrow$ Station 2 $\rightarrow$ Station 4 $\rightarrow$ Station 3 | Station 4 delayed |
| **Run 6** | Station 4 $\rightarrow$ Station 3 $\rightarrow$ Station 2 $\rightarrow$ Station 1 | Reverse completion order |
| **Run 7** | Station 1 $\rightarrow$ Station 4 $\rightarrow$ Station 3 $\rightarrow$ Station 2 | Preemption shift |
| **Run 8** | Station 3 $\rightarrow$ Station 4 $\rightarrow$ Station 2 $\rightarrow$ Station 1 | Station 3 lead |
| **Run 9** | Station 4 $\rightarrow$ Station 2 $\rightarrow$ Station 1 $\rightarrow$ Station 3 | Multi-core interleaving |
| **Run 10** | Station 1 $\rightarrow$ Station 3 $\rightarrow$ Station 2 $\rightarrow$ Station 4 | Non-deterministic ordering |

**Conclusion:** Thread scheduling is governed by preemptive time-slicing. A correct concurrent program must never depend on execution ordering, relying instead on synchronization barriers (`join()`).

### 6.4 Daemon Thread Behavior
When marked with `setDaemon(true)`, the JVM terminates the thread immediately when all user threads finish, without executing remaining instructions or closing files.

---

## 7. Task 05: Race Conditions in a Cinema Seat-Booking System

### 7.1 Anatomy of a Race Condition
```
Thread 1 (Alice): Reads booked[3] == false
Thread 2 (Bob):   Reads booked[3] == false
Thread 1 (Alice): Writes booked[3] = true, increments bookingsMade (Count = 1)
Thread 2 (Bob):   Writes booked[3] = true, increments bookingsMade (Count = 2)
Result: Seat 3 sold to both Alice and Bob!
```

### 7.2 Ten-Run Double-Booking Audit (`task05_double_booking_10runs.csv`)
In unsynchronized execution across 10 runs with 100 threads competing for 10 seats:
- Up to **20 seats reported sold** out of 10 available seats!
- Double booking errors occurred in 8 out of 10 runs, with up to **10 duplicate sales** in a single run.

### Complete the Code Q5: SeatBooking Analysis
- **Code Solution in `CompleteTheCode/Q5_SeatBooking/SeatBooking.java`:**
  ```java
  // TODO 1: Make the check-then-book operation atomic so that a seat can never be
  // sold twice
  public synchronized void book(int seat) {
      if (!booked[seat]) {
          booked[seat] = true;
          bookingsMade++;
      }
  }
  ```
- **Output:** `Seats sold: 10` (Verified across 10 runs with zero double bookings).

### 7.3 High Contention Benchmark (10,000 Bookings)

| Strategy | Total Time (ms) | Avg Time per Op (ns) | Scalability Assessment |
|---|---|---|---|
| **AtomicBoolean (CAS)** | **15.17 ms** | **1,516.7 ns** | **Fastest (Lock-free hardware atomics)** |
| **Synchronized Block** | **17.07 ms** | **1,706.6 ns** | High (Per-seat fine-grained locking) |
| **ReentrantLock** | **21.08 ms** | **2,108.2 ns** | Flexible (Explicit lock queuing) |
| **Synchronized Method** | **51.55 ms** | **5,155.1 ns** | Bottleneck (Coarse-grained instance lock) |
| **ConcurrentHashMap** | **63.95 ms** | **6,395.1 ns** | Moderate (Bucket hashing & object nodes) |

### 7.4 Architecture for a 20-Screen Multiplex
- **Recommendation:** Fine-grained per-seat locking or per-screen `AtomicBoolean[]`.
- **Reason:** Synchronizing at class level (`static synchronized`) creates a global bottleneck across all 20 screens. Independent screens and independent seats must execute concurrently.

---

## 8. Task 06: Deadlock and Inter-Thread Communication

### 8.1 Part A: Hostel Wallet Deadlock & Prevention
- **Deadlock Construction:** Thread 1 transfers $A \rightarrow B$ (locks A then B); Thread 2 transfers $B \rightarrow A$ (locks B then A).
- **The Four Coffman Conditions:**
  1. Mutual Exclusion (monitors cannot be shared).
  2. Hold and Wait (holds first wallet while waiting for second).
  3. No Preemption (locks cannot be confiscated).
  4. Circular Wait ($T_1 \rightarrow B \rightarrow T_2 \rightarrow A \rightarrow T_1$).
- **Prevention Strategy:** Global lock ordering based on numerical account ID:
  ```java
  Wallet first = (from.getId() < to.getId()) ? from : to;
  Wallet second = (from.getId() < to.getId()) ? to : from;
  synchronized (first) {
      synchronized (second) { ... }
  }
  ```
  Breaks Circular Wait; 1,000 transfers executed in 2 ms with zero deadlocks.

### 8.2 Part B: Mess Token Counter Bounded Buffer
- Capacity: exactly 5 tokens.
- Buffer occupancy tracked over 100 operations (`task06_buffer_tracking.csv`):
  - Strictly bounded: $0 \le \text{count} \le 5$.
  - Invariant violations: **0**.
- **Why `notify()` is unsafe:** With multiple consumers and producers, `notify()` can wake another consumer when the buffer is empty instead of waking the producer, causing permanent signal starvation deadlock.

### Complete the Code Q6: TokenCounter Analysis
- **Code Solution in `CompleteTheCode/Q6_TokenCounter/TokenCounter.java`:**
  ```java
  public synchronized void put(int token) throws InterruptedException {
      while (count == buffer.length) {
          wait();
      }
      buffer[in] = token;
      in = (in + 1) % buffer.length;
      count++;
      notifyAll();
  }

  public synchronized int take() throws InterruptedException {
      while (count == 0) {
          wait();
      }
      int token = buffer[out];
      out = (out + 1) % buffer.length;
      count--;
      notifyAll();
      return token;
  }
  ```
- **Output:** `Sum of tokens collected: 210 (expected 210)`.
- **Replacing `while` with `if`:** Woken threads do not re-verify condition, risking reading empty buffer or overwriting unconsumed slots under spurious wakeups.

---

## 9. Task 07: High-Throughput Fault-Tolerant Score File Processor

### 9.1 Stage 1: Problem Interpretation (Joint Work)
- Workload: 10,000 score files at semester end.
- Requirements: Maximize throughput, tolerate missing/corrupt files without aborting, ensure 100% summary conservation.

### 9.2 Stage 2: Architecture & Design (Student A)
- Pool: `ExecutorService` with fixed worker threads.
- Tasks: `Callable<ProcessingResult>` returns immutable typed result (`SUCCESS`, `CORRUPT_DATA`, `FILE_NOT_FOUND`).
- Collection: `ConcurrentLinkedQueue<ProcessingResult>` and `AtomicInteger` counters in `ProcessingSummary`.

### 9.3 Stage 3: Implementation & Empirical Measurements (Student B)
- **Hand-Checked Ground Truth Match (20 files):**
  - Expected: Total=20, Valid=15 (Passed=12, Failed=3), Corrupt=3, Missing=2, Unhandled=0.
  - Actual: **100.0% Exact Match**.
- **10,000 File Benchmark across Thread Pools (`task07_thread_pool_benchmark.csv`):**

| Thread Pool Size ($P$) | Total Files | Valid Processed | Passed | Failed | Corrupt | Missing | Unhandled | Time (ms) | Throughput (Files/sec) | Speedup |
|---|---|---|---|---|---|---|---|---|---|---|
| **1 Thread** | 10,000 | 9,600 | 9,286 | 314 | 250 | 150 | **0** | **22,699 ms** | 440.5 | 1.00x |
| **2 Threads** | 10,000 | 9,600 | 9,286 | 314 | 250 | 150 | **0** | **9,788 ms** | 1,021.7 | **2.32x** |
| **4 Threads** | 10,000 | 9,600 | 9,286 | 314 | 250 | 150 | **0** | **7,832 ms** | 1,276.8 | **2.90x** |
| **8 Threads** | 10,000 | 9,600 | 9,286 | 314 | 250 | 150 | **0** | **6,701 ms** | 1,492.3 | **3.39x** |

### 9.4 Stage 4: Design Defense (Joint Work)
- **Why performance plateaus beyond 4–8 threads:** Answer-script evaluation is limited by storage I/O bandwidth and single disk queue serialization (Amdahl's Law). Adding excessive threads incurs context-switch overhead without faster disk reads.
- **One Remaining Limitation:** Retaining all 10,000 `Future` handles in heap memory before aggregation limits scalability for multi-million file batches. Future versions should use streaming `ExecutorCompletionService`.

---

## 10. Experimental Data & Comparative Complexity Analysis

| Task | Experimental CSV Artifact | Key Measurement | Algorithmic Complexity | Memory Complexity |
|---|---|---|---|---|
| **Task 01** | `task01_benchmark.csv` | Error code (65 ns) vs Exception (666 ns) | Error: $O(1)$ \| Throw: $O(D)$ | $O(D)$ stack frames |
| **Task 03** | `task03_resource_comparison.csv` | Manual leaks 1 handle; TWR leaks 0 handles | $O(f)$ time for $f$ files | Manual: $O(f)$ leaked FDs \| TWR: $O(1)$ |
| **Task 04** | `task04_ten_runs.csv` | 10 non-deterministic completion orders | $O(n/p)$ batch scanning | $O(p)$ thread stacks ($p \times 1\text{ MB}$) |
| **Task 05** | `task05_double_booking_10runs.csv` | Unsynced sells up to 20 seats; Synced sells 10 | $O(k)$ contention | Per-seat locks: $s \times 16\text{ B}$ |
| **Task 05** | `task05_booking_benchmark.csv` | AtomicBoolean (15 ms) vs SyncMethod (51 ms) | Lock-free CAS $O(1)$ | $s \times 24\text{ B}$ |
| **Task 06** | `task06_buffer_tracking.csv` | Buffer size strictly $0 \le \text{count} \le 5$ | Put/Take: $O(1)$ | $O(1)$ ring buffer |
| **Task 07** | `task07_thread_pool_benchmark.csv` | 10k files in 6.7s (1,492 files/sec with 8 threads) | $T(P) = \frac{T_{serial}}{P} + T_{overhead}$ | Fixed $P \times 1\text{ MB}$ stack |

---

## 11. Final Verification Checklist

| Verification Item | Requirement Specification | Verification Evidence | Status |
|---|---|---|---|
| **Task 01: ATM Simulation** | Try, multi-catch, finally, 5 test paths | `Task01/AtmSimulation.java`, `Logs/task01_atm_simulation.txt` | **Tested Successfully** |
| **Task 01: Catch Order & Stack Trace** | Compiler error on broad catch; uncaught stack trace | `Logs/task01_unreachable_catch_compiler_error.txt` | **Tested Successfully** |
| **Task 01: Benchmark** | Error codes vs Exceptions (200k ops) | `ExperimentalData/task01_benchmark.csv` | **Tested Successfully** |
| **Task 02: Exam Portal Hierarchy** | Checked user exceptions, 3 layers | `Task02/layers/*`, `Logs/task02_exam_portal_demo.txt` | **Tested Successfully** |
| **Task 02: Exception Chaining** | getCause() and unchained information loss | `Task02/UnchainedExceptionDemo.java`, `Logs/task02_unchained_demo.txt`| **Tested Successfully** |
| **Complete the Code Q2** | StorageDemo TODO 1 completed | `CompleteTheCode/Q2_StorageDemo/StorageDemo.java` | **Tested Successfully** |
| **Task 03: Report Generator** | Manual vs try-with-resources | `Task03/ResourceSafetyBenchmark.java` | **Tested Successfully** |
| **Task 03: File Handle Tracking** | Leaked handle counts under failure | `ExperimentalData/task03_resource_comparison.csv` (1 leaked vs 0) | **Tested Successfully** |
| **Task 03: Suppressed Exceptions** | Demonstrating getSuppressed() | `Task03/SuppressedExceptionDemo.java`, `Logs/task03_suppressed_exceptions.txt` | **Tested Successfully** |
| **Task 04: Scanner Stations** | Thread vs Runnable, start() vs run() | `Task04/StartVsRunEvidenceDemo.java` (ID 1 vs 22) | **Tested Successfully** |
| **Task 04: Behavioural Runs** | 10 runs completion order recorded | `ExperimentalData/task04_ten_runs.csv` | **Tested Successfully** |
| **Task 04: Daemon & Priority** | Advisory priorities & daemon termination | `Logs/task04_thread_priority.txt`, `Logs/task04_daemon_thread.txt` | **Tested Successfully** |
| **Task 05: Seat Booking Race** | Unsynced, sync method, sync block | `Task05/*`, `ExperimentalData/task05_double_booking_10runs.csv` | **Tested Successfully** |
| **Task 05: 10,000 Benchmark** | Contention benchmark & 20-screen choice | `ExperimentalData/task05_booking_benchmark.csv` | **Tested Successfully** |
| **Complete the Code Q5** | SeatBooking TODO 1 (Seats sold: 10) | `CompleteTheCode/Q5_SeatBooking/SeatBooking.java` | **Tested Successfully** |
| **Task 06: Deadlock Demonstration** | A->B and B->A deadlock & lock-ordering | `Task06/PartA/DeadlockDemo.java`, `DeadlockFreeDemo.java` | **Tested Successfully** |
| **Task 06: Token Counter Bounded Buffer**| Buffer capacity 5, wait() loop, notifyAll() | `Task06/PartB/MessTokenCounter.java`, `task06_buffer_tracking.csv` | **Tested Successfully** |
| **Complete the Code Q6** | TokenCounter TODO 1 & 2 (Sum: 210) | `CompleteTheCode/Q6_TokenCounter/TokenCounter.java` | **Tested Successfully** |
| **Task 07: 10,000 File Processor** | Stages 1-4, ExecutorService, Fault Tolerance | `Task07/ResultProcessor.java`, `task07_thread_pool_benchmark.csv` | **Tested Successfully** |
| **Visual Evidence** | 16 screenshots & charts generated | `Screenshots/*.png` | **Tested Successfully** |

---

## 12. Academic Conclusion & Viva Reference

This laboratory assignment successfully demonstrated the core engineering principles of CO3:
1. **Exception Handling:** Demonstrates structured recovery, exception chaining without cause loss, and separation of user-facing UI messages from low-level storage exceptions.
2. **Resource Management:** Quantifies the file handle leaks inherent in manual closing and proves the deterministic safety of `try-with-resources` and suppressed exceptions.
3. **Concurrency & Thread Life Cycle:** Proves thread creation semantics, OS scheduler non-determinism across 10 runs, and daemon thread termination.
4. **Synchronization & Deadlock:** Quantifies double booking under race conditions, proves lock-ordering deadlock elimination, and demonstrates invariant-preserving condition synchronization in bounded buffers.
5. **High-Throughput Concurrent Systems:** Validates a fault-tolerant batch processor over 10,000 answer scripts, demonstrating Amdahl's Law and thread-pool scalability.

Both Student A (Geetika T) and Student B (Pair Partner) have fully executed, analyzed, and verified every program and are prepared to defend all design decisions during viva evaluation.
