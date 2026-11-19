package Task07.models;

/**
 * Task 07: Encapsulates the immutable result of processing an individual answer-script score file.
 * Catches all possible outcomes including SUCCESS, CORRUPT_DATA, and FILE_NOT_FOUND.
 */
public class ProcessingResult {

    public enum Status {
        SUCCESS,
        CORRUPT_DATA,
        FILE_NOT_FOUND,
        UNEXPECTED_ERROR
    }

    private final String fileName;
    private final String studentId;
    private final Status status;
    private final double totalMarks;
    private final double averageMarks;
    private final boolean passed;
    private final String errorMessage;

    public ProcessingResult(String fileName, String studentId, Status status,
                            double totalMarks, double averageMarks, boolean passed, String errorMessage) {
        this.fileName = fileName;
        this.studentId = studentId;
        this.status = status;
        this.totalMarks = totalMarks;
        this.averageMarks = averageMarks;
        this.passed = passed;
        this.errorMessage = errorMessage;
    }

    public static ProcessingResult success(String fileName, String studentId, double totalMarks, double averageMarks, boolean passed) {
        return new ProcessingResult(fileName, studentId, Status.SUCCESS, totalMarks, averageMarks, passed, null);
    }

    public static ProcessingResult corrupt(String fileName, String studentId, String errorMessage) {
        return new ProcessingResult(fileName, studentId, Status.CORRUPT_DATA, 0.0, 0.0, false, errorMessage);
    }

    public static ProcessingResult missing(String fileName, String studentId, String errorMessage) {
        return new ProcessingResult(fileName, studentId, Status.FILE_NOT_FOUND, 0.0, 0.0, false, errorMessage);
    }

    public static ProcessingResult error(String fileName, String studentId, String errorMessage) {
        return new ProcessingResult(fileName, studentId, Status.UNEXPECTED_ERROR, 0.0, 0.0, false, errorMessage);
    }

    public String getFileName() { return fileName; }
    public String getStudentId() { return studentId; }
    public Status getStatus() { return status; }
    public double getTotalMarks() { return totalMarks; }
    public double getAverageMarks() { return averageMarks; }
    public boolean isPassed() { return passed; }
    public String getErrorMessage() { return errorMessage; }
}
