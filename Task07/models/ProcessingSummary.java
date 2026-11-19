package Task07.models;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task 07: Thread-safe summary aggregator of processing metrics across all 10,000 files.
 */
public class ProcessingSummary {
    private final AtomicInteger totalFiles = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger passedCount = new AtomicInteger(0);
    private final AtomicInteger failedCount = new AtomicInteger(0);
    private final AtomicInteger corruptCount = new AtomicInteger(0);
    private final AtomicInteger missingCount = new AtomicInteger(0);
    private final AtomicInteger unhandledFailures = new AtomicInteger(0);

    public void record(ProcessingResult result) {
        totalFiles.incrementAndGet();
        switch (result.getStatus()) {
            case SUCCESS -> {
                successCount.incrementAndGet();
                if (result.isPassed()) {
                    passedCount.incrementAndGet();
                } else {
                    failedCount.incrementAndGet();
                }
            }
            case CORRUPT_DATA -> corruptCount.incrementAndGet();
            case FILE_NOT_FOUND -> missingCount.incrementAndGet();
            case UNEXPECTED_ERROR -> unhandledFailures.incrementAndGet();
        }
    }

    public int getTotalFiles() { return totalFiles.get(); }
    public int getSuccessCount() { return successCount.get(); }
    public int getPassedCount() { return passedCount.get(); }
    public int getFailedCount() { return failedCount.get(); }
    public int getCorruptCount() { return corruptCount.get(); }
    public int getMissingCount() { return missingCount.get(); }
    public int getUnhandledFailures() { return unhandledFailures.get(); }

    @Override
    public String toString() {
        return String.format(
            "ProcessingSummary: Total=%d | Valid=%d (Passed=%d, Failed=%d) | Corrupt=%d | Missing=%d | Unhandled=%d",
            totalFiles.get(), successCount.get(), passedCount.get(), failedCount.get(),
            corruptCount.get(), missingCount.get(), unhandledFailures.get()
        );
    }
}
