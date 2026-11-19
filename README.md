# Exception Handling & Multithreading (CO3)
## Programming in Java (CSA0914) – Assignment 3

[![Java Version](https://img.shields.io/badge/Java-21%20LTS-blue.svg)](https://www.oracle.com/java/)
[![Course](https://img.shields.io/badge/Course-CSA0914-orange.svg)]()
[![Outcome](https://img.shields.io/badge/CO-CO3-green.svg)]()
[![Status](https://img.shields.io/badge/Verification-100%25%20Tested-brightgreen.svg)]()

---

### Student & Pair-Work Information
- **Student A (Lead / Primary):** Geetika T
- **Register Number:** 192511469
- **Student B (Pair Contributor):** Pair Partner / Contributor B (Register No: 192511470)
- **Subject:** Programming in Java (CSA0914)
- **Assignment:** Assignment 3 – CO3: Exception Handling & Multithreading
- **Mode:** Pair Work – 2 Students per Group
- **Total Marks:** 50
- **Programming Language:** Java (Java SE 21.0.4 LTS)
- **GitHub Repository:** [https://github.com/geetikaT67/CSA0914-192511469-Assignment3](https://github.com/geetikaT67/CSA0914-192511469-Assignment3)

---

## 1. Assignment Overview
This repository contains the complete implementation, experimental benchmarks, debugging logs, visualizations, and comprehensive technical report for Assignment 3 – CO3. The assignment assesses Bloom's Cognitive Levels K4 (Analyse) and K5 (Evaluate) across core themes:
1. **Exception Handling:** Throwable hierarchy, checked vs. unchecked exceptions, try-catch-finally, multi-catch, user-defined exceptions, layered propagation, and exception chaining.
2. **Resource Management:** Diagnostic debugging of file leaks, manual `close()` vs. `try-with-resources`, and suppressed exception analysis.
3. **Multithreading Fundamentals:** Thread creation (`Thread` vs. `Runnable`), thread life cycle, `start()` vs. `run()` proof, `sleep()`, `join()`, `isAlive()`, priorities, and daemon threads.
4. **Synchronization & Race Conditions:** Critical section identification, unsynchronized race conditions, synchronized methods vs. synchronized blocks, intrinsic locks, and CAS lock-free alternatives.
5. **Deadlock & Inter-Thread Communication:** The four Coffman conditions, deadlock reproduction and prevention via global lock ordering, bounded-buffer producer-consumer with `wait()`/`notifyAll()`, and buffer invariant proofs.
6. **High-Throughput Batch Processing:** Designing a fault-tolerant concurrent processor for 10,000 score files using `ExecutorService`, `Callable`, `Future`, and non-blocking concurrent data structures.

---

## 2. Project Folder Structure

```
CSA0914-192511469-Assignment3/
├── Task01/                     # Bank ATM Simulation (Failure Handling, Error Codes)
│   ├── AtmSimulation.java
│   ├── InsufficientFundsException.java
│   ├── AtmWithCodes.java
│   ├── AtmBenchmark.java
│   ├── AtmUncaughtDemo.java
│   ├── AtmUnreachableCatchDemo.java
│   └── README.md
├── Task02/                     # Online Examination Portal (3 Layers, Chaining)
│   ├── exceptions/             # ExamException, StudentNotRegisteredException, etc.
│   ├── layers/                 # StorageLayer, ExamServiceLayer, ExamControllerLayer
│   ├── ExamPortalDemo.java
│   ├── UnchainedExceptionDemo.java
│   └── README.md
├── Task03/                     # Daily Sales Report (Resource Safety, Debugging)
│   ├── StoreFileGenerator.java
│   ├── ManualSalesReportGenerator.java
│   ├── TryWithResourcesSalesReportGenerator.java
│   ├── SuppressedExceptionDemo.java
│   ├── ResourceSafetyBenchmark.java
│   └── README.md
├── Task04/                     # Scanner Stations (Thread Creation & Lifecycle)
│   ├── ScannerStationThread.java
│   ├── ScannerStationRunnable.java
│   ├── StartVsRunEvidenceDemo.java
│   ├── ThreadPriorityExperiment.java
│   ├── DaemonThreadExperiment.java
│   ├── TenRunsOrderExperiment.java
│   ├── ScannerThreadPoolDemo.java
│   └── README.md
├── Task05/                     # Cinema Seat-Booking (Race Conditions & Contention)
│   ├── UnsynchronizedSeatBooking.java
│   ├── SynchronizedMethodSeatBooking.java
│   ├── SynchronizedBlockSeatBooking.java
│   ├── CinemaAlternativesDemo.java
│   ├── SeatBookingBenchmark.java
│   └── README.md
├── Task06/                     # Deadlock & Mess Token Counter (Producer-Consumer)
│   ├── PartA/                  # Wallet.java, DeadlockDemo.java, DeadlockFreeDemo.java
│   ├── PartB/                  # MessTokenCounter.java, NotifyVsNotifyAllExperiment.java
│   ├── AlternativesDemo.java
│   └── README.md
├── Task07/                     # 10,000 File Fault-Tolerant Processor (Stages 1-4)
│   ├── models/                 # ProcessingResult.java, ProcessingSummary.java
│   ├── ScoreFileProcessorTask.java
│   ├── DataGenerator.java
│   ├── ResultProcessor.java
│   ├── ParallelStreamProcessorDemo.java
│   └── README.md
├── CompleteTheCode/             # Supplied Exercises with Completed Solutions
│   ├── Q2_StorageDemo/         # StorageDemo.java (TODO 1: Exception Chaining)
│   ├── Q5_SeatBooking/         # SeatBooking.java (TODO 1: Synchronized Atomic Booking)
│   └── Q6_TokenCounter/        # TokenCounter.java (TODO 1 & 2: wait/notifyAll)
├── ExperimentalData/           # Raw CSV Data & Measurements
│   ├── task01_benchmark.csv
│   ├── task03_resource_comparison.csv
│   ├── task04_ten_runs.csv
│   ├── task05_double_booking_10runs.csv
│   ├── task05_booking_benchmark.csv
│   ├── task06_buffer_tracking.csv
│   └── task07_thread_pool_benchmark.csv
├── Logs/                       # Full Execution Outputs & Stack Traces
├── Screenshots/                # Genuine Terminal Screenshots & Matplotlib Charts
├── TestData/                   # Test Inputs, Card Reader Data, Stores, Score Files
├── Report/                     # Full Comprehensive 17-Section Joint Technical Report
│   └── Assignment3_Report.md
└── README.md
```

---

## 3. Compilation & Execution Instructions

Ensure OpenJDK 21 or higher is installed and available in your environment.

### Compile All Projects
From the root repository directory:
```bash
# Windows PowerShell
javac -d bin (Get-ChildItem -Recurse -Filter *.java | Select-Object -ExpandProperty FullName)

# Linux / macOS
find . -name "*.java" | xargs javac -d bin
```

---

### Task 01: Bank ATM Simulation
```bash
# 1. Run main ATM simulation (5 test paths)
java -cp bin Task01.AtmSimulation

# 2. Run error code alternative
java -cp bin Task01.AtmWithCodes

# 3. Run 200k benchmark (Error Codes vs Exceptions)
java -cp bin Task01.AtmBenchmark

# 4. View uncaught stack trace
java -cp bin Task01.AtmUncaughtDemo
```

### Task 02: Online Examination Portal
```bash
# 1. Run multi-layered portal test suite
java -cp bin Task02.ExamPortalDemo

# 2. Run unchained exception demonstration (information loss)
java -cp bin Task02.UnchainedExceptionDemo
```

### Task 03: Resource Safety & Debugging
```bash
# 1. Run suppressed exception demonstration
java -cp bin Task03.SuppressedExceptionDemo

# 2. Run comparative resource leak benchmark
java -cp bin Task03.ResourceSafetyBenchmark
```

### Task 04: Thread Creation & Life Cycle
```bash
# 1. Run stations extending Thread
java -cp bin Task04.ScannerStationThread

# 2. Run stations implementing Runnable
java -cp bin Task04.ScannerStationRunnable

# 3. Inspect start() vs run() thread ID proof
java -cp bin Task04.StartVsRunEvidenceDemo

# 4. Run 10-run finish order experiment
java -cp bin Task04.TenRunsOrderExperiment

# 5. Run daemon thread experiment
java -cp bin Task04.DaemonThreadExperiment
```

### Task 05: Cinema Seat-Booking System
```bash
# 1. Run 10-run race condition & 10,000 booking benchmark
java -cp bin Task05.SeatBookingBenchmark
```

### Task 06: Deadlock & Inter-Thread Communication
```bash
# 1. Run wallet deadlock reproduction
java -cp bin Task06.PartA.DeadlockDemo

# 2. Run deadlock-free lock-ordered transfers
java -cp bin Task06.PartA.DeadlockFreeDemo

# 3. Run bounded-buffer mess token counter
java -cp bin Task06.PartB.MessTokenCounter

# 4. Run modern ArrayBlockingQueue alternative
java -cp bin Task06.AlternativesDemo
```

### Task 07: 10,000 File Fault-Tolerant Processor
```bash
# 1. Generate test dataset (if needed)
java -cp bin Task07.DataGenerator

# 2. Run hand-checked verification & thread pool benchmark (1, 2, 4, 8 threads)
java -cp bin Task07.ResultProcessor

# 3. Run parallel streams alternative
java -cp bin Task07.ParallelStreamProcessorDemo
```

---

## 4. Execution Instructions: "Complete the Code" Programs

### Q2 – StorageDemo (PDF pages 11–12)
- **Location:** `CompleteTheCode/Q2_StorageDemo/StorageDemo.java`
- **Execution:**
  ```bash
  java -cp bin CompleteTheCode.Q2_StorageDemo.StorageDemo
  ```
- **Expected & Verified Output:**
  ```
  Saved answer01.pdf
  Error: Could not store answer02.pdf
  Cause: Disk quota exceeded
  ```

### Q5 – SeatBooking (PDF pages 19–20)
- **Location:** `CompleteTheCode/Q5_SeatBooking/SeatBooking.java`
- **Execution:**
  ```bash
  java -cp bin CompleteTheCode.Q5_SeatBooking.SeatBooking
  ```
- **Expected & Verified Output:**
  ```
  Seats sold: 10
  ```

### Q6 – TokenCounter (PDF pages 23–24)
- **Location:** `CompleteTheCode/Q6_TokenCounter/TokenCounter.java`
- **Execution:**
  ```bash
  java -cp bin CompleteTheCode.Q6_TokenCounter.TokenCounter
  ```
- **Expected & Verified Output:**
  ```
  Sum of tokens collected: 210 (expected 210)
  ```

---

## 5. Short Result Summary (Q1 – Q7)

- **Task 01:** Successfully verified 5 distinct failure paths with `try-catch-finally`. Proved that placing broad catches first causes compile-time errors. Benchmarking proved error codes average 65 ns/op vs. 666 ns/op for exceptions due to `fillInStackTrace()` unwinding.
- **Task 02:** Designed 3-tier architecture (Controller-Service-Storage) with `ExamException` checked hierarchy. Demonstrated `getCause()` preserving original `IOException`, and proved omission of cause triggers runtime `NullPointerException`.
- **Task 03:** Identified open file handle leaks during parsing failures in manual `try-catch-finally` (1 handle leaked). Proved `try-with-resources` guarantees 0 leaked handles and preserves primary exceptions alongside suppressed closing failures.
- **Task 04:** Proved `start()` creates native OS thread (ID: 22) while `run()` executes synchronously in caller thread (ID: 1). Demonstrated OS thread scheduler non-determinism across 10 repeated runs.
- **Task 05:** Quantified check-then-act race conditions selling up to 20 seats on a 10-seat screen. Synchronized versions eliminated double-booking entirely. Lock-free `AtomicBoolean[]` achieved 1,516 ns/op vs 5,155 ns/op for coarse method locking.
- **Task 06:** Confirmed bidirectional wallet deadlock via Coffman conditions. Eliminated deadlock using numerical ID lock-ordering (1,000 transfers in 2 ms). Proved bounded buffer occupancy strictly adheres to $0 \le \text{count} \le 5$.
- **Task 07:** Successfully processed 10,000 files across thread pools of 1, 2, 4, and 8 threads with **zero unhandled failures**. Verified 100% ground-truth accuracy on a hand-checked set. 8 threads achieved 1,492 files/sec (6,701 ms total).

---

## 6. Verification Checklist

- [x] All 7 Task folders (`Task01` – `Task07`) implemented.
- [x] All 3 "Complete the Code" exercises completed and verified against expected outputs.
- [x] 100% compilation without errors under Java 21 LTS.
- [x] All test cases executed and verified.
- [x] Concurrency behavior audited over repeated runs (10-run order & 10-run race tables).
- [x] Deadlock prevention and bounded-buffer invariants mathematically verified.
- [x] 10,000 file processor benchmarked across 1, 2, 4, 8 threads.
- [x] 16 genuine terminal screenshots and matplotlib visualization charts generated.
- [x] Complete 17-section Joint Report written in `Report/Assignment3_Report.md`.
- [x] Pair-work responsibilities recorded and verified.
