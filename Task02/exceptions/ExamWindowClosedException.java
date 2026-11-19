package Task02.exceptions;

/**
 * Thrown when a student attempts to submit an exam after the submission deadline has elapsed.
 */
public class ExamWindowClosedException extends ExamException {
    private final long deadlineEpoch;
    private final long submissionEpoch;

    public ExamWindowClosedException(String message, long deadlineEpoch, long submissionEpoch) {
        super(message);
        this.deadlineEpoch = deadlineEpoch;
        this.submissionEpoch = submissionEpoch;
    }

    public long getDeadlineEpoch() {
        return deadlineEpoch;
    }

    public long getSubmissionEpoch() {
        return submissionEpoch;
    }
}
