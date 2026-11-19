package Task02.exceptions;

/**
 * Thrown when an underlying persistence or I/O failure occurs while saving the exam script.
 * Preserves the root cause (e.g. java.io.IOException) via exception chaining.
 */
public class SubmissionStorageException extends ExamException {
    public SubmissionStorageException(String message) {
        super(message);
    }

    public SubmissionStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
