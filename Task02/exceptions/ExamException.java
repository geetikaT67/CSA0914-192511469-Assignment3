package Task02.exceptions;

/**
 * Base checked exception for all examination portal errors.
 * Checked because callers (Controller/Service) are required to anticipate
 * and handle business and operational submission failures.
 */
public class ExamException extends Exception {
    public ExamException(String message) {
        super(message);
    }

    public ExamException(String message, Throwable cause) {
        super(message, cause);
    }
}
