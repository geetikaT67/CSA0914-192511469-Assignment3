package Task02.layers;

import Task02.exceptions.ExamException;
import Task02.exceptions.ExamWindowClosedException;
import Task02.exceptions.FileSizeLimitExceededException;
import Task02.exceptions.StudentNotRegisteredException;
import Task02.exceptions.SubmissionStorageException;

/**
 * Controller Layer:
 * Top-level application gateway facing the web browser or mobile client.
 * Catches all propagated ExamExceptions, translates them into user-facing status responses,
 * and prevents low-level system internal traces (e.g. disk paths) from exposing to the student.
 */
public class ExamControllerLayer {

    private final ExamServiceLayer examService;

    public ExamControllerLayer(ExamServiceLayer examService) {
        this.examService = examService;
    }

    public SubmissionResponse handleSubmission(String studentId, String courseCode, String fileName, long sizeKb, long timestamp) {
        System.out.println("\n[Controller] Incoming submission: Student=" + studentId + ", Course=" + courseCode + ", File=" + fileName + " (" + sizeKb + " KB)");

        try {
            examService.processSubmission(studentId, courseCode, fileName, sizeKb, timestamp);
            return new SubmissionResponse(200, "SUBMISSION ACCEPTED", "Your exam submission '" + fileName + "' was successfully recorded. Best wishes!");

        } catch (StudentNotRegisteredException e) {
            System.err.println("  [Controller-Log]: Registration violation: " + e.getMessage());
            return new SubmissionResponse(403, "REGISTRATION ERROR",
                "You are not registered for " + e.getCourseCode() + ". Please contact the academic registrar office.");

        } catch (ExamWindowClosedException e) {
            System.err.println("  [Controller-Log]: Late submission violation: " + e.getMessage());
            return new SubmissionResponse(400, "DEADLINE EXPIRED",
                "The examination window has closed. Late submissions cannot be accepted.");

        } catch (FileSizeLimitExceededException e) {
            System.err.println("  [Controller-Log]: File size violation: " + e.getMessage());
            return new SubmissionResponse(413, "PAYLOAD TOO LARGE",
                "Your file (" + e.getFileSizeKb() + " KB) exceeds the maximum limit (" + e.getMaxAllowedKb() + " KB). Please compress your PDF.");

        } catch (SubmissionStorageException e) {
            // Secure exception handling: root cause (IOException) logged on server, sanitized message shown to student
            System.err.println("  [Controller-Log]: Storage failure: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  [Controller-Log]: Root Cause (" + e.getCause().getClass().getName() + "): " + e.getCause().getMessage());
            }
            return new SubmissionResponse(500, "SYSTEM STORAGE ERROR",
                "Unable to safely store your submission due to an internal server storage error. Please retry in a few moments or contact support.");

        } catch (ExamException e) {
            System.err.println("  [Controller-Log]: General exam exception: " + e.getMessage());
            return new SubmissionResponse(400, "SUBMISSION REJECTED", e.getMessage());
        }
    }

    public static class SubmissionResponse {
        private final int statusCode;
        private final String statusTitle;
        private final String studentMessage;

        public SubmissionResponse(int statusCode, String statusTitle, String studentMessage) {
            this.statusCode = statusCode;
            this.statusTitle = statusTitle;
            this.studentMessage = studentMessage;
        }

        public int getStatusCode() { return statusCode; }
        public String getStatusTitle() { return statusTitle; }
        public String getStudentMessage() { return studentMessage; }

        @Override
        public String toString() {
            return "[HTTP " + statusCode + " - " + statusTitle + "]: " + studentMessage;
        }
    }
}
