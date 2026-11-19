package Task02.layers;

import Task02.exceptions.FileSizeLimitExceededException;
import Task02.exceptions.SubmissionStorageException;
import java.io.IOException;

/**
 * Storage Layer:
 * Responsible for physical file persistence on disk or cloud storage.
 * Directly encounters low-level java.io.IOException and wraps it into
 * domain-specific SubmissionStorageException with root cause chaining.
 */
public class StorageLayer {

    private static final long MAX_FILE_SIZE_KB = 500; // 500 KB limit

    public void saveAnswerScript(String studentId, String fileName, long sizeKb)
            throws SubmissionStorageException {
        // Validation 1: File size threshold
        if (sizeKb > MAX_FILE_SIZE_KB) {
            throw new FileSizeLimitExceededException(
                "Upload rejected: File '" + fileName + "' size (" + sizeKb + " KB) exceeds maximum allowed limit (" + MAX_FILE_SIZE_KB + " KB)",
                sizeKb, MAX_FILE_SIZE_KB
            );
        }

        // Validation 2: Simulate physical disk I/O operation
        try {
            // Simulate disk quota overflow for files between 400KB and 500KB
            if (sizeKb > 400) {
                throw new IOException("POSIX File System Error: Disk quota exceeded on /var/exam_storage partition");
            }
            // Simulated successful file write
            System.out.println("  [StorageLayer] Successfully wrote " + fileName + " (" + sizeKb + " KB) to disk for student " + studentId);
        } catch (IOException ioEx) {
            // Exception Chaining: Wrap raw IOException in domain SubmissionStorageException
            throw new SubmissionStorageException(
                "Storage subsystem failure while persisting answer script '" + fileName + "' for student " + studentId,
                ioEx
            );
        }
    }
}
