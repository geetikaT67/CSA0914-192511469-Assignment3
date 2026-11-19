package Task07;

import Task07.models.ProcessingResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Task 07: Callable Task for processing a single score file.
 * Encapsulates file I/O, validation, calculation, and fault-tolerant exception handling.
 */
public class ScoreFileProcessorTask implements Callable<ProcessingResult> {

    private final String filePath;
    private final String studentId;

    public ScoreFileProcessorTask(String filePath, String studentId) {
        this.filePath = filePath;
        this.studentId = studentId;
    }

    @Override
    public ProcessingResult call() {
        File file = new File(filePath);

        // Failure Case 1: Missing File
        if (!file.exists()) {
            return ProcessingResult.missing(file.getName(), studentId, "File not found at: " + filePath);
        }

        double totalMarks = 0.0;
        int subjectCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Header
            if (line == null) {
                return ProcessingResult.corrupt(file.getName(), studentId, "Empty file");
            }

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) {
                    return ProcessingResult.corrupt(file.getName(), studentId, "Malformed line: '" + line + "'");
                }

                // Failure Case 2: Corrupt non-numeric score
                double score;
                try {
                    score = Double.parseDouble(parts[1].trim());
                } catch (NumberFormatException nfe) {
                    return ProcessingResult.corrupt(file.getName(), studentId, "Non-numeric score: '" + parts[1] + "'");
                }

                // Failure Case 3: Score range constraint violation
                if (score < 0 || score > 100) {
                    return ProcessingResult.corrupt(file.getName(), studentId, "Score out of bounds (0-100): " + score);
                }

                totalMarks += score;
                subjectCount++;
            }

            if (subjectCount == 0) {
                return ProcessingResult.corrupt(file.getName(), studentId, "No valid subject scores found");
            }

            double average = totalMarks / subjectCount;
            boolean passed = average >= 50.0; // 50% pass requirement
            return ProcessingResult.success(file.getName(), studentId, totalMarks, average, passed);

        } catch (FileNotFoundException e) {
            return ProcessingResult.missing(file.getName(), studentId, e.getMessage());
        } catch (IOException e) {
            return ProcessingResult.error(file.getName(), studentId, "I/O failure: " + e.getMessage());
        } catch (Exception e) {
            return ProcessingResult.error(file.getName(), studentId, "Unexpected runtime error: " + e.getMessage());
        }
    }
}
