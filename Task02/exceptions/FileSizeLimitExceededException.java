package Task02.exceptions;

/**
 * Thrown when the uploaded answer script exceeds the allowable file size limit.
 */
public class FileSizeLimitExceededException extends SubmissionStorageException {
    private final long fileSizeKb;
    private final long maxAllowedKb;

    public FileSizeLimitExceededException(String message, long fileSizeKb, long maxAllowedKb) {
        super(message);
        this.fileSizeKb = fileSizeKb;
        this.maxAllowedKb = maxAllowedKb;
    }

    public long getFileSizeKb() {
        return fileSizeKb;
    }

    public long getMaxAllowedKb() {
        return maxAllowedKb;
    }
}
