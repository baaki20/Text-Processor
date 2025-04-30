package com.dataflow;

import com.dataflow.regex.RegexService;
import com.dataflow.file.FileProcessingService;
import com.dataflow.stream.StreamProcessingService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class MainApp {

    public static void main(String[] args) {
        RegexService regexService = new RegexService();
        FileProcessingService fileProcessingService = new FileProcessingService();
        StreamProcessingService streamProcessingService = new StreamProcessingService();

        String inputText = "";

        // Load sample.txt from classpath
        try (InputStream inputStream = MainApp.class.getClassLoader().getResourceAsStream("sample.txt")) {
            if (inputStream == null) {
                System.err.println("Resource file 'sample.txt' not found in classpath.");
                return;
            }
            inputText = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            System.err.println("Error reading resource file: " + e.getMessage());
            return;
        }

        // Requirement 1 usage — Regex processing
        String pattern = "\\bdata\\b";
        List<String> matches = regexService.findMatches(inputText, pattern);
        System.out.println("Matches Found:");
        matches.forEach(System.out::println);

        String replacedText = regexService.replaceMatches(inputText, pattern, "DATA");
        System.out.println("\nModified Text:\n" + replacedText);

        long count = regexService.countMatches(inputText, pattern);
        System.out.println("\nNumber of Matches: " + count);

        // Requirement 2 usage — File writing
        Path outputPath = Path.of("output.txt");
        try {
            fileProcessingService.writeFile(outputPath, replacedText);
            System.out.println("\nModified text successfully written to output.txt");
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }

        // Optional: Example of line-by-line processing
        try {
            fileProcessingService.processFileLineByLine(
                    outputPath,
                    Path.of("output_processed.txt"),
                    line -> line.toUpperCase()
            );
            System.out.println("Processed output written to output_processed.txt with all text capitalized");
        } catch (IOException e) {
            System.err.println("Error processing file line by line: " + e.getMessage());
        }

        // Requirement 3 — Stream processing

        // a) Word frequency
        Map<String, Long> freqMap = streamProcessingService.computeWordFrequency(inputText);
        System.out.println("\nWord Frequencies:");
        freqMap.forEach((word, counts) -> System.out.println(word + ": " + counts));

        // b) Pattern matching in lines
        String linePattern = "you";
        List<String> matchedLines = streamProcessingService.filterLinesByPattern(inputText, linePattern);
        System.out.println("\nLines containing '" + linePattern + "':");
        matchedLines.forEach(System.out::println);

        // c) Text summarization
        Map<String, Long> summary = streamProcessingService.summarizeText(inputText);
        System.out.println("\nText Summary:");
        summary.forEach((metric, value) -> System.out.println(metric + ": " + value));
    }
}
