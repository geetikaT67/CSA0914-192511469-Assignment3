# Task 05: Race Conditions in a Cinema Seat-Booking System

**Subject:** Programming in Java (CSA0914)  
**Bloom's Level:** K4 – Analyse  
**Marks:** 6 Marks  
**Repository:** `CSA0914-192511469-Assignment3`

---

## 1. Problem Overview
In an online multiplex cinema seat-booking system, thousands of concurrent users attempt to reserve seats across cinema screens simultaneously. In the naive implementation, the booking transaction executes a non-atomic "check-then-act" sequence:
1. Check if `booked[seat] == false`
2. If free, mark `booked[seat] = true`
3. Increment `bookingsMade++`

When multiple threads execute this sequence concurrently without synchronization, race conditions arise, leading to double-booked seats (same seat sold to multiple customers) and corrupted financial tallies.

---

## 2. Shared Group Work
### Synchronization Implementations
1. **Unsynchronized Booking (`Task05/UnsynchronizedSeatBooking.java`):** Non-atomic check-then-act; exposes severe race conditions.
2. **Synchronized Method (`Task05/SynchronizedMethodSeatBooking.java`):** Synchronizes on `this` instance monitor for the entire duration of `book(seat)`.
3. **Synchronized Block (`Task05/SynchronizedBlockSeatBooking.java`):** Employs fine-grained per-seat locks (`seatLocks[seat]`) to minimize lock contention and maximize parallelism.

---

## 3. Student A – Producing and Explaining the Race

### 3.1 Designing the Test to Reproduce Double Booking Reliably
To reliably expose the race window in software testing:
- 100 concurrent threads compete for only 10 seats (`seat = threadId % 10`), ensuring 10 simultaneous competitors per seat.
- A microscopic 1 ms delay (`Thread.sleep(1)`) or thread yield is placed between the boolean inspection `if (!booked[seat])` and the state assignment `booked[seat] = true`.
- This ensures that the operating system preempts Thread 1 after it reads `booked[seat] == false` but before it sets `booked[seat] = true`.

### 3.2 10-Run Double Booking Observations (`ExperimentalData/task05_double_booking_10runs.csv`)

| Run # | Total Seats | Concurrent Competitors | Seats Reported Sold | Double Bookings Occurred | Status |
|---|---|---|---|---|---|
| **Run 1** | 10 | 100 | 9 | 0 | Race in counter |
| **Run 2** | 10 | 100 | 10 | 0 | Normal interleaved |
| **Run 3** | 10 | 100 | 15 | **5 Duplicate Seats** | **SEVERE RACE** |
| **Run 4** | 10 | 100 | 19 | **9 Duplicate Seats** | **SEVERE RACE** |
| **Run 5** | 10 | 100 | 12 | **2 Duplicate Seats** | **SEVERE RACE** |
| **Run 6** | 10 | 100 | 16 | **6 Duplicate Seats** | **SEVERE RACE** |
| **Run 7** | 10 | 100 | 20 | **10 Duplicate Seats (100% Error)** | **SEVERE RACE** |
| **Run 8** | 10 | 100 | 19 | **9 Duplicate Seats** | **SEVERE RACE** |
| **Run 9** | 10 | 100 | 17 | **7 Duplicate Seats** | **SEVERE RACE** |
| **Run 10** | 10 | 100 | 17 | **7 Duplicate Seats** | **SEVERE RACE** |

### 3.3 Step-by-Step Thread Interleaving (Race Condition Anatomy)

| Step | Thread 1 ($T_1$) - User Alice | Thread 2 ($T_2$) - User Bob | Shared State (`booked[3]`) | `bookingsMade` | Anomaly |
|---|---|---|---|---|---|
| 1 | Checks `!booked[3]` $\rightarrow$ evaluates `true` | Ready in scheduler queue | `false` | `0` | Both target Seat #3 |
| 2 | *Preempted by OS scheduler* | Dispatched by OS scheduler | `false` | `0` | $T_1$ paused mid-check |
| 3 | Paused in ready queue | Checks `!booked[3]` $\rightarrow$ evaluates `true` | `false` | `0` | $T_2$ also reads stale `false` |
| 4 | Paused in ready queue | Sets `booked[3] = true` | `true` | `0` | $T_2$ marks booked |
| 5 | Paused in ready queue | Increments `bookingsMade++` | `true` | `1` | $T_2$ successfully books |
| 6 | Resumed by OS scheduler | Exits method | `true` | `1` | $T_1$ resumes with cached `true` |
| 7 | Sets `booked[3] = true` | Completed | `true` | `1` | Overwrites already true! |
| 8 | Increments `bookingsMade++` | Completed | `true` | **`2`** | **CRITICAL FAULT: Seat #3 sold twice!** |

### 3.4 Identification of the Exact Critical Section
```java
// CRITICAL SECTION BOUNDARY
if (!booked[seat]) {          // 1. Read shared state
    booked[seat] = true;      // 2. Mutate shared state
    bookingsMade++;           // 3. Mutate shared counter (non-atomic read-modify-write)
}
// END CRITICAL SECTION
```
The critical section encompasses lines that perform a **check-then-act** pattern and a **read-modify-write** (`bookingsMade++`). Without atomicity, multiple threads can execute inside this window simultaneously.

---

## 4. Student B – Comparing Synchronization Strategies

### 4.1 High Contention Benchmark (10,000 Bookings across 16 Threads)
Measured in `Task05/SeatBookingBenchmark.java` (`ExperimentalData/task05_booking_benchmark.csv`):

| Strategy | Total Execution Time (ms) | Average Time per Op (ns) | Relative Speed | Contention Behavior |
|---|---|---|---|---|
| **AtomicBoolean (CAS)** | **15.17 ms** | **1,516.7 ns** | **1.0x (Fastest)** | Lock-free hardware atomic primitives |
| **Synchronized Block** | **17.07 ms** | **1,706.6 ns** | 1.1x | Fine-grained per-seat locking |
| **ReentrantLock** | **21.08 ms** | **2,108.2 ns** | 1.4x | Explicit lock acquisition/release |
| **Synchronized Method** | **51.55 ms** | **5,155.1 ns** | 3.4x Slower | Coarse-grained bottleneck on `this` |
| **ConcurrentHashMap** | **63.95 ms** | **6,395.1 ns** | 4.2x Slower | Dynamic segment hashing & node allocations |

### 4.2 Locking Whole Method vs. Locking Critical Section
- **Whole Method (`synchronized public void book()`):** Serializes all threads entering the method. If User A wants Seat 1 and User B wants Seat 50, User B must block waiting for User A, causing artificial queuing and severe CPU thread stalling.
- **Critical Section Block (`synchronized(seatLocks[seat])`):** Threads booking different seats execute **in true parallel on separate CPU cores**. Contention only occurs when two users genuinely attempt to reserve the exact same seat.

### 4.3 Static Synchronization vs. Instance Locks
- **Instance Synchronization (`synchronized void book()`):** Locks on `this` (the individual `SeatBooking` screen instance). Screen 1 and Screen 2 have separate monitors and do not block each other.
- **Static Synchronization (`public static synchronized void book()`):** Locks on the `SeatBooking.class` object across the entire JVM. This creates a global bottleneck where bookings for Screen 1 completely block bookings for Screen 2 through Screen 20!

### 4.4 Intrinsic Lock Details
In Java, every `java.lang.Object` possesses an internal data structure called an **intrinsic lock** or **monitor**:
- In `public synchronized void book()`, the monitor associated with `this` is acquired before entering and released upon return or exception.
- In `synchronized(seatLocks[seat])`, the monitor associated with the distinct `Object` located at index `seat` is acquired.

---

## 5. Joint Engineering Decision
**Which synchronization strategy would you deploy for a cinema with 20 screens, and why?**
- **Recommended Architecture:** **Per-Seat `AtomicBoolean` array within each Screen instance, or fine-grained per-seat locks.**
- **Justification:**
  1. Cinema screens operate independently. Screen 1 bookings should never block Screen 20 bookings. Therefore, never use static synchronization.
  2. Within a screen of 200 seats, booking Seat A1 should not block Seat H12. Using an `AtomicBoolean[]` or per-seat lock allows up to 200 concurrent users to book seats in parallel per screen.
  3. Across 20 screens, this architecture achieves maximum hardware concurrency, sub-millisecond response latency, and zero double bookings.

---

## 6. Complete the Code – Q5 (`SeatBooking.java`)
Located in `CompleteTheCode/Q5_SeatBooking/SeatBooking.java`:
- **Completed Code:**
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
- **Execution Output:**
  ```
  Seats sold: 10
  ```
  Matches expected output in the specification.
- **Why Unsynchronized Version Sells More Than 10 Seats:**
  In `SeatBookingUnsynced`, multiple threads evaluate `if (!booked[seat])` as `true` simultaneously before any single thread updates the array. Consequently, multiple threads enter the body, execute `booked[seat] = true` redundantly, and execute `bookingsMade++` multiple times for the same seat, selling up to 20 seats on a 10-seat screen.

---

## 7. Alternative Concept & Complexity Analysis

### 7.1 Alternative Implementations
1. **`AtomicBoolean[]` using `compareAndSet(false, true)`:**
   ```java
   if (seats[seat].compareAndSet(false, true)) {
       bookingsCount.incrementAndGet();
       return true;
   }
   return false;
   ```
2. **`ReentrantLock[]` per seat:**
   Allows timed acquisition (`tryLock(500, TimeUnit.MILLISECONDS)`) and fair queuing.
3. **`ConcurrentHashMap<Integer, String>.putIfAbsent(seat, userId)`:**
   Dynamic map storage, useful for sparse seat layouts.

### 7.2 Time and Space Complexity for $s$ Seats
| Approach | Time per Booking under Contention | Extra Memory Space for $s$ Seats |
|---|---|---|
| **Synchronized Method** | $O(k)$ where $k$ is number of competing threads (serialized) | $0$ extra bytes (uses existing instance monitor) |
| **Synchronized Block (Per-Seat)** | $O(1)$ when booking distinct seats | $s \times 16\text{ bytes}$ (array of empty lock objects) |
| **`AtomicBoolean[]`** | $O(1)$ lock-free hardware CAS | $s \times 24\text{ bytes}$ (`AtomicBoolean` object wrappers) |
| **`ReentrantLock[]`** | $O(1)$ when distinct; low overhead queuing | $s \times 48\text{ bytes}$ (`ReentrantLock` + AQS sync objects) |
| **`ConcurrentHashMap`** | $O(1)$ hash bucket lookup + CAS | $s \times 64\text{ bytes}$ (Map `Node` objects + bucket table) |
