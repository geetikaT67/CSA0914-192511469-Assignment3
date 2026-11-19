package Task02.exceptions;

/**
 * Thrown when the submitting student ID is not registered for the course module.
 */
public class StudentNotRegisteredException extends ExamException {
    private final String studentId;
    private final String courseCode;

    public StudentNotRegisteredException(String message, String studentId, String courseCode) {
        super(message);
        this.studentId = studentId;
        this.courseCode = courseCode;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }
}
