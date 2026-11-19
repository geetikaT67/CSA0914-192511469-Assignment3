package Task02;

import Task02.exceptions.*;
import Task02.layers.*;

/**
 * Task 02 Demonstration and Test Runner
 * Executes all failure scenarios and inspects exception propagation,
 * chained stack traces, and cause preservation.
 */
public class ExamPortalDemo {

    public static void main(String[] args) {
        long baseEpoch = 1700000000L;
        long windowStart = baseEpoch;
        long windowEnd = baseEpoch + 3600; // 1-hour exam window

        StorageLayer storage = new StorageLayer();
        ExamServiceLayer service = new ExamServiceLayer(storage, windowStart, windowEnd);
        ExamControllerLayer controller = new ExamControllerLayer(service);

        System.out.println("=========================================================");
        System.out.println("   TASK 02: ONLINE EXAMINATION PORTAL EXCEPTION SUITE    ");
        System.out.println("=========================================================");

        // Scenario 1: Valid Submission (Success)
        System.out.println("\n--- Scenario 1: Valid Submission ---");
        ExamControllerLayer.SubmissionResponse r1 = controller.handleSubmission(
            "192511469", "CSA0914", "GeetikaT_FinalScript.pdf", 250, baseEpoch + 1800
        );
        System.out.println("Result: " + r1);

        // Scenario 2: Student Not Registered
        System.out.println("\n--- Scenario 2: Student Not Registered ---");
        ExamControllerLayer.SubmissionResponse r2 = controller.handleSubmission(
            "192599999", "CSA0914", "Unknown_Script.pdf", 250, baseEpoch + 1800
        );
        System.out.println("Result: " + r2);

        // Scenario 3: Exam Window Closed (Late Submission)
        System.out.println("\n--- Scenario 3: Exam Window Closed ---");
        ExamControllerLayer.SubmissionResponse r3 = controller.handleSubmission(
            "192511469", "CSA0914", "GeetikaT_LateScript.pdf", 250, baseEpoch + 5000
        );
        System.out.println("Result: " + r3);

        // Scenario 4: File Size Limit Exceeded (> 500 KB)
        System.out.println("\n--- Scenario 4: File Size Exceeds Limit ---");
        ExamControllerLayer.SubmissionResponse r4 = controller.handleSubmission(
            "192511469", "CSA0914", "GeetikaT_HugeScript.pdf", 800, baseEpoch + 1800
        );
        System.out.println("Result: " + r4);

        // Scenario 5: Storage Layer IOException chained into SubmissionStorageException
        System.out.println("\n--- Scenario 5: Storage Layer Failure with Exception Chaining ---");
        ExamControllerLayer.SubmissionResponse r5 = controller.handleSubmission(
            "192511469", "CSA0914", "GeetikaT_QuotaExceeded.pdf", 450, baseEpoch + 1800
        );
        System.out.println("Result: " + r5);

        // Chaining Inspection & Demonstration of getCause()
        System.out.println("\n--- Deep Inspection of Chained Stack Trace (Student B Requirement) ---");
        try {
            storage.saveAnswerScript("192511469", "disk_error_demo.pdf", 450);
        } catch (SubmissionStorageException sse) {
            System.out.println("Caught Outer Exception: " + sse.getClass().getName());
            System.out.println("Outer Message:          " + sse.getMessage());
            System.out.println("Direct getCause():       " + sse.getCause());
            System.out.println("Cause Class:            " + (sse.getCause() != null ? sse.getCause().getClass().getName() : "null"));
            System.out.println("Cause Detail Message:   " + (sse.getCause() != null ? sse.getCause().getMessage() : "null"));
            System.out.println("\nFull Chained Stack Trace:");
            sse.printStackTrace(System.out);
        }
    }
}
