package com.dataflow.file;

import java.io.*;
import java.nio.file.Path;
import java.util.function.Function;

public class FileProcessingService {

    /**
     * Reads the content of a file line by line and returns the entire text as a single string.
     * @param filePath path to the input file
     * @return entire file content as string
     * @throws IOException if file reading fails
     */
    public String readFile(Path filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append(System.lineSeparator());
            }
        }

        return contentBuilder.toString();
    }

    /**
     * Writes the given content to a file.
     * @param filePath path to the output file
     * @param content text content to write
     * @throws IOException if file writing fails
     */
    public void writeFile(Path filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(content);
        }
    }

    /**
     * Processes a file line by line using a transformation function and writes the result to a new file.
     * @param inputPath path to input file
     * @param outputPath path to output file
     * @param lineProcessor function to process each line
     * @throws IOException if file operations fail
     */
    public void processFileLineByLine(Path inputPath, Path outputPath, Function<String, String> lineProcessor) throws IOException {
        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputPath.toFile()));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String processedLine = lineProcessor.apply(line);
                writer.write(processedLine);
                writer.newLine();
            }
        }
    }
}
