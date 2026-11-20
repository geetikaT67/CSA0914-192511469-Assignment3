import os
import sys
from reportlab.lib.pagesizes import A4
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image, PageBreak, KeepTogether
)
from pdf_helpers import (
    AcademicNumberedCanvas, TOCRegister, toc_page_numbers,
    title_style, subtitle_style, h1_style, h2_style, h3_style, body_style,
    bullet_style, caption_style, viva_q_style, viva_a_style,
    table_text, table_header, make_code_box, make_image_flowable
)

PDF_OUTPUT_PATH = "Final_PDF/Assignment3_CO3_Important_Programs_and_Screenshots.pdf"

def build_pdf_story(is_second_pass=False):
    story = []

    # =========================================================================
    # 1. COVER PAGE
    # =========================================================================
    story.append(Spacer(1, 15))
    story.append(Paragraph("<b>DEPARTMENT OF COMPUTER SCIENCE & ENGINEERING</b>", ParagraphStyle('CoverDept', parent=subtitle_style, fontSize=11, fontName='Times-Bold', spaceAfter=4)))
    story.append(Paragraph("COURSE: PROGRAMMING IN JAVA (COURSE CODE: CSA0914)", ParagraphStyle('CoverCourse', parent=subtitle_style, fontSize=10, fontName='Times-Roman', spaceAfter=25)))

    story.append(Paragraph("ASSIGNMENT 3 – CO3<br/>EXCEPTION HANDLING & MULTITHREADING", title_style))
    story.append(Paragraph("A Comprehensive Academic Laboratory Report on Robust Error Handling, Resource Safety, Multithreading Primitives, Deadlock Prevention, and Concurrent Systems", subtitle_style))
    story.append(Spacer(1, 25))

    # Student & Project Metadata Box
    meta_data = [
        [Paragraph("<b>Parameter</b>", table_header), Paragraph("<b>Academic & Repository Details</b>", table_header)],
        [Paragraph("<b>Student Name</b>", table_text), Paragraph("Geetika T", table_text)],
        [Paragraph("<b>Register Number</b>", table_text), Paragraph("192511469", table_text)],
        [Paragraph("<b>Course & Degree</b>", table_text), Paragraph("Programming in Java (CSA0914) — B.Tech Computer Science & Engineering", table_text)],
        [Paragraph("<b>Course Outcome (CO)</b>", table_text), Paragraph("CO3: Exception Handling & Multithreading (Sessions 27–38)", table_text)],
        [Paragraph("<b>Bloom's Cognitive Levels</b>", table_text), Paragraph("K4 – Analyse &bull; K5 – Evaluate", table_text)],
        [Paragraph("<b>Assignment Mode</b>", table_text), Paragraph("Pair Work (Lead: Geetika T [192511469] | Contributor: Contributor B [192511470])", table_text)],
        [Paragraph("<b>Project Repository</b>", table_text), Paragraph("CSA0914-192511469-Assignment3", table_text)],
        [Paragraph("<b>GitHub Repository URL</b>", table_text), Paragraph("https://github.com/geetikaT67/CSA0914-192511469-Assignment3", table_text)],
        [Paragraph("<b>Development Platform</b>", table_text), Paragraph("Java SE 21.0.4 LTS (Oracle OpenJDK) on Windows x64", table_text)],
        [Paragraph("<b>Submission Date</b>", table_text), Paragraph("November 2026", table_text)]
    ]
    t_meta = Table(meta_data, colWidths=[160, 355])
    t_meta.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#EEEEEE")),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CCCCCC")),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
        ('LEFTPADDING', (0,0), (-1,-1), 8),
        ('RIGHTPADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(t_meta)
    story.append(Spacer(1, 35))

    verif_summary = [
        [Paragraph("<b>Verification Summary:</b> All 7 task modules and 3 'Complete the Code' exercises have been compiled under Java 21, executed under high-contention/failure conditions, verified against expected outputs, benchmarked with 7 empirical CSV datasets, and committed to GitHub.", table_text)]
    ]
    t_verif = Table(verif_summary, colWidths=[515])
    t_verif.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#F9F9F9")),
        ('BOX', (0,0), (-1,-1), 0.5, colors.HexColor("#BBBBBB")),
        ('PADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(t_verif)
    story.append(PageBreak())

    # =========================================================================
    # 2. TABLE OF CONTENTS
    # =========================================================================
    story.append(Paragraph("TABLE OF CONTENTS", h1_style))
    story.append(Spacer(1, 6))

    toc_entries = [
        ("sec_intro", "1. Introduction & Assignment Objectives"),
        ("sec_concepts", "2. Important Theoretical & Engineering Concepts"),
        ("sec_task1", "3. Task 01: Bank ATM Simulation (Failure Handling, Error Codes)"),
        ("sec_task2", "4. Task 02: Online Examination Portal (Exception Hierarchy, Chaining)"),
        ("sec_task3", "5. Task 03: Resource Safety & Debugging (File Leak Diagnosis)"),
        ("sec_task4", "6. Task 04: Thread Creation & Life Cycle (Scanner Stations)"),
        ("sec_task5", "7. Task 05: Race Conditions in Cinema Seat-Booking System"),
        ("sec_task6", "8. Task 06: Deadlock & Inter-Thread Communication (Producer-Consumer)"),
        ("sec_task7", "9. Task 07: High-Throughput Fault-Tolerant Processor (10,000 Files)"),
        ("sec_complete", "10. Complete the Code Programs (Q2 StorageDemo, Q5 SeatBooking, Q6 TokenCounter)"),
        ("sec_diagrams", "11. Important Architectural & Structural Diagrams"),
        ("sec_tables", "12. Important Comparative Analysis Tables"),
        ("sec_conclusion", "13. Final Conclusion & Academic Summary")
    ]

    toc_rows = [
        [Paragraph("<b>Section Title</b>", table_header), Paragraph("<b>Page</b>", table_header)]
    ]
    for key, title in toc_entries:
        p_num = str(toc_page_numbers.get(key, 1)) if is_second_pass else "--"
        dots = ". " * 45
        display_title = f"{title} <font color='#888888'>{dots[:max(10, 85 - len(title)*2)]}</font>"
        toc_rows.append([
            Paragraph(display_title, table_text),
            Paragraph(f"<b>{p_num}</b>", ParagraphStyle('TOCPNum', parent=table_text, alignment=2))
        ])

    t_toc = Table(toc_rows, colWidths=[465, 50])
    t_toc.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#EEEEEE")),
        ('LINEBELOW', (0,0), (-1,0), 1, colors.HexColor("#333333")),
        ('LINEBELOW', (0,1), (-1,-1), 0.3, colors.HexColor("#E5E5E5")),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('LEFTPADDING', (0,0), (-1,-1), 4),
        ('RIGHTPADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t_toc)
    story.append(Spacer(1, 15))

    # =========================================================================
    # 3. SECTION 1: INTRODUCTION & ASSIGNMENT OBJECTIVES
    # =========================================================================
    story.append(TOCRegister("sec_intro"))
    story.append(Paragraph("1. Introduction & Assignment Objectives", h1_style))
    story.append(Paragraph(
        "Robust software design in modern enterprise Java applications requires two fundamental pillars: <b>resilient exception handling</b> and <b>efficient concurrent multithreading</b>. In distributed systems, failure is an inevitability rather than an exception. Hardware devices disconnect, disk quotas fill up, input streams encounter corrupted bytes, and network sockets time out. Well-architected applications isolate these failure boundaries, prevent unhandled process crashes, ensure deterministic resource reclamation, and present meaningful, sanitized status responses to clients.",
        body_style
    ))
    story.append(Paragraph(
        "Simultaneously, multicore processors require concurrent execution to deliver high throughput. However, concurrent threads sharing mutable memory introduce complex synchronization challenges: check-then-act race conditions, mutual resource deadlocks, thread scheduler non-determinism, and producer-consumer buffer corruption.",
        body_style
    ))
    story.append(Paragraph("<b>Primary Assignment Objectives (CO3 Coverage):</b>", h3_style))
    story.append(Paragraph("&bull; <b>Structured Exception Handling:</b> Master Java's Throwable hierarchy, checked vs. unchecked semantics, specific catch block ordering, multi-catch syntax, and the unconditional execution guarantees of finally.", bullet_style))
    story.append(Paragraph("&bull; <b>Layered Propagation & Chaining:</b> Build domain-specific custom exception hierarchies across Controller, Service, and Storage tiers; preserve low-level root causes using getCause() exception chaining without leaking internal filesystem details to clients.", bullet_style))
    story.append(Paragraph("&bull; <b>Deterministic Resource Safety:</b> Diagnose operating system file descriptor leaks caused by manual close() statements; implement try-with-resources and inspect suppressed exceptions.", bullet_style))
    story.append(Paragraph("&bull; <b>Thread Mechanics & Lifecycle:</b> Demonstrate Thread vs. Runnable, prove that run() does not spawn an OS thread while start() does, analyze thread scheduler non-determinism over repeated runs, and observe daemon thread termination.", bullet_style))
    story.append(Paragraph("&bull; <b>Concurrency Control:</b> Reproduce check-then-act race conditions in cinema ticketing; eliminate double-booking using synchronized methods, synchronized blocks, and lock-free AtomicBoolean CAS primitives.", bullet_style))
    story.append(Paragraph("&bull; <b>Deadlock Prevention & Bounded Buffers:</b> Demonstrate reciprocal wallet deadlock via Coffman conditions; eliminate deadlock via global lock-ordering; implement producer-consumer condition synchronization via wait(), notifyAll(), and while-loops.", bullet_style))
    story.append(Paragraph("&bull; <b>High-Throughput Concurrent Systems:</b> Engineer a fault-tolerant batch processor for 10,000 files using ExecutorService, Callable, Future, and lock-free concurrent queues, establishing Amdahl's Law scalability boundaries.", bullet_style))
    story.append(Spacer(1, 10))

    # =========================================================================
    # 4. SECTION 2: IMPORTANT THEORETICAL & ENGINEERING CONCEPTS
    # =========================================================================
    story.append(TOCRegister("sec_concepts"))
    story.append(Paragraph("2. Important Theoretical & Engineering Concepts", h1_style))

    story.append(Paragraph("2.1 The Java Throwable Hierarchy & Classification", h2_style))
    story.append(Paragraph(
        "All error-reporting types in Java descend from <code>java.lang.Throwable</code>. The hierarchy branches into two distinct paths: <b>Error</b> and <b>Exception</b>. <code>Error</code> (e.g., <code>OutOfMemoryError</code>, <code>StackOverflowError</code>) represents severe environmental or hardware faults from which normal application logic cannot recover; applications should not catch them. <code>Exception</code> represents conditions that a reasonable application might want to catch. Subclasses of <code>RuntimeException</code> are designated as <i>unchecked exceptions</i>, representing programming bugs (e.g. <code>NullPointerException</code>, <code>IndexOutOfBoundsException</code>). All other subclasses of <code>Exception</code> are <i>checked exceptions</i> (e.g. <code>IOException</code>, <code>FileNotFoundException</code>), representing anticipated operational failures enforced at compile time.",
        body_style
    ))

    story.append(Paragraph("2.2 Checked vs. Unchecked Exceptions", h2_style))
    story.append(Paragraph(
        "<b>Checked Exceptions:</b> The Java compiler enforces the 'Catch or Specify' requirement. Any method executing code that may throw a checked exception must either enclose it in a <code>try-catch</code> block or declare it in its signature via <code>throws</code>. Checked exceptions are reserved for recoverable business and environmental failures (e.g., card reader missing, late exam submission).<br/>"
        "<b>Unchecked Exceptions:</b> Subclasses of <code>RuntimeException</code>. The compiler does not mandate explicit handling. They represent logical programmer errors or constraint violations (e.g., entering letters for a numeric balance, negative amounts).",
        body_style
    ))

    story.append(Paragraph("2.3 Exception Control Flow: try, catch, finally, throw, and throws", h2_style))
    story.append(Paragraph(
        "&bull; <b>try:</b> Encloses code that may trigger one or more exceptions, establishing an error monitoring scope.<br/>"
        "&bull; <b>catch:</b> Catches and handles specific exception types. Evaluated sequentially from top to bottom; narrower subclasses must appear before broader superclasses.<br/>"
        "&bull; <b>finally:</b> Guarantees unconditional execution regardless of whether an exception occurred, was caught, or whether the try block executed an early return. Used for resource cleanup, session teardown, and audit logging.<br/>"
        "&bull; <b>throw:</b> Explicitly triggers an exception instance (e.g., <code>throw new IllegalArgumentException(...)</code>).<br/>"
        "&bull; <b>throws:</b> Clause in a method signature indicating that the method propagates checked exceptions to its caller.",
        body_style
    ))

    story.append(Paragraph("2.4 Custom Exceptions & Exception Chaining", h2_style))
    story.append(Paragraph(
        "Custom exceptions provide meaningful, domain-specific abstraction (e.g., <code>InsufficientFundsException</code>, <code>ExamWindowClosedException</code>). In multi-tier systems, lower-tier exceptions (such as raw disk <code>IOException</code>) should not be exposed to higher-tier callers. Instead, the storage layer wraps the low-level exception into a domain exception while preserving the original exception via <b>exception chaining</b>: <code>throw new SubmissionStorageException('Message', ioException);</code>. The caller can subsequently retrieve the root cause via <code>e.getCause()</code>.",
        body_style
    ))

    story.append(Paragraph("2.5 Resource Management & Suppressed Exceptions", h2_style))
    story.append(Paragraph(
        "Java 7 introduced <b>try-with-resources</b>, applicable to any resource implementing <code>java.lang.AutoCloseable</code>. Resources declared within <code>try (...)</code> are automatically and deterministically closed upon exiting the block, in <b>reverse order of declaration</b> (LIFO). If an exception occurs inside the try block and a subsequent exception occurs during automatic closing, the primary exception is propagated while the closing exception is attached as a <b>suppressed exception</b> (retrievable via <code>e.getSuppressed()</code>).",
        body_style
    ))

    story.append(Paragraph("2.6 Thread Life Cycle States", h2_style))
    story.append(Paragraph(
        "Java threads (<code>java.lang.Thread</code>) transition across six distinct states defined by <code>Thread.State</code>:<br/>"
        "1. <b>NEW:</b> Thread instance instantiated (<code>new Thread()</code>) but not yet started.<br/>"
        "2. <b>RUNNABLE:</b> Executing in the JVM or ready in the OS scheduler queue awaiting CPU time.<br/>"
        "3. <b>BLOCKED:</b> Suspended waiting to acquire an intrinsic monitor lock held by another thread.<br/>"
        "4. <b>WAITING:</b> Suspended indefinitely until another thread executes a notification (<code>wait()</code>, <code>join()</code>).<br/>"
        "5. <b>TIMED_WAITING:</b> Suspended for a specified duration (<code>sleep(ms)</code>, <code>wait(ms)</code>, <code>join(ms)</code>).<br/>"
        "6. <b>TERMINATED:</b> Thread has completed execution of its <code>run()</code> method.",
        body_style
    ))

    story.append(Paragraph("2.7 Thread versus Runnable", h2_style))
    story.append(Paragraph(
        "Extending <code>Thread</code> tightly couples task definition with execution mechanics and consumes Java's single inheritance slot. Implementing <code>Runnable</code> defines a pure functional task decoupled from thread creation, adhering to composition over inheritance, and enables seamless execution within modern thread pools (<code>ExecutorService</code>).",
        body_style
    ))

    story.append(Paragraph("2.8 Race Conditions, Critical Sections, and Synchronization", h2_style))
    story.append(Paragraph(
        "A <b>race condition</b> occurs when the correctness of a program depends on the relative timing or interleaving of concurrent threads. A <b>critical section</b> is a segment of code accessing shared mutable state where concurrent access causes state corruption. Java's <code>synchronized</code> keyword utilizes an object's intrinsic monitor lock to enforce <b>mutual exclusion</b>, guaranteeing that only one thread executes inside the critical section at any instant.",
        body_style
    ))

    story.append(Paragraph("2.9 Deadlock & The Four Coffman Conditions", h2_style))
    story.append(Paragraph(
        "Deadlock occurs when two or more threads are permanently blocked, each waiting for a lock held by the other. Deadlock requires four simultaneous conditions: <b>(1) Mutual Exclusion</b>, <b>(2) Hold and Wait</b>, <b>(3) No Preemption</b>, and <b>(4) Circular Wait</b>. Enforcing a <b>strict global lock-ordering hierarchy</b> (e.g., acquiring locks based on unique numerical account IDs) mathematically breaks Circular Wait, eliminating deadlock completely.",
        body_style
    ))

    story.append(Paragraph("2.10 Inter-Thread Communication: wait(), notify(), and notifyAll()", h2_style))
    story.append(Paragraph(
        "Defined on <code>java.lang.Object</code>, these methods coordinate thread execution via monitor locks. <code>wait()</code> atomically releases the lock and suspends the thread. <code>notifyAll()</code> wakes all waiting threads. Critical rule: <code>wait()</code> must <b>always be called inside a while-loop</b> to protect against spurious wakeups and condition invalidations, and <code>notifyAll()</code> must be favored over <code>notify()</code> to prevent signal starvation deadlocks.",
        body_style
    ))

    story.append(Paragraph("2.11 The Executor Framework & Thread Pools", h2_style))
    story.append(Paragraph(
        "Spawning raw threads for high-volume tasks causes memory exhaustion (each thread allocates ~1 MB native stack). <code>ExecutorService</code> maintains a bounded pool of reusable worker threads. Work is submitted as <code>Callable<T></code> tasks that return asynchronous <code>Future<T></code> handles, providing bounded resource allocation, parallel speedup, and clean exception isolation.",
        body_style
    ))
    story.append(PageBreak())

    # =========================================================================
    # 5. SECTION 3: IMPORTANT PROGRAMS, SCREENSHOTS & VIVA DEFENSE
    # =========================================================================
    story.append(TOCRegister("sec_task1"))
    story.append(Paragraph("3. Task 01: Bank ATM Simulation", h1_style))
    story.append(Paragraph("<b>Problem Statement:</b> Simulate an ATM cash withdrawal system accepting an account number and withdrawal amount. The program must validate card-reader hardware status, numeric formats, positive amounts, and balance limits using structured exception handling.", body_style))

    story.append(Paragraph("<b>Essential Implementation Code (AtmSimulation.java):</b>", h3_style))
    code_t1 = (
        "public void withdraw(String rawAccNo, String rawAmount, String cardReaderPath)\n"
        "        throws FileNotFoundException, InsufficientFundsException {\n"
        "    try {\n"
        "        // 1. Hardware card-reader validation (Checked exception)\n"
        "        verifyCardReader(cardReaderPath);\n"
        "        // 2. Account format validation (Unchecked exception)\n"
        "        if (!rawAccNo.matches(\"\\\\d{10}\"))\n"
        "            throw new NumberFormatException(\"Account must be 10 numeric digits: '\" + rawAccNo + \"'\");\n"
        "        // 3. Amount parsing and positive validation (Unchecked exception)\n"
        "        double amount = Double.parseDouble(rawAmount);\n"
        "        if (amount <= 0)\n"
        "            throw new IllegalArgumentException(\"Amount must be positive: \" + amount);\n"
        "        // 4. Balance check (Custom checked exception)\n"
        "        if (amount > balance)\n"
        "            throw new InsufficientFundsException(\"Overdraft! Bal: \" + balance + \", Req: \" + amount, balance, amount);\n"
        "        balance -= amount;\n"
        "        System.out.println(\">>> SUCCESS: Dispensed Rs \" + amount + \". Remaining: Rs \" + balance);\n"
        "    } catch (NumberFormatException e) {\n"
        "        System.out.println(\"[CATCH - NumberFormat]: Invalid format: \" + e.getMessage());\n"
        "    } catch (IllegalArgumentException e) {\n"
        "        System.out.println(\"[CATCH - IllegalArgument]: \" + e.getMessage());\n"
        "    } catch (InsufficientFundsException e) {\n"
        "        System.out.println(\"[CATCH - InsufficientFunds]: Overdraft declined: \" + e.getMessage());\n"
        "    } catch (FileNotFoundException e) {\n"
        "        System.out.println(\"[CATCH - FileNotFound]: Hardware error: \" + e.getMessage());\n"
        "    } finally {\n"
        "        // Finally guarantees unconditional session cleanup and card ejection\n"
        "        System.out.println(\"[FINALLY]: Card ejected securely. ATM session closed. Audit log saved.\");\n"
        "    }\n"
        "}"
    )
    story.append(make_code_box(code_t1))
    story.append(Spacer(1, 6))

    story.append(Paragraph("<b>Actual Execution Evidence & Benchmark:</b>", h3_style))
    story.append(make_image_flowable("Screenshots/task01_atm_execution.png", max_width=500, max_height=200))
    story.append(Paragraph("Figure 1.1: Actual execution output of AtmSimulation verifying 5 distinct failure paths and unconditional finally execution.", caption_style))
    story.append(Spacer(1, 4))
    story.append(make_image_flowable("Screenshots/task01_benchmark_chart.png", max_width=420, max_height=145))
    story.append(Paragraph("Figure 1.2: Empirical latency benchmark (200k operations) comparing Error Codes (65.1 ns) vs Exception Handling (666.7 ns).", caption_style))

    story.append(Paragraph("<b>Expected vs. Observed Results:</b> All 5 test inputs (Valid, Non-numeric, Negative, Overdraft, Missing Config) executed their designated catch blocks cleanly. In every case, the <code>finally</code> block executed unconditionally, ensuring card ejection and audit logging.", body_style))

    story.append(Paragraph("<b>Important Viva Points:</b>", h3_style))
    story.append(Paragraph("Q: Why does placing <code>catch (Exception e)</code> before <code>catch (InsufficientFundsException e)</code> cause a compile error?", viva_q_style))
    story.append(Paragraph("A: Java evaluates catch blocks top-to-bottom. Because <code>InsufficientFundsException</code> is a subclass of <code>Exception</code>, the superclass catch block would intercept all instances, rendering the subclass catch block completely unreachable dead code. The compiler rejects this with 'error: exception InsufficientFundsException has already been caught'.", viva_a_style))
    story.append(Paragraph("Q: What is the computational cost of throwing an exception vs returning an error code?", viva_q_style))
    story.append(Paragraph("A: Creating a Throwable triggers the native JVM method <code>fillInStackTrace()</code>, which walks the current thread's entire stack frame. In our benchmark, this incurred a 10.24x latency penalty (666.7 ns vs 65.1 ns for primitive integer returns). Exceptions must be reserved for exceptional errors, not routine flow control.", viva_a_style))
    story.append(PageBreak())

    # -------------------------------------------------------------------------
    # TASK 02: ONLINE EXAMINATION PORTAL
    # -------------------------------------------------------------------------
    story.append(TOCRegister("sec_task2"))
    story.append(Paragraph("4. Task 02: Online Examination Portal", h1_style))
    story.append(Paragraph("<b>Problem Statement:</b> Implement a multi-layered examination portal (Controller, Service, Storage) that rejects submissions when the exam window is closed, student is unregistered, or file size exceeds limits. Demonstrate exception chaining across architectural boundaries without losing root causes.", body_style))

    story.append(Paragraph("<b>Essential Implementation Code (Layered Propagation & Chaining):</b>", h3_style))
    code_t2 = (
        "// STORAGE LAYER: Chains low-level IOException into domain SubmissionStorageException\n"
        "public class StorageLayer {\n"
        "    public void saveAnswerScript(String studentId, String fileName, long sizeKb)\n"
        "            throws SubmissionStorageException {\n"
        "        if (sizeKb > 500) throw new FileSizeLimitExceededException(\"File exceeds 500KB\", sizeKb, 500);\n"
        "        try {\n"
        "            if (sizeKb > 400) throw new IOException(\"POSIX Error: Disk quota exceeded on /var/exam_storage\");\n"
        "            System.out.println(\"  [Storage] Successfully saved \" + fileName + \" for student \" + studentId);\n"
        "        } catch (IOException ioEx) {\n"
        "            // Exception Chaining: Preserves raw IOException as underlying cause\n"
        "            throw new SubmissionStorageException(\"Storage failure persisting '\" + fileName + \"'\", ioEx);\n"
        "        }\n"
        "    }\n"
        "}\n\n"
        "// CONTROLLER LAYER: Sanitizes technical details and generates student-facing UI response\n"
        "try {\n"
        "    examService.processSubmission(studentId, courseCode, fileName, sizeKb, timestamp);\n"
        "    return new SubmissionResponse(200, \"SUBMISSION ACCEPTED\", \"Exam script recorded successfully.\");\n"
        "} catch (SubmissionStorageException e) {\n"
        "    System.err.println(\"Log Internal Cause: \" + e.getCause()); // Diagnostic cause logged on server\n"
        "    return new SubmissionResponse(500, \"STORAGE ERROR\", \"Internal storage failure. Please retry shortly.\");\n"
        "}"
    )
    story.append(make_code_box(code_t2))
    story.append(Spacer(1, 6))

    story.append(Paragraph("<b>Actual Execution Evidence:</b>", h3_style))
    story.append(make_image_flowable("Screenshots/task02_portal_execution.png", max_width=500, max_height=210))
    story.append(Paragraph("Figure 2.1: Execution transcript of ExamPortalDemo displaying full chained stack trace and sanitized student-facing HTTP responses.", caption_style))

    story.append(Paragraph("<b>Expected vs. Observed Results:</b> The storage layer wrapped raw disk IOExceptions into <code>SubmissionStorageException</code>. Calling <code>e.getCause()</code> confirmed that the original POSIX error message and file line numbers were preserved, while the student received a secure, user-friendly HTTP 500 message.", body_style))

    story.append(Paragraph("<b>Important Viva Points:</b>", h3_style))
    story.append(Paragraph("Q: Why wrap IOException in SubmissionStorageException rather than letting it propagate to the UI?", viva_q_style))
    story.append(Paragraph("A: Exposing raw IOExceptions to the UI violates architectural abstraction (couples UI to filesystem implementation) and poses severe security risks by leaking internal server directory paths, partition names, and operating system usernames to end users.", viva_a_style))
    story.append(Paragraph("Q: What happens if the root exception is NOT passed to the custom exception constructor?", viva_q_style))
    story.append(Paragraph("A: The cause field defaults to null. Any downstream debugging logic invoking <code>e.getCause().getMessage()</code> triggers an immediate <code>NullPointerException</code>, crashing diagnostic pipelines and permanently destroying root-cause traceability.", viva_a_style))
    story.append(PageBreak())

    # -------------------------------------------------------------------------
    # TASK 03: RESOURCE SAFETY & DEBUGGING
    # -------------------------------------------------------------------------
    story.append(TOCRegister("sec_task3"))
    story.append(Paragraph("5. Task 03: Resource Safety & Debugging", h1_style))
    story.append(Paragraph("<b>Problem Statement:</b> A daily sales report generator aggregates multiple store CSV files into a summary file. The legacy version intermittently halts with 'Too many open files'. Diagnose the resource leak using step-through debugging and compare manual finally closing against try-with-resources under failure injection.", body_style))

    story.append(Paragraph("<b>Essential Implementation Code (Manual vs. Try-With-Resources):</b>", h3_style))
    code_t3 = (
        "// FLAWED MANUAL VERSION: Exception in parsing skips br.close(), leaking file descriptor!\n"
        "TrackedFileReader fr = new TrackedFileReader(storeFile);\n"
        "BufferedReader br = new BufferedReader(fr);\n"
        "try {\n"
        "    while ((line = br.readLine()) != null) {\n"
        "        int qty = Integer.parseInt(parts[1]); // Fails on corrupt input: skips br.close()!\n"
        "    }\n"
        "    br.close();\n"
        "} catch (NumberFormatException nfe) {\n"
        "    throw new IOException(\"Corrupt file: \" + storeFile.getName(), nfe); // LEAKS FILE HANDLE!\n"
        "}\n\n"
        "// ROBUST TRY-WITH-RESOURCES: Guarantees prompt closing in reverse declaration order\n"
        "try (TrackedFileWriter fw = new TrackedFileWriter(out);\n"
        "     BufferedWriter writer = new BufferedWriter(fw)) {\n"
        "    for (File storeFile : storeFiles) {\n"
        "        try (TrackedFileReader fr = new TrackedFileReader(storeFile);\n"
        "             BufferedReader br = new BufferedReader(fr)) {\n"
        "            // Parsing logic...\n"
        "        } // br and fr guaranteed closed here, even if parsing throws NumberFormatException!\n"
        "    }\n"
        "}"
    )
    story.append(make_code_box(code_t3))
    story.append(Spacer(1, 6))

    story.append(Paragraph("<b>Actual Execution Evidence & Handle Comparison:</b>", h3_style))
    story.append(make_image_flowable("Screenshots/task03_resource_safety.png", max_width=490, max_height=145))
    story.append(Paragraph("Figure 3.1: Actual execution transcript of ResourceSafetyBenchmark proving 1 handle leaked in manual version vs 0 in try-with-resources.", caption_style))
    story.append(Spacer(1, 4))
    story.append(make_image_flowable("Screenshots/task03_handles_comparison.png", max_width=420, max_height=145))
    story.append(Paragraph("Figure 3.2: Bar chart comparing leaked open file descriptors upon partial parsing failure: Manual (1 handle) vs Try-with-resources (0 handles).", caption_style))

    story.append(Paragraph("<b>Expected vs. Observed Results:</b> Both versions produced identical output on valid data. When tested against failing data (corrupt file 4), the manual version leaked 1 open OS file descriptor, whereas try-with-resources closed all resources cleanly (0 leaked handles).", body_style))

    story.append(Paragraph("<b>Important Viva Points:</b>", h3_style))
    story.append(Paragraph("Q: What is a suppressed exception and when does it occur?", viva_q_style))
    story.append(Paragraph("A: A suppressed exception occurs when an exception is thrown in the try block and a second exception is thrown while automatically closing an AutoCloseable resource. Try-with-resources preserves the primary exception and attaches the closing exception as a suppressed exception (retrievable via <code>e.getSuppressed()</code>). In manual finally blocks, the closing exception completely masked and erased the primary application error.", viva_a_style))
    story.append(Paragraph("Q: Under what conditions is try-with-resources alone insufficient?", viva_q_style))
    story.append(Paragraph("A: Try-with-resources guarantees file handle closure, but if a write fails mid-way, it leaves a truncated, corrupt summary file on disk. Robust systems must write to a temporary file (<code>summary.tmp</code>) and execute an atomic filesystem move (<code>Files.move</code> with <code>ATOMIC_MOVE</code>) only upon complete success.", viva_a_style))
    story.append(PageBreak())

    # -------------------------------------------------------------------------
    # TASK 04: THREAD CREATION & LIFECYCLE
    # -------------------------------------------------------------------------
    story.append(TOCRegister("sec_task4"))
    story.append(Paragraph("6. Task 04: Thread Creation and the Thread Life Cycle", h1_style))
    story.append(Paragraph("<b>Problem Statement:</b> Simulate four examination answer-script scanner stations processing batches simultaneously. Compare extending Thread vs. implementing Runnable, demonstrate thread life cycle states, analyze start() vs. run(), experiment with priorities and daemon threads, and analyze OS scheduler non-determinism across 10 repeated runs.", body_style))

    story.append(Paragraph("<b>Essential Implementation Code (ScannerStationThread.java):</b>", h3_style))
    code_t4 = (
        "public class ScannerStationThread extends Thread {\n"
        "    private final int stationId, scriptsToScan;\n"
        "    private int scriptsScanned = 0;\n"
        "    public ScannerStationThread(int id, int count) { super(\"Station-\" + id); this.stationId = id; this.scriptsToScan = count; }\n"
        "    @Override\n"
        "    public void run() {\n"
        "        for (int i = 1; i <= scriptsToScan; i++) {\n"
        "            try { Thread.sleep(10); scriptsScanned++; } catch (InterruptedException e) { return; }\n"
        "        }\n"
        "    }\n"
        "}\n"
        "// Main Orchestration & join() Synchronization:\n"
        "for (ScannerStationThread s : stations) s.start(); // Spawns OS threads\n"
        "for (ScannerStationThread s : stations) s.join();  // Guarantees aggregation only after completion!\n"
        "int grandTotal = 0;\n"
        "for (ScannerStationThread s : stations) grandTotal += s.getScriptsScanned();\n"
        "System.out.println(\"Total Scanned: \" + grandTotal); // Deterministic 200/200"
    )
    story.append(make_code_box(code_t4))
    story.append(Spacer(1, 6))

    story.append(Paragraph("<b>Actual Execution Evidence:</b>", h3_style))
    story.append(make_image_flowable("Screenshots/task04_thread_lifecycle.png", max_width=490, max_height=150))
    story.append(Paragraph("Figure 4.1: Terminal output of ScannerStationThread demonstrating NEW, RUNNABLE, TIMED_WAITING, and TERMINATED state transitions.", caption_style))
    story.append(Spacer(1, 4))
    story.append(make_image_flowable("Screenshots/task04_start_vs_run_evidence.png", max_width=490, max_height=110))
    story.append(Paragraph("Figure 4.2: Direct proof showing directThread.run() executed on 'main' thread (ID: 1), while asyncThread.start() allocated a new OS thread (ID: 22).", caption_style))

    story.append(Paragraph("<b>Expected vs. Observed Results:</b> Across 10 consecutive runs, the station finish orders varied non-deterministically (e.g. 3-2-1-4, 3-4-1-2, 1-3-2-4, 4-3-2-1), confirming that thread scheduling is governed by preemptive OS time-slicing. Calling <code>join()</code> ensured 100% correct script aggregation (200/200) regardless of scheduling order.", body_style))

    story.append(Paragraph("<b>Important Viva Points:</b>", h3_style))
    story.append(Paragraph("Q: Why does calling run() instead of start() not create a new thread?", viva_q_style))
    story.append(Paragraph("A: In Java, <code>run()</code> is simply an ordinary method declared on the Runnable interface. Invoking it directly executes its bytecode synchronously on the calling thread's existing stack frame (main thread ID: 1). Only <code>start()</code> issues a native system call to the host OS kernel to allocate a new thread stack, register with the CPU scheduler, and invoke run() asynchronously (thread ID: 22).", viva_a_style))
    story.append(Paragraph("Q: What happens to a daemon thread when the main thread finishes?", viva_q_style))
    story.append(Paragraph("A: The JVM terminates execution immediately as soon as all non-daemon user threads terminate. Active daemon threads are abruptly aborted mid-instruction without executing finally blocks or flushing buffered I/O streams.", viva_a_style))
    story.append(PageBreak())

    # -------------------------------------------------------------------------
    # TASK 05: RACE CONDITIONS & SYNCHRONIZATION
    # -------------------------------------------------------------------------
    story.append(TOCRegister("sec_task5"))
    story.append(Paragraph("7. Task 05: Race Conditions in Cinema Seat-Booking System", h1_style))
    story.append(Paragraph("<b>Problem Statement:</b> Multiple concurrent users book cinema seats simultaneously. A non-atomic check-then-act sequence causes the same seat to be sold multiple times. Reproduce double bookings reliably, explain the interleaving, and compare synchronized methods, synchronized blocks, and atomic CAS primitives over 10,000 operations.", body_style))

    story.append(Paragraph("<b>Essential Implementation Code (Atomic vs Non-Atomic):</b>", h3_style))
    code_t5 = (
        "// 1. UNSYNCHRONIZED: Non-atomic check-then-act permits severe double-booking\n"
        "public boolean bookUnsynced(int seat) {\n"
        "    if (!booked[seat]) { // Thread preemption here allows second thread to read false!\n"
        "        try { Thread.sleep(1); } catch (InterruptedException ignored) {}\n"
        "        booked[seat] = true; bookingsMade++; return true;\n"
        "    }\n"
        "    return false;\n"
        "}\n\n"
        "// 2. SYNCHRONIZED BLOCK (PER-SEAT): Fine-grained locking maximizes throughput\n"
        "public boolean bookFineGrained(int seat) {\n"
        "    synchronized (seatLocks[seat]) { // Threads booking distinct seats run in parallel!\n"
        "        if (!booked[seat]) {\n"
        "            booked[seat] = true;\n"
        "            synchronized (countLock) { bookingsMade++; }\n"
        "            return true;\n"
        "        }\n"
        "    }\n"
        "    return false;\n"
        "}\n\n"
        "// 3. LOCK-FREE CAS: AtomicBoolean compareAndSet hardware primitive (Fastest)\n"
        "public boolean bookCAS(int seat) {\n"
        "    if (seats[seat].compareAndSet(false, true)) { bookingsCount.incrementAndGet(); return true; }\n"
        "    return false;\n"
        "}"
    )
    story.append(make_code_box(code_t5))
    story.append(Spacer(1, 6))

    story.append(Paragraph("<b>Actual Execution Evidence & Throughput Comparison:</b>", h3_style))
    story.append(make_image_flowable("Screenshots/task05_race_condition_chart.png", max_width=460, max_height=140))
    story.append(Paragraph("Figure 5.1: Bar chart recording double bookings across 10 repeated unsynchronized runs (up to 10 duplicate seats sold per run).", caption_style))
    story.append(Spacer(1, 4))
    story.append(make_image_flowable("Screenshots/task05_throughput_benchmark.png", max_width=470, max_height=145))
    story.append(Paragraph("Figure 5.2: Throughput benchmark across 10,000 bookings: Lock-free CAS (1,516 ns) and Sync Block (1,706 ns) vs Coarse Sync Method (5,155 ns).", caption_style))

    story.append(Paragraph("<b>Expected vs. Observed Results:</b> In unsynchronized execution, up to 20 seats were sold on a 10-seat screen. Synchronized versions eliminated double-booking completely (strictly 10 seats sold). Lock-free `AtomicBoolean` CAS achieved the lowest latency (1,516.7 ns/op), while synchronized blocks outperformed whole-method locking by 3.0x under contention.", body_style))

    story.append(Paragraph("<b>Important Viva Points:</b>", h3_style))
    story.append(Paragraph("Q: Which synchronization strategy should be deployed for a cinema with 20 screens?", viva_q_style))
    story.append(Paragraph("A: Use per-seat locks or per-screen `AtomicBoolean` arrays. Never use static synchronization (`public static synchronized void book()`), because a static lock synchronizes on `SeatBooking.class`, creating a global bottleneck where booking a ticket on Screen 1 blocks all users across Screen 2 through Screen 20.", viva_a_style))
    story.append(Paragraph("Q: What is the exact critical section in the booking operation?", viva_q_style))
    story.append(Paragraph("A: The critical section begins at the state evaluation <code>if (!booked[seat])</code>, encompasses the state mutation <code>booked[seat] = true</code>, and ends after the counter increment <code>bookingsMade++</code>. All three operations must execute atomically.", viva_a_style))
    story.append(PageBreak())

    # -------------------------------------------------------------------------
    # TASK 06: DEADLOCK & PRODUCER-CONSUMER
    # -------------------------------------------------------------------------
    story.append(TOCRegister("sec_task6"))
    story.append(Paragraph("8. Task 06: Deadlock and Inter-Thread Communication", h1_style))
    story.append(Paragraph("<b>Problem Statement:</b> Part A: Demonstrate deadlock during reciprocal hostel wallet transfers (A->B vs B->A) and eliminate it using consistent lock ordering. Part B: Implement a bounded mess token counter (capacity 5) using wait() and notifyAll(), proving the buffer invariant $0 \\le \\text{count} \\le 5$.", body_style))

    story.append(Paragraph("<b>Essential Implementation Code (Lock-Ordering & Token Counter):</b>", h3_style))
    code_t6 = (
        "// PART A: DEADLOCK ELIMINATION VIA STRICT GLOBAL LOCK ORDERING\n"
        "public static void safeTransfer(Wallet from, Wallet to, double amount) {\n"
        "    // Enforce lock acquisition based on numerical account ID: eliminates Circular Wait!\n"
        "    Wallet first = (from.getAccountId() < to.getAccountId()) ? from : to;\n"
        "    Wallet second = (from.getAccountId() < to.getAccountId()) ? to : from;\n"
        "    synchronized (first) {\n"
        "        synchronized (second) {\n"
        "            if (from.getBalance() >= amount) { from.withdraw(amount); to.deposit(amount); }\n"
        "        }\n"
        "    }\n"
        "}\n\n"
        "// PART B: BOUNDED BUFFER PRODUCER-CONSUMER (CAPACITY = 5)\n"
        "public synchronized void putToken(int token) throws InterruptedException {\n"
        "    while (count == buffer.length) wait(); // Must use while loop to protect against spurious wakeups!\n"
        "    buffer[in] = token; in = (in + 1) % buffer.length; count++;\n"
        "    notifyAll(); // Wakes all waiting consumers\n"
        "}\n"
        "public synchronized int takeToken() throws InterruptedException {\n"
        "    while (count == 0) wait();\n"
        "    int token = buffer[out]; out = (out + 1) % buffer.length; count--;\n"
        "    notifyAll(); return token;\n"
        "}"
    )
    story.append(make_code_box(code_t6))
    story.append(Spacer(1, 6))

    story.append(Paragraph("<b>Actual Execution Evidence:</b>", h3_style))
    story.append(make_image_flowable("Screenshots/task06_deadlock_and_buffer.png", max_width=490, max_height=100))
    story.append(Paragraph("Figure 6.1: Watchdog thread confirming both wallet transfer threads permanently BLOCKED in deadlock when locks are acquired out of order.", caption_style))
    story.append(Spacer(1, 4))
    story.append(make_image_flowable("Screenshots/task06_buffer_occupancy_chart.png", max_width=470, max_height=145))
    story.append(Paragraph("Figure 6.2: Buffer size over 100 transaction steps proving that buffer count strictly adheres to the invariant 0 <= count <= 5.", caption_style))

    story.append(Paragraph("<b>Expected vs. Observed Results:</b> Out-of-order transfers deadlocked reliably within 50 ms. With numerical lock-ordering, 1,000 reciprocal transfers completed in 2 ms with zero deadlock. In the mess counter, 100 producer-consumer operations maintained buffer bounds between 0 and 5 with zero invariant violations.", body_style))

    story.append(Paragraph("<b>Important Viva Points:</b>", h3_style))
    story.append(Paragraph("Q: Why must wait() be called inside a while loop rather than an if statement?", viva_q_style))
    story.append(Paragraph("A: Two reasons: (1) Spurious Wakeups: The operating system scheduler can wake a waiting thread without any explicit notify() signal. (2) Condition Invalidation: In multi-consumer systems, notifyAll() wakes multiple threads. The first thread to acquire the lock consumes the item. If subsequent threads used 'if', they would proceed without re-checking, attempting to read from an empty buffer (causing underflow errors).", viva_a_style))
    story.append(Paragraph("Q: What goes wrong if notify() is used instead of notifyAll()?", viva_q_style))
    story.append(Paragraph("A: Signal Starvation Deadlock. If two consumers are waiting on an empty buffer and a producer adds one item and calls notify(), Consumer 1 wakes and consumes the item. If Consumer 1 calls notify(), the JVM might wake Consumer 2 instead of the Producer. Consumer 2 finds the buffer empty and goes back to sleep. All threads are now waiting, halting the application.", viva_a_style))
    story.append(PageBreak())

    # -------------------------------------------------------------------------
    # TASK 07: 10,000 FILE CONCURRENT PROCESSOR
    # -------------------------------------------------------------------------
    story.append(TOCRegister("sec_task7"))
    story.append(Paragraph("9. Task 07: High-Throughput Fault-Tolerant Processor (10,000 Files)", h1_style))
    story.append(Paragraph("<b>Problem Statement:</b> Process 10,000 semester score files quickly and reliably. Individual files may be corrupt or missing. A single bad file must not crash the run, and an exact summary of successes, failures, and ground-truth validation must be produced.", body_style))

    story.append(Paragraph("<b>Essential Implementation Code (ScoreFileProcessorTask & ResultProcessor):</b>", h3_style))
    code_t7 = (
        "// 1. FAULT-TOLERANT CALLABLE: Never throws uncaught exceptions outward\n"
        "public class ScoreFileProcessorTask implements Callable<ProcessingResult> {\n"
        "    public ProcessingResult call() {\n"
        "        File file = new File(filePath);\n"
        "        if (!file.exists()) return ProcessingResult.missing(file.getName(), studentId, \"Missing file\");\n"
        "        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {\n"
        "            // Parse lines, validate bounds (0 <= marks <= 100), compute average\n"
        "            return ProcessingResult.success(file.getName(), studentId, total, avg, avg >= 50.0);\n"
        "        } catch (NumberFormatException nfe) {\n"
        "            return ProcessingResult.corrupt(file.getName(), studentId, \"Malformed score data\");\n"
        "        } catch (Exception e) {\n"
        "            return ProcessingResult.error(file.getName(), studentId, e.getMessage());\n"
        "        }\n"
        "    }\n"
        "}\n"
        "// 2. ORCHESTRATION WITH EXECUTORSERVICE & FUTURES:\n"
        "ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);\n"
        "List<Future<ProcessingResult>> futures = new ArrayList<>(10000);\n"
        "for (String path : filePaths) futures.add(executor.submit(new ScoreFileProcessorTask(path, id)));\n"
        "for (Future<ProcessingResult> f : futures) summary.record(f.get()); // Aggregates results cleanly\n"
        "executor.shutdown(); executor.awaitTermination(30, TimeUnit.SECONDS);"
    )
    story.append(make_code_box(code_t7))
    story.append(Spacer(1, 6))

    story.append(Paragraph("<b>Actual Execution Evidence & Scalability Benchmark:</b>", h3_style))
    story.append(make_image_flowable("Screenshots/task07_10k_benchmark.png", max_width=490, max_height=145))
    story.append(Paragraph("Figure 7.1: Terminal execution output of 10,000 files processed across thread pools 1, 2, 4, 8 with 100% hand-checked ground-truth verification.", caption_style))
    story.append(Spacer(1, 4))
    story.append(make_image_flowable("Screenshots/task07_scalability_chart.png", max_width=460, max_height=145))
    story.append(Paragraph("Figure 7.2: Scalability curve showing execution time drop from 22.7s (1 thread) to 6.7s (8 threads), achieving 1,492 files/sec throughput.", caption_style))

    story.append(Paragraph("<b>Expected vs. Observed Results:</b> Processed 10,000 files (9,600 valid, 250 corrupt, 150 missing) with exactly <b>0 unhandled failures</b>. Execution time dropped from 22,699 ms (1 thread) to 6,701 ms (8 threads), reaching 1,492.3 files/sec. Hand-checked set of 20 files verified 100.0% ground-truth accuracy.", body_style))

    story.append(Paragraph("<b>Important Viva Points:</b>", h3_style))
    story.append(Paragraph("Q: Why does throughput plateau beyond 4 to 8 threads?", viva_q_style))
    story.append(Paragraph("A: Amdahl's Law and I/O Bottlenecks. File processing is heavily bound by physical storage read speeds and operating system page-cache locks. Adding threads beyond available CPU cores (8 threads) causes disk queue saturation and context-switch thrashing rather than parallel acceleration.", viva_a_style))
    story.append(Paragraph("Q: Why is ExecutorService superior to spawning 10,000 raw Thread objects?", viva_q_style))
    story.append(Paragraph("A: Spawning 10,000 raw threads consumes ~10 GB of native stack memory (1 MB default per thread), immediately crashing the JVM with <code>OutOfMemoryError: unable to create native thread</code>. A fixed pool of 8 threads caps memory at ~8 MB total.", viva_a_style))
    story.append(PageBreak())

    # =========================================================================
    # 6. SECTION 4: COMPLETE THE CODE PROGRAMS
    # =========================================================================
    story.append(TOCRegister("sec_complete"))
    story.append(Paragraph("10. Complete the Code Programs", h1_style))
    story.append(Paragraph("This section presents the three supplied incomplete programs from the specification, their completed code sections, actual execution outputs, and solution analysis.", body_style))

    # Q2 StorageDemo
    story.append(Paragraph("Complete the Code – Q2: StorageDemo (PDF Pages 11–12)", h2_style))
    code_q2_complete = (
        "// Completed TODO 1 in Storage.save():\n"
        "class Storage {\n"
        "    void save(String fileName, long sizeKb) throws SubmissionStorageException {\n"
        "        try {\n"
        "            if (sizeKb > 500) throw new IOException(\"Disk quota exceeded\");\n"
        "            System.out.println(\"Saved \" + fileName);\n"
        "        } catch (IOException e) {\n"
        "            // TODO 1: Wrap the IOException in a SubmissionStorageException, keeping\n"
        "            // the IOException as its cause\n"
        "            throw new SubmissionStorageException(\"Could not store \" + fileName, e);\n"
        "        }\n"
        "    }\n"
        "}"
    )
    story.append(make_code_box(code_q2_complete))
    story.append(Spacer(1, 4))
    story.append(make_image_flowable("Screenshots/task02_storagedemo_q2.png", max_width=470, max_height=80))
    story.append(Paragraph("Figure 10.1: Actual execution output of completed StorageDemo matching expected output character-for-character.", caption_style))
    story.append(Paragraph("<b>Analysis:</b> Wrapping <code>IOException</code> preserves the underlying POSIX error message. If the cause were omitted, <code>e.getCause()</code> in <code>main()</code> would return <code>null</code>, triggering a fatal <code>NullPointerException</code> on <code>e.getCause().getMessage()</code>.", body_style))
    story.append(Spacer(1, 6))

    # Q5 SeatBooking
    story.append(Paragraph("Complete the Code – Q5: SeatBooking (PDF Pages 19–20)", h2_style))
    code_q5_complete = (
        "// Completed TODO 1 in SeatBooking:\n"
        "public class SeatBooking {\n"
        "    private final boolean[] booked;\n"
        "    private int bookingsMade = 0;\n"
        "    SeatBooking(int seats) { booked = new boolean[seats]; }\n\n"
        "    // TODO 1: Make the check-then-book operation atomic so that a seat can never be sold twice\n"
        "    public synchronized void book(int seat) {\n"
        "        if (!booked[seat]) {\n"
        "            booked[seat] = true;\n"
        "            bookingsMade++;\n"
        "        }\n"
        "    }\n"
        "}"
    )
    story.append(make_code_box(code_q5_complete))
    story.append(Spacer(1, 4))
    story.append(make_image_flowable("Screenshots/task05_seatbooking_q5.png", max_width=470, max_height=75))
    story.append(Paragraph("Figure 10.2: Actual execution output of completed SeatBooking verifying exactly 10 seats sold across 100 competing threads.", caption_style))
    story.append(Paragraph("<b>Analysis:</b> The <code>synchronized</code> keyword locks on the <code>SeatBooking</code> instance monitor, guaranteeing that the check <code>!booked[seat]</code> and the update <code>booked[seat]=true</code> execute as an indivisible atomic unit.", body_style))
    story.append(Spacer(1, 6))

    # Q6 TokenCounter
    story.append(Paragraph("Complete the Code – Q6: TokenCounter (PDF Pages 23–24)", h2_style))
    code_q6_complete = (
        "// Completed TODO 1 & TODO 2 in TokenCounter:\n"
        "public synchronized void put(int token) throws InterruptedException {\n"
        "    // TODO 1: Wait while counter is full, add token at position in, wake waiting threads\n"
        "    while (count == buffer.length) wait();\n"
        "    buffer[in] = token; in = (in + 1) % buffer.length; count++;\n"
        "    notifyAll();\n"
        "}\n"
        "public synchronized int take() throws InterruptedException {\n"
        "    // TODO 2: Wait while counter is empty, remove token at position out, wake waiting threads\n"
        "    while (count == 0) wait();\n"
        "    int token = buffer[out]; out = (out + 1) % buffer.length; count--;\n"
        "    notifyAll(); return token;\n"
        "}"
    )
    story.append(make_code_box(code_q6_complete))
    story.append(Spacer(1, 4))
    story.append(make_image_flowable("Screenshots/task06_tokencounter_q6.png", max_width=470, max_height=75))
    story.append(Paragraph("Figure 10.3: Actual execution output of completed TokenCounter verifying exact sum of 210 tokens collected.", caption_style))
    story.append(Paragraph("<b>Analysis:</b> Using <code>while</code> loops ensures that woken threads re-verify buffer availability before mutating indices. Replacing <code>while</code> with <code>if</code> breaks under multi-consumer contention when spurious wakeups occur.", body_style))
    story.append(PageBreak())

    # =========================================================================
    # 7. SECTION 5: IMPORTANT DIAGRAMS
    # =========================================================================
    story.append(TOCRegister("sec_diagrams"))
    story.append(Paragraph("11. Important Architectural & Structural Diagrams", h1_style))
    story.append(Paragraph("This section presents structured architectural and state diagrams illustrating the core concurrency and exception mechanics evaluated across all tasks.", body_style))

    # Diagram 1: Throwable Hierarchy
    story.append(Paragraph("Diagram 1: Java Throwable Class Hierarchy", h3_style))
    diag1_text = (
        "+-----------------------------------------------------------------------------------+\n"
        "|                                java.lang.Throwable                                |\n"
        "+-----------------------------------------+-----------------------------------------+\n"
        "                                          |                                          \n"
        "              +---------------------------+---------------------------+              \n"
        "              |                                                       |              \n"
        "      java.lang.Error                                       java.lang.Exception      \n"
        "   (Fatal System Failures)                                (Checked Application Base) \n"
        "              |                                                       |              \n"
        "     +--------+--------+                           +------------------+--------------+\n"
        "     |                 |                           |                                 |\n"
        "OutOfMemoryError  StackOverflowError       java.io.IOException              RuntimeException  \n"
        "                                           (Checked I/O Base)             (Unchecked Base)   \n"
        "                                                   |                                 |       \n"
        "                                        FileNotFoundException           +------------+-------+\n"
        "                                                   |                    |                    |\n"
        "                                       SubmissionStorageException  NullPointerEx  NumberFormatEx\n"
        "+-----------------------------------------------------------------------------------+"
    )
    story.append(make_code_box(diag1_text))
    story.append(Paragraph("Figure 11.1: Detailed hierarchy showing checked vs unchecked inheritance paths and custom exception placement.", caption_style))
    story.append(Spacer(1, 6))

    # Diagram 2: Thread Lifecycle
    story.append(Paragraph("Diagram 2: Java Thread Life Cycle State Transition Diagram", h3_style))
    diag2_text = (
        "  [ NEW ]  --- start() --->  [       RUNNABLE       ]  --- run() exits --->  [ TERMINATED ]\n"
        "                               (Ready in OS / Running) \n"
        "                                   |   ^         ^  |  \n"
        "             Thread.sleep(ms)      |   |         |  |   Lock contention\n"
        "             join(ms) / wait(ms)   v   |         |  v   \n"
        "                             [ TIMED_WAITING ]   [ BLOCKED ]\n"
        "                                   |   ^            ^  | \n"
        "                    wait() / join()|   |            |  | Object.wait()\n"
        "                                   v   |            |  v \n"
        "                                   [    WAITING     ]"
    )
    story.append(make_code_box(diag2_text))
    story.append(Paragraph("Figure 11.2: State transitions of a Java thread across NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, and TERMINATED.", caption_style))
    story.append(Spacer(1, 6))

    # Diagram 3: 3-Tier Exam Portal Architecture
    story.append(Paragraph("Diagram 3: Three-Tier Exam Portal Exception Propagation & Sanitization", h3_style))
    diag3_text = (
        "[ Student Browser / Client ] <== HTTP 200 (Success) or HTTP 403 / 400 / 500 (Sanitized Status)\n"
        "            |\n"
        "            v\n"
        "[ ExamControllerLayer ]  --- Catches ExamException; logs technical cause; returns safe UI message\n"
        "            |\n"
        "            v (processSubmission)\n"
        "[ ExamServiceLayer ]     --- Enforces registration & deadline rules; propagates ExamException\n"
        "            |\n"
        "            v (saveAnswerScript)\n"
        "[ StorageLayer ]         --- Encloses disk I/O; catches raw IOException; wraps in SubmissionStorageException\n"
        "            |\n"
        "            v (write bytes)\n"
        "[ Host Operating System Filesystem / Disk Partition ]"
    )
    story.append(make_code_box(diag3_text))
    story.append(Paragraph("Figure 11.3: Architectural layering showing error propagation and secure UI decoupling from raw POSIX exceptions.", caption_style))
    story.append(PageBreak())

    # Diagram 4: Interleaving
    story.append(Paragraph("Diagram 4: Thread Interleaving Anatomy in Check-Then-Act Race Conditions", h3_style))
    diag4_text = (
        "Time  Thread 1 (User Alice - Seat 3)          Thread 2 (User Bob - Seat 3)            Shared State\n"
        "----+---------------------------------------+---------------------------------------+-------------\n"
        " t1 | Checks: !booked[3] -> TRUE            | Ready in scheduler queue              | booked[3]=F\n"
        " t2 | [PREEMPTED by OS Scheduler]           | Dispatched by OS Scheduler            | booked[3]=F\n"
        " t3 | Waiting in queue                      | Checks: !booked[3] -> TRUE (STALE!)   | booked[3]=F\n"
        " t4 | Waiting in queue                      | Sets: booked[3] = true; booksMade=1   | booked[3]=T\n"
        " t5 | Resumed by OS Scheduler               | Exits book() with SUCCESS             | booked[3]=T\n"
        " t6 | Sets: booked[3] = true; booksMade=2   | Completed                             | booked[3]=T\n"
        "----+---------------------------------------+---------------------------------------+-------------\n"
        "RESULT: CRITICAL RACE FAULT: Both Alice and Bob issued confirmed tickets for Seat #3!"
    )
    story.append(make_code_box(diag4_text))
    story.append(Paragraph("Figure 11.4: Step-by-step interleaving illustrating how non-atomic reads permit multiple bookings on a single seat.", caption_style))
    story.append(Spacer(1, 6))

    # Diagram 5: Deadlock Elimination
    story.append(Paragraph("Diagram 5: Deadlock Circular Wait vs. Strict Lock-Ordering Elimination", h3_style))
    diag5_text = (
        "A. DEADLOCK SCENARIO (Arbitrary Acquisition Order):\n"
        "   Thread 1 (A -> B):  Acquires Lock(A) =====> Waiting for Lock(B) [BLOCKED!]\n"
        "                                                      ^             |\n"
        "                                                      | Circular    |\n"
        "                                                      | Dependency  v\n"
        "   Thread 2 (B -> A):  Waiting for Lock(A) <===== Acquires Lock(B) [BLOCKED!]\n\n"
        "B. DEADLOCK-FREE SCENARIO (Strict Numerical Lock-Ordering: min(ID) then max(ID)):\n"
        "   Rule: Both threads MUST acquire Lock(Account 101) before acquiring Lock(Account 102).\n"
        "   Thread 1 (101 -> 102): Acquires Lock(101) ---> Acquires Lock(102) ---> Transfer Completed!\n"
        "   Thread 2 (102 -> 101): Blocks on Lock(101) until Thread 1 finishes (NO CIRCULAR WAIT!)"
    )
    story.append(make_code_box(diag5_text))
    story.append(Paragraph("Figure 11.5: Structural comparison showing Coffman circular dependency and its elimination via numerical lock hierarchy.", caption_style))
    story.append(Spacer(1, 6))

    # Diagram 6: Concurrent Processor
    story.append(Paragraph("Diagram 6: High-Throughput Concurrent Processor Architecture (Task 07)", h3_style))
    diag6_text = (
        "[ 10,000 Score Files ] ===> [ Master Dispatcher Thread ]\n"
        "                                    |\n"
        "                 Submits 10,000 Callable Tasks\n"
        "                                    v\n"
        "    +---------------------------------------------------------------+\n"
        "    |      ExecutorService Worker Thread Pool (P = 1, 2, 4, 8)       |\n"
        "    |   [Worker-1]     [Worker-2]     [Worker-3]     [Worker-4]...  |\n"
        "    +-------------------------------+-------------------------------+\n"
        "                                    |\n"
        "                 Returns Future<ProcessingResult> Handles\n"
        "                                    v\n"
        "    +---------------------------------------------------------------+\n"
        "    |     Lock-Free Concurrent Aggregator (ConcurrentLinkedQueue)   |\n"
        "    |     Summary: Total=10k | Valid=9.6k | Corrupt=250 | Miss=150  |\n"
        "    +---------------------------------------------------------------+"
    )
    story.append(make_code_box(diag6_text))
    story.append(Paragraph("Figure 11.6: Master-worker execution pipeline utilizing bounded thread pools, Future aggregation, and atomic counters.", caption_style))
    story.append(PageBreak())

    # =========================================================================
    # 8. SECTION 6: IMPORTANT COMPARISON TABLES
    # =========================================================================
    story.append(TOCRegister("sec_tables"))
    story.append(Paragraph("12. Important Comparative Analysis Tables", h1_style))
    story.append(Paragraph("This section presents concise academic comparisons across the core architectural and concurrency alternatives evaluated in the assignment.", body_style))

    # Table 1: Exception vs Error Codes
    story.append(Paragraph("Table 1: Exception Handling vs. Pre-Condition Error Codes", h3_style))
    t1_data = [
        [Paragraph("<b>Evaluation Dimension</b>", table_header), Paragraph("<b>Structured Exception Handling</b>", table_header), Paragraph("<b>Pre-Condition Error Codes</b>", table_header)],
        [Paragraph("<b>Code Readability</b>", table_text), Paragraph("High: Happy path separated from error handling", table_text), Paragraph("Low: Arrow anti-pattern with nested checks", table_text)],
        [Paragraph("<b>Check Frequency</b>", table_text), Paragraph("Zero caller checks; JVM unwinds automatically", table_text), Paragraph("Every method invocation requires 'if (code != 0)'", table_text)],
        [Paragraph("<b>Happy-Path Time</b>", table_text), Paragraph("O(1) zero-cost exception tables", table_text), Paragraph("O(1) conditional branching overhead", table_text)],
        [Paragraph("<b>Failure-Path Time</b>", table_text), Paragraph("O(D) stack traversal: 666.7 ns/op (Measured)", table_text), Paragraph("O(1) primitive return: 65.1 ns/op (Measured)", table_text)],
        [Paragraph("<b>Memory Allocation</b>", table_text), Paragraph("Heap allocation of Throwable and stack frames", table_text), Paragraph("Zero heap allocation (passed via register)", table_text)],
        [Paragraph("<b>Engineering Role</b>", table_text), Paragraph("Reserved for exceptional and failure conditions", table_text), Paragraph("Routine local validation and simple queries", table_text)]
    ]
    t1 = Table(t1_data, colWidths=[125, 195, 195])
    t1.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#EEEEEE")),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CCCCCC")),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('PADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t1)
    story.append(Spacer(1, 8))

    # Table 2: Manual Closing vs Try-with-resources
    story.append(Paragraph("Table 2: Manual finally Closing vs. Modern try-with-resources", h3_style))
    t2_data = [
        [Paragraph("<b>Evaluation Dimension</b>", table_header), Paragraph("<b>Manual try-finally Closing</b>", table_header), Paragraph("<b>Automatic try-with-resources</b>", table_header)],
        [Paragraph("<b>Closing Guarantee</b>", table_text), Paragraph("Fragile: Parsing exception skips close() statement", table_text), Paragraph("Deterministic: Unconditional close upon block exit", table_text)],
        [Paragraph("<b>Closing Order</b>", table_text), Paragraph("Manual: Prone to developer sequencing bugs", table_text), Paragraph("Automatic: Strict LIFO reverse declaration order", table_text)],
        [Paragraph("<b>Multiple Exceptions</b>", table_text), Paragraph("Exception in finally masks primary error", table_text), Paragraph("Primary preserved; closing error attached as suppressed", table_text)],
        [Paragraph("<b>Open Handle Leak</b>", table_text), Paragraph("1 handle leaked per corrupt file (Measured)", table_text), Paragraph("0 handles leaked under all failures (Measured)", table_text)],
        [Paragraph("<b>Syntax Overhead</b>", table_text), Paragraph("High: Requires verbose nested finally blocks", table_text), Paragraph("Concise: Clean try (Resource r = ...) declaration", table_text)]
    ]
    t2 = Table(t2_data, colWidths=[125, 195, 195])
    t2.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#EEEEEE")),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CCCCCC")),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('PADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t2)
    story.append(Spacer(1, 8))

    # Table 3: Thread vs Runnable
    story.append(Paragraph("Table 3: Extending java.lang.Thread vs. Implementing java.lang.Runnable", h3_style))
    t3_data = [
        [Paragraph("<b>Evaluation Dimension</b>", table_header), Paragraph("<b>Extending Thread</b>", table_header), Paragraph("<b>Implementing Runnable (Best Practice)</b>", table_header)],
        [Paragraph("<b>Class Inheritance</b>", table_text), Paragraph("Consumes single inheritance slot in Java", table_text), Paragraph("Class remains free to extend another domain class", table_text)],
        [Paragraph("<b>Coupling</b>", table_text), Paragraph("Tightly couples task logic with thread control", table_text), Paragraph("Decouples task logic from thread execution mechanics", table_text)],
        [Paragraph("<b>Reusability</b>", table_text), Paragraph("Cannot be passed to modern thread pools", table_text), Paragraph("Directly compatible with ExecutorService pools", table_text)],
        [Paragraph("<b>Design Philosophy</b>", table_text), Paragraph("Inheritance-based (rarely recommended)", table_text), Paragraph("Composition-based (standard enterprise practice)", table_text)]
    ]
    t3 = Table(t3_data, colWidths=[125, 195, 195])
    t3.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#EEEEEE")),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CCCCCC")),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('PADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t3)
    story.append(PageBreak())

    # Table 4: Sync Method vs Sync Block vs CAS
    story.append(Paragraph("Table 4: Synchronized Method vs. Synchronized Block vs. Lock-Free CAS", h3_style))
    t4_data = [
        [Paragraph("<b>Dimension</b>", table_header), Paragraph("<b>Synchronized Method</b>", table_header), Paragraph("<b>Synchronized Block (Per-Seat)</b>", table_header), Paragraph("<b>AtomicBoolean (CAS)</b>", table_header)],
        [Paragraph("<b>Lock Scope</b>", table_text), Paragraph("Entire instance monitor (this)", table_text), Paragraph("Fine-grained per-seat lock object", table_text), Paragraph("Lock-free hardware atomic primitive", table_text)],
        [Paragraph("<b>Concurrency</b>", table_text), Paragraph("Zero parallelism across seats", table_text), Paragraph("Full parallelism across distinct seats", table_text), Paragraph("Maximum lock-free core parallelism", table_text)],
        [Paragraph("<b>Measured Time</b>", table_text), Paragraph("51.55 ms (5,155 ns/op)", table_text), Paragraph("17.07 ms (1,706 ns/op)", table_text), Paragraph("15.17 ms (1,516 ns/op - Fastest)", table_text)],
        [Paragraph("<b>Memory Space</b>", table_text), Paragraph("0 extra bytes overhead", table_text), Paragraph("s * 16 bytes for lock array", table_text), Paragraph("s * 24 bytes for AtomicBoolean array", table_text)],
        [Paragraph("<b>Best Use Case</b>", table_text), Paragraph("Coarse operations & singletons", table_text), Paragraph("Multi-seat cinema booking systems", table_text), Paragraph("Ultra-high frequency financial/ticketing", table_text)]
    ]
    t4 = Table(t4_data, colWidths=[95, 140, 140, 140])
    t4.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#EEEEEE")),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CCCCCC")),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('PADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t4)
    story.append(Spacer(1, 8))

    # Table 5: wait() vs sleep()
    story.append(Paragraph("Table 5: Object.wait() vs. Thread.sleep()", h3_style))
    t5_data = [
        [Paragraph("<b>Dimension</b>", table_header), Paragraph("<b>Object.wait()</b>", table_header), Paragraph("<b>Thread.sleep(millis)</b>", table_header)],
        [Paragraph("<b>Class Origin</b>", table_text), Paragraph("java.lang.Object instance method", table_text), Paragraph("java.lang.Thread static method", table_text)],
        [Paragraph("<b>Lock Handling</b>", table_text), Paragraph("Atomically RELEASES monitor lock to other threads", table_text), Paragraph("HOLDS all acquired monitor locks throughout sleep", table_text)],
        [Paragraph("<b>Execution Context</b>", table_text), Paragraph("MUST be called inside a synchronized block/method", table_text), Paragraph("Can be called anywhere in application code", table_text)],
        [Paragraph("<b>Wakeup Trigger</b>", table_text), Paragraph("Explicit notify() / notifyAll() or timeout", table_text), Paragraph("Time expiration or Thread.interrupt()", table_text)],
        [Paragraph("<b>Primary Purpose</b>", table_text), Paragraph("Inter-thread coordination & condition signaling", table_text), Paragraph("Execution pacing, polling pauses, delays", table_text)]
    ]
    t5 = Table(t5_data, colWidths=[125, 195, 195])
    t5.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#EEEEEE")),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CCCCCC")),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('PADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t5)
    story.append(Spacer(1, 8))

    # Table 6: ExecutorService vs Manual Threads
    story.append(Paragraph("Table 6: ExecutorService Thread Pools vs. Unbounded Manual Threads", h3_style))
    t6_data = [
        [Paragraph("<b>Dimension</b>", table_header), Paragraph("<b>ExecutorService (Fixed Thread Pool)</b>", table_header), Paragraph("<b>Manual Raw Thread Spawning</b>", table_header)],
        [Paragraph("<b>Thread Limit</b>", table_text), Paragraph("Strictly bounded pool of P worker threads", table_text), Paragraph("Unbounded: Creates N raw OS thread objects", table_text)],
        [Paragraph("<b>Memory Footprint</b>", table_text), Paragraph("Fixed: P * 1 MB native stack (~8 MB for P=8)", table_text), Paragraph("Dangerous: N * 1 MB stack (10 GB for 10,000 tasks!)", table_text)],
        [Paragraph("<b>Lifecycle Cost</b>", table_text), Paragraph("Worker threads reused continuously from queue", table_text), Paragraph("Heavy creation and destruction OS kernel overhead", table_text)],
        [Paragraph("<b>Task Return Value</b>", table_text), Paragraph("Callable returns Future<T> with get() and exceptions", table_text), Paragraph("Runnable run() cannot return values directly", table_text)],
        [Paragraph("<b>Crash Risk</b>", table_text), Paragraph("Zero: Tasks queue gracefully under heavy load", table_text), Paragraph("Fatal OutOfMemoryError: unable to create native thread", table_text)]
    ]
    t6 = Table(t6_data, colWidths=[125, 195, 195])
    t6.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#EEEEEE")),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CCCCCC")),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('PADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t6)
    story.append(Spacer(1, 10))

    # =========================================================================
    # 9. SECTION 7: FINAL CONCLUSION & ACADEMIC SUMMARY
    # =========================================================================
    story.append(TOCRegister("sec_conclusion"))
    story.append(Paragraph("13. Final Conclusion & Academic Summary", h1_style))
    story.append(Paragraph(
        "Assignment 3 (CO3) provides an end-to-end practical and analytical exploration of Java's core robustness and concurrency mechanisms. Through systematic implementation, rigorous debugging, and empirical benchmarking, key software engineering principles were verified:",
        body_style
    ))
    story.append(Paragraph("1. <b>Exception Handling as Architectural Boundaries:</b> User-defined checked exceptions decouple domain logic from infrastructural failures. Exception chaining ensures root-cause diagnostic fidelity while sanitizing user-facing presentations.", bullet_style))
    story.append(Paragraph("2. <b>Resource Safety Guarantees:</b> Diagnostic debugging demonstrated that manual finally blocks are inherently prone to descriptor leaks under failure injection. The try-with-resources construct guarantees deterministic, reverse-order resource reclamation and suppresses secondary closing errors.", bullet_style))
    story.append(Paragraph("3. <b>Thread Life Cycle & Concurrency Control:</b> Thread creation mechanics were established by proving start() allocates native threads while run() executes synchronously. The non-deterministic nature of the OS thread scheduler was proven across 10 repeated runs, reinforcing why concurrent correctness must depend on synchronization barriers rather than timing assumptions.", bullet_style))
    story.append(Paragraph("4. <b>Synchronization & Deadlock Avoidance:</b> Critical sections in cinema ticketing were analyzed, proving that fine-grained locking and lock-free CAS primitives eliminate double-booking while delivering up to 3.4x higher throughput than coarse method locking. Reciprocal deadlock was resolved via global lock ordering, breaking Coffman's Circular Wait condition.", bullet_style))
    story.append(Paragraph("5. <b>High-Throughput Concurrent Systems:</b> A fault-tolerant processor successfully evaluated 10,000 score files across 8 worker threads in 6.7 seconds (1,492 files/sec) with zero unhandled failures, demonstrating the performance boundaries dictated by Amdahl's Law and I/O saturation.", bullet_style))
    story.append(Spacer(1, 6))

    verif_box = [
        [Paragraph("<b>Student Declaration:</b> I hereby confirm that all Java programs, empirical benchmarks, screenshots, diagrams, and analyses presented in this document were executed, measured, and verified from actual code running in the project environment. All code and documentation have been submitted to GitHub.", table_text)],
        [Paragraph("<b>Student Signature:</b> Geetika T (Register Number: 192511469) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b>Date:</b> November 2026", table_header)]
    ]
    t_vbox = Table(verif_box, colWidths=[515])
    t_vbox.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#F8F9FA")),
        ('BOX', (0,0), (-1,-1), 0.5, colors.HexColor("#888888")),
        ('PADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(t_vbox)

    return story

def main():
    print("Beginning 2-pass PDF compilation...")

    # PASS 1: Build story to populate TOC page numbers
    print("Pass 1: Recording TOC page numbers...")
    doc1 = SimpleDocTemplate(
        PDF_OUTPUT_PATH,
        pagesize=A4,
        leftMargin=40,
        rightMargin=40,
        topMargin=45,
        bottomMargin=45
    )
    story1 = build_pdf_story(is_second_pass=False)
    doc1.build(story1, canvasmaker=AcademicNumberedCanvas)
    print(f"Recorded TOC Page Numbers: {toc_page_numbers}")

    # PASS 2: Re-build story with populated TOC page numbers
    print("Pass 2: Generating final formatted PDF with dynamic TOC...")
    doc2 = SimpleDocTemplate(
        PDF_OUTPUT_PATH,
        pagesize=A4,
        leftMargin=40,
        rightMargin=40,
        topMargin=45,
        bottomMargin=45
    )
    story2 = build_pdf_story(is_second_pass=True)
    doc2.build(story2, canvasmaker=AcademicNumberedCanvas)

    file_size_kb = os.path.getsize(PDF_OUTPUT_PATH) / 1024
    print(f"Final PDF successfully created at: {PDF_OUTPUT_PATH}")
    print(f"File size: {file_size_kb:.1f} KB")

if __name__ == '__main__':
    main()
