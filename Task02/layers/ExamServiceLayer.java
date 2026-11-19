package Task02.layers;

import Task02.exceptions.ExamException;
import Task02.exceptions.ExamWindowClosedException;
import Task02.exceptions.StudentNotRegisteredException;
import Task02.exceptions.SubmissionStorageException;

import java.util.HashSet;
import java.util.Set;

/**
 * Service Layer:
 * Enforces business domain rules:
 * 1. Is student registered for course?
 * 2. Is current timestamp within active exam window?
 * 3. Delegates persistence to StorageLayer.
 */
public class ExamServiceLayer {

    private final StorageLayer storageLayer;
    private final Set<String> registeredStudents = new HashSet<>();
    private final long examWindowStartEpoch;
    private final long examWindowEndEpoch;
    private final String courseCode = "CSA0914";

    public ExamServiceLayer(StorageLayer storageLayer, long windowStartEpoch, long windowEndEpoch) {
        this.storageLayer = storageLayer;
        this.examWindowStartEpoch = windowStartEpoch;
        this.examWindowEndEpoch = windowEndEpoch;

        // Seed registered students
        registeredStudents.add("192511469"); // Geetika T
        registeredStudents.add("192511470"); // Registered Peer
    }

    public void processSubmission(String studentId, String submittedCourse, String fileName, long sizeKb, long currentEpoch)
            throws ExamException {

        // Rule 1: Course and Student Registration Check
        if (!courseCode.equalsIgnoreCase(submittedCourse) || !registeredStudents.contains(studentId)) {
            throw new StudentNotRegisteredException(
                "Access Denied: Student ID '" + studentId + "' is not officially registered for course '" + submittedCourse + "'",
                studentId, submittedCourse
            );
        }

        // Rule 2: Active Exam Window Verification
        if (currentEpoch > examWindowEndEpoch) {
            throw new ExamWindowClosedException(
                "Submission Rejected: The exam deadline for course " + courseCode + " elapsed at epoch " + examWindowEndEpoch + ". Submitted at " + currentEpoch,
                examWindowEndEpoch, currentEpoch
            );
        }

        if (currentEpoch < examWindowStartEpoch) {
            throw new ExamException("Submission Rejected: Exam window has not yet opened for course " + courseCode);
        }

        // Rule 3: Delegate to Storage Layer (propagates SubmissionStorageException)
        storageLayer.saveAnswerScript(studentId, fileName, sizeKb);
    }
}
