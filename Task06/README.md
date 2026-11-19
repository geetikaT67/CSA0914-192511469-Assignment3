# Task 06: Deadlock and Inter-Thread Communication

**Subject:** Programming in Java (CSA0914)  
**Bloom's Level:** K4 – Analyse  
**Marks:** 7 Marks  
**Repository:** `CSA0914-192511469-Assignment3`

---

## 1. Problem Overview
This task investigates two fundamental concurrent coordination challenges:
- **Part A (Hostel Wallet Transfers):** Mutual deadlocks arising during financial transfers between student hostel wallets when multiple locks are acquired in inconsistent order.
- **Part B (Mess Token Counter):** Bounded-buffer coordination where a mess cook produces meal tokens into a counter with a strict maximum capacity of 5 tokens, and students consume them.

---

## 2. Shared Group Work
- **Part A:** Developed `Task06/PartA/DeadlockDemo.java` demonstrating reproducible deadlock between Wallet 101 and Wallet 102, and `Task06/PartA/DeadlockFreeDemo.java` eliminating deadlock via global lock-ordering.
- **Part B:** Developed `Task06/PartB/MessTokenCounter.java` using `wait()`, `notify()`, and `notifyAll()` with bounded buffer capacity of 5, logging buffer occupancy over 100 operations.

---

## 3. Student A – Producer-Consumer Design

### 3.1 Why `wait()` Must Be Called Inside a `while` Loop and Inside a Synchronized Block
1. **Inside a Synchronized Block:**
   - Calling `wait()` requires ownership of the object's monitor.
   - If invoked outside a `synchronized` block, the JVM immediately throws `java.lang.IllegalMonitorStateException`.
   - Atomicity requirement: Releasing the lock and entering the wait set must be executed as a single atomic operation by the JVM kernel.
2. **Inside a `while` Loop (Never `if`):**
   - **Spurious Wakeups:** The OS thread scheduler may wake up a sleeping thread without any explicit `notify()` signal.
   - **Condition Invalidation:** When `notifyAll()` is called, multiple waiting threads wake up. The first thread to re-acquire the monitor may consume the token, leaving the buffer empty. If a subsequent woken thread did not re-check the condition in a `while` loop, it would proceed to read from an empty buffer (`count = 0`), producing buffer underflow or index out-of-bounds corruption!

### 3.2 What Goes Wrong When `notify()` Is Used Instead of `notifyAll()`
Demonstrated in `Task06/PartB/NotifyVsNotifyAllExperiment.java`:
- When both consumers and producers share the same monitor lock:
  1. Buffer becomes empty (`count = 0`). Consumer 1 and Consumer 2 both call `wait()`.
  2. Producer puts 1 token into the buffer and calls `notify()`.
  3. Consumer 1 wakes up, consumes the token, and calls `notify()`.
  4. **The Critical Flaw:** The JVM arbitrarily selects Consumer 2 from the wait set instead of the Producer!
  5. Consumer 2 wakes up, checks `while(count == 0)`, finds the buffer empty, and immediately goes back to `wait()`.
  6. **Result:** Producer is waiting for space; Consumer 1 is waiting for tokens; Consumer 2 is waiting for tokens. **Every thread is permanently asleep—a complete signal starvation deadlock!**
- `notifyAll()` wakes all threads, guaranteeing that the producer is awakened.

### 3.3 Buffer Size Over Time Analysis (`ExperimentalData/task06_buffer_tracking.csv`)
100 operations recorded in `Task06/PartB/MessTokenCounter.java`:
- Minimum buffer count observed: **`0`**
- Maximum buffer count observed: **`5`**
- Invariant violation count: **`0`**
- Mathematical proof that $0 \le \text{count} \le 5$ holds invariant across all concurrent executions.

### 3.4 Difference Between `Thread.sleep()` and `Object.wait()`
| Characteristic | `Thread.sleep(millis)` | `Object.wait()` |
|---|---|---|
| **Class Origin** | `java.lang.Thread` static method | `java.lang.Object` final instance method |
| **Lock Handling** | **Holds all locks**; does not release monitors | **Releases the intrinsic monitor lock** to other threads |
| **Wakeup Mechanism** | Time expiration or `interrupt()` | Explicit `notify()`, `notifyAll()`, or timeout |
| **Context** | Anywhere in code | **Only inside a `synchronized` block/method** |
| **Primary Purpose** | Delay / polling pause | Inter-thread coordination and condition signaling |

---

## 4. Student B – Deadlock Analysis and Prevention

### 4.1 Reliable Deadlock Reproduction
Captured in `Logs/task06_deadlock_demo.txt`:
```
=== PART A: DEADLOCK REPRODUCTION DEMO ===
  [TransferThread-2(B->A)] ACQUIRED lock on Wallet-102[Rs 1000.0]. Waiting for lock on Wallet-101[Rs 1000.0]...
  [TransferThread-1(A->B)] ACQUIRED lock on Wallet-101[Rs 1000.0]. Waiting for lock on Wallet-102[Rs 1000.0]...

[WATCHDOG INSPECTION]:
  TransferThread-1(A->B) State: BLOCKED
  TransferThread-2(B->A) State: BLOCKED
>>> DEADLOCK CONFIRMED: Both threads are permanently BLOCKED waiting on each other's locks!
```

### 4.2 The Four Coffman Conditions Present
Deadlock occurs if and only if all four conditions hold simultaneously:
1. **Mutual Exclusion:** Each wallet monitor lock can only be held by one thread at a time.
2. **Hold and Wait:** Thread 1 holds lock on Wallet A while waiting to acquire lock on Wallet B; Thread 2 holds lock on Wallet B while waiting for Wallet A.
3. **No Preemption:** Neither thread can forcibly confiscate or preempt the lock held by the other thread.
4. **Circular Wait:** A closed dependency loop exists: $T_1 \rightarrow \text{holds } A \rightarrow \text{waiting for } B \rightarrow \text{held by } T_2 \rightarrow \text{waiting for } A$.

### 4.3 Deadlock Prevention via Consistent Lock Ordering
In `Task06/PartA/DeadlockFreeDemo.java`:
```java
Wallet firstLock = (from.getAccountId() < to.getAccountId()) ? from : to;
Wallet secondLock = (from.getAccountId() < to.getAccountId()) ? to : from;

synchronized (firstLock) {
    synchronized (secondLock) {
        // Execute safe transfer
    }
}
```
- **Why It Works:** By enforcing a strict numerical ordering (lower account ID is always acquired first), every thread acquires Wallet 101 before Wallet 102.
- A circular dependency cycle ($A \rightarrow B \rightarrow A$) becomes mathematically impossible, completely breaking the **Circular Wait** condition.
- **Verification:** 1,000 reciprocal transfers executed in 2 ms with zero deadlocks and total money conserved.

---

## 5. Joint Conclusion
**What do deadlock and the producer–consumer problem have in common, and how does each design ensure that threads make progress?**
- **Commonality:** Both deal with multi-threaded resource contention and mutual dependency. If unmanaged, both can cause permanent thread stalling (deadlock in wallet transfers; signal starvation / buffer deadlock in producer-consumer).
- **Progress Assurance:**
  - In wallet transfers, **strict lock-ordering** eliminates circular waiting, guaranteeing finite acquisition time.
  - In producer-consumer, **condition synchronization (`wait()`/`notifyAll()`)** decouples buffer capacity from thread execution, ensuring threads only block when physically necessary and wake up immediately when state changes.

---

## 6. Complete the Code – Q6 (`TokenCounter.java`)
Located in `CompleteTheCode/Q6_TokenCounter/TokenCounter.java`:
- **Completed Code:**
  ```java
  public synchronized void put(int token) throws InterruptedException {
      // TODO 1: Wait while the counter is full, add the token at position in, then
      // wake waiting threads
      while (count == buffer.length) {
          wait();
      }
      buffer[in] = token;
      in = (in + 1) % buffer.length;
      count++;
      notifyAll();
  }

  public synchronized int take() throws InterruptedException {
      // TODO 2: Wait while the counter is empty, remove the token at position out,
      // wake waiting threads and return the token
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
- **Execution Verification:**
  ```
  Sum of tokens collected: 210 (expected 210)
  ```
  Matches expected output in the specification.
- **Replacing `while` with `if`:**
  In `CompleteTheCode/Q6_TokenCounter/TokenCounterWithIf.java`, replacing `while` with `if` works in simple 1-producer 2-consumer cases only if no spurious wakeups occur. However, under higher contention, woken threads skip re-checking `count == 0`, attempting to read `buffer[out]` when empty, yielding array corruption or overwritten token sums.

---

## 7. Alternative Concept & Complexity Analysis

### 7.1 Modern Alternatives
1. **`ArrayBlockingQueue<Integer>(5)`:** Replaces low-level `wait()`/`notifyAll()` with thread-safe ring-buffer methods `put()` and `take()`. Internally uses separate `notFull` and `notEmpty` `Condition` variables on a single `ReentrantLock`.
2. **`tryLock()` with Timeout:** Replaces lock ordering by allowing threads to attempt lock acquisition for a bounded timeout (e.g. 100 ms). If the second lock cannot be acquired, the thread releases the first lock, backs off, and retries, breaking the "Hold and Wait" condition.

### 7.2 Time, Space, and Error-Proneness Comparison
| Approach | Time Complexity per Op | Space Overhead | Error-Proneness |
|---|---|---|---|
| **Manual `wait()`/`notifyAll()`** | $O(1)$ monitor synchronization | Minimal ($0$ extra wrapper objects) | **High** (easy to forget loop, wrong notify, lost signals) |
| **`ArrayBlockingQueue(5)`** | $O(1)$ dual condition queues | Small (array + 2 `Condition` objects) | **Very Low** (rock solid JDK implementation) |
| **Lock-Ordering (`synchronized`)** | $O(1)$ deterministic locks | $0$ extra space | Low (requires consistent ID sorting) |
| **`tryLock()` with Timeout** | $O(1)$ to $O(k)$ retries | Moderate (`ReentrantLock` instances) | Medium (potential livelock if retries align) |
