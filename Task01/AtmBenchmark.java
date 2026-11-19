package Task01;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Task 01: Benchmarking Exception Handling vs Error Codes.
 * Quantifies time in nanoseconds and memory overhead.
 */
public class AtmBenchmark {

    private static final int ITERATIONS = 200_000;

    public static void main(String[] args) {
        System.out.println("Running benchmark: " + ITERATIONS + " iterations each...");

        // Warm up JVM
        for (int i = 0; i < 20_000; i++) {
            errorMethod(true);
            try { exceptionMethod(true); } catch (Exception ignored) {}
        }

        // 1. Measure Error Codes
        long startError = System.nanoTime();
        int errCount = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            int code = errorMethod(i % 2 == 0);
            if (code != 0) {
                errCount++;
            }
        }
        long durationError = System.nanoTime() - startError;

        // 2. Measure Exception Handling
        long startException = System.nanoTime();
        int excCount = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            try {
                exceptionMethod(i % 2 == 0);
            } catch (IllegalArgumentException e) {
                excCount++;
            }
        }
        long durationException = System.nanoTime() - startException;

        double avgNsError = (double) durationError / ITERATIONS;
        double avgNsException = (double) durationException / ITERATIONS;
        double ratio = (double) durationException / durationError;

        System.out.printf("Error Codes Total Time:     %10.2f ms (Avg: %8.2f ns/op)\n", durationError / 1e6, avgNsError);
        System.out.printf("Exceptions Total Time:      %10.2f ms (Avg: %8.2f ns/op)\n", durationException / 1e6, avgNsException);
        System.out.printf("Slowdown Factor:            %10.2fx\n", ratio);

        // Save CSV experimental data
        try (FileWriter fw = new FileWriter("ExperimentalData/task01_benchmark.csv")) {
            fw.write("Approach,TotalTimeMs,AvgTimeNs,ErrorCount,RatioVsErrorCode\n");
            fw.write(String.format("ErrorCode,%.3f,%.2f,%d,1.0\n", durationError / 1e6, avgNsError, errCount));
            fw.write(String.format("ExceptionHandling,%.3f,%.2f,%d,%.2f\n", durationException / 1e6, avgNsException, excCount, ratio));
            System.out.println("Benchmark data saved to ExperimentalData/task01_benchmark.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int errorMethod(boolean fail) {
        if (fail) return -1;
        return 0;
    }

    private static void exceptionMethod(boolean fail) {
        if (fail) throw new IllegalArgumentException("Failure triggered");
    }
}
