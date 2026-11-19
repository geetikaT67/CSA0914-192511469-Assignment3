package Task07;

import Task07.models.ProcessingResult;
import Task07.models.ProcessingSummary;

import java.util.ArrayList;
import java.util.List;

/**
 * Task 07: Alternative Concept - Java 8 Parallel Streams vs ExecutorService.
 */
public class ParallelStreamProcessorDemo {

    public static void main(String[] args) {
        System.out.println("=== TASK 07 ALTERNATIVE CONCEPT: PARALLEL STREAMS ===");

        int sampleSize = 1000;
        List<String> files = new ArrayList<>(sampleSize);
        for (int i = 1; i <= sampleSize; i++) {
            files.add("TestData/score_files/score_STU" + String.format("%06d", i) + ".csv");
        }

        ProcessingSummary streamSummary = new ProcessingSummary();
        long start = System.currentTimeMillis();

        // Using parallelStream()
        files.parallelStream().forEach(path -> {
            String studentId = path.substring(path.lastIndexOf('_') + 1).replace(".csv", "");
            ScoreFileProcessorTask task = new ScoreFileProcessorTask(path, studentId);
            ProcessingResult res = task.call();
            streamSummary.record(res);
        });

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("ParallelStream completed " + sampleSize + " files in " + elapsed + " ms.");
        System.out.println("Summary: " + streamSummary);
        System.out.println("\nComparison Insight:");
        System.out.println("1. Parallel streams share the common ForkJoinPool.commonPool() across entire JVM.");
        System.out.println("2. Long-running I/O file reads on parallel streams can block other unrelated parallel stream operations.");
        System.out.println("3. Custom ExecutorService allows isolated thread pool sizing dedicated specifically to file I/O.");
    }
}
