package Task07;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Task 07: Test Data Generator for 10,000 Score Files and Hand-Checked Baseline.
 */
public class DataGenerator {

    public static void generate10kWorkload(String outputDir, int total, int corruptCount, int missingCount) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        Random rand = new Random(192511469L);
        int validCount = total - corruptCount - missingCount; // 9,600

        System.out.println("Generating 10,000 file dataset in " + outputDir + "...");
        System.out.printf("  Valid: %d | Corrupt: %d | Missing: %d\n", validCount, corruptCount, missingCount);

        for (int i = 1; i <= total; i++) {
            String studentId = String.format("STU%06d", i);
            File file = new File(dir, "score_" + studentId + ".csv");

            if (i > validCount + corruptCount) {
                // Missing file: Do not create file on disk
                continue;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("SubjectCode,Marks\n");

                if (i > validCount && i <= validCount + corruptCount) {
                    // Corrupt file
                    if (i % 2 == 0) {
                        writer.write("CSA0914,CORRUPT_SCORE\n");
                    } else {
                        writer.write("CSA0914,999.00\n"); // Out of bounds
                    }
                } else {
                    // Valid file
                    double m1 = 40 + rand.nextInt(55);
                    double m2 = 45 + rand.nextInt(50);
                    double m3 = 35 + rand.nextInt(60);
                    writer.write("CSA0914," + m1 + "\n");
                    writer.write("CSA0915," + m2 + "\n");
                    writer.write("CSA0916," + m3 + "\n");
                }
            }
        }
        System.out.println("Generation complete.");
    }

    public static void generateHandCheckedDataset(String outputDir) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        // 20 files total:
        // 1-12: Valid Pass
        // 13-15: Valid Fail
        // 16-18: Corrupt
        // 19-20: Missing
        for (int i = 1; i <= 20; i++) {
            String id = String.format("CHK%03d", i);
            File f = new File(dir, "score_" + id + ".csv");

            if (i >= 19) {
                // Missing files
                continue;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                bw.write("SubjectCode,Marks\n");
                if (i <= 12) {
                    // Valid Pass (Average >= 50)
                    bw.write("Sub1,75\nSub2,80\nSub3,85\n");
                } else if (i <= 15) {
                    // Valid Fail (Average < 50)
                    bw.write("Sub1,30\nSub2,35\nSub3,40\n");
                } else if (i == 16) {
                    bw.write("Sub1,INVALID_VAL\n");
                } else if (i == 17) {
                    bw.write("Sub1,150\n"); // Out of bounds
                } else {
                    // Empty corrupt file
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            generateHandCheckedDataset("TestData/hand_checked");
            generate10kWorkload("TestData/score_files", 10_000, 250, 150);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
