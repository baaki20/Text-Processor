package com.dataflow;

import com.dataflow.regex.RegexService;
import com.dataflow.file.FileProcessingService;
import com.dataflow.stream.StreamProcessingService;
import com.dataflow.text.TextProcessingService;
import com.dataflow.util.LoggerService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class MainApp {

    public static void main(String[] args) {
        LoggerService.info("Application started.");

        RegexService regexService = new RegexService();
        FileProcessingService fileProcessingService = new FileProcessingService();
        StreamProcessingService streamProcessingService = new StreamProcessingService();
        TextProcessingService textProcessingService = new TextProcessingService();

        String inputText = "";

        // Validate pattern
        String userPattern = "[a-zA-Z]+\\d{2}"; // Example: word followed by exactly 2 digits

        if (regexService.isValidRegex(userPattern)) {
            LoggerService.info("User-provided regex pattern is valid: " + userPattern);

            List<Map<String, Object>> matchDetails = regexService.getMatchDetails(inputText, userPattern);
            System.out.println("\nDetailed Match Info:");
            matchDetails.forEach(detail -> System.out.println(detail));

        } else {
            LoggerService.warn("Invalid regex pattern provided: " + userPattern);
        }

        // Load sample.txt from classpath
        try (InputStream inputStream = MainApp.class.getClassLoader().getResourceAsStream("sample.txt")) {
            if (inputStream == null) {
                LoggerService.error("Resource file 'sample.txt' not found in classpath.", null);
                return;
            }
            inputText = new String(inputStream.readAllBytes());
            LoggerService.info("'sample.txt' loaded successfully.");
        } catch (IOException e) {
            LoggerService.error("Error reading resource file", e);
            return;
        }

        try {
            // Requirement 1 — Regex usage
            String pattern = "\\bdata\\b";
            List<String> matches = regexService.findMatches(inputText, pattern);
            LoggerService.info("Regex matches found: " + matches.size());

            // Requirement 2 — File writing
            String replacedText = regexService.replaceMatches(inputText, pattern, "DATA");
            Path outputPath = Path.of("output.txt");
            fileProcessingService.writeFile(outputPath, replacedText);
            LoggerService.info("Modified text written to output.txt");

            // Requirement 3 — Stream processing
            Map<String, Long> freqMap = streamProcessingService.computeWordFrequency(inputText);
            LoggerService.info("Word frequency computed. Unique words: " + freqMap.size());

            String linePattern = "you";
            List<String> matchedLines = streamProcessingService.filterLinesByPattern(inputText, linePattern);
            LoggerService.info("Lines matching '" + linePattern + "': " + matchedLines.size());

            Map<String, Long> summary = streamProcessingService.summarizeText(inputText);
            LoggerService.info("Text summarization complete. Metrics: " + summary);

            // === Requirement 6 Demo ===
            String replacement = "[REDACTED]";

            // a) Search pattern
            List<Map<String, Object>> matchDetails = textProcessingService.searchPatternInText(inputText, userPattern);
            System.out.println("\nPattern Search Results:");
            matchDetails.forEach(System.out::println);

            // b) Replace pattern
            String modifiedText = textProcessingService.replacePatternInText(inputText, userPattern, replacement);
            System.out.println("\nModified Text:");
            System.out.println(modifiedText);

            LoggerService.info("Text processing (search & replace) completed.");

        } catch (IllegalArgumentException e) {
            LoggerService.error("Regex pattern error", e);
        } catch (Exception e) {
            LoggerService.error("Unexpected error during processing", e);
        }

        LoggerService.info("Application finished.");
    }
}
