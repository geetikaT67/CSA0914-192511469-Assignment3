package Task02;

import Task02.exceptions.SubmissionStorageException;
import java.io.IOException;

/**
 * Task 02 Demonstration: Information loss when rethrowing without preserving root cause.
 * Student B Requirement: Show what information is lost if the exception is re-thrown without its cause.
 */
public class UnchainedExceptionDemo {

    public static void saveWithChaining() throws SubmissionStorageException {
        try {
            throw new IOException("POSIX File System: Bad sector at block 0x7FFE9012");
        } catch (IOException e) {
            // Chained properly: passes e as second argument
            throw new SubmissionStorageException("Could not save submission file", e);
        }
    }

    public static void saveWithoutChaining() throws SubmissionStorageException {
        try {
            throw new IOException("POSIX File System: Bad sector at block 0x7FFE9012");
        } catch (IOException e) {
            // BAD PRACTICE: Drops root cause 'e'
            throw new SubmissionStorageException("Could not save submission file");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 1. WITH EXCEPTION CHAINING ===");
        try {
            saveWithChaining();
        } catch (SubmissionStorageException e) {
            System.out.println("e.getCause() is: " + e.getCause());
            System.out.println("Has underlying diagnostic details: YES");
        }

        System.out.println("\n=== 2. WITHOUT EXCEPTION CHAINING (INFORMATION LOST) ===");
        try {
            saveWithoutChaining();
        } catch (SubmissionStorageException e) {
            System.out.println("e.getCause() is: " + e.getCause());
            System.out.println("Has underlying diagnostic details: NO (LOST FOREVER!)");
            System.out.println("Developer/SRE cannot determine if error was Disk Full, Permission Denied, or Network Timeout!");
        }
    }
}
