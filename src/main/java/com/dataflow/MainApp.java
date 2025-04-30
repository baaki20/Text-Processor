package com.dataflow;

import com.dataflow.regex.RegexService;
import com.dataflow.file.FileProcessingService;
import com.dataflow.stream.StreamProcessingService;
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

        String inputText = "";

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

        } catch (Exception e) {
            LoggerService.error("Unexpected error during processing", e);
        }

        LoggerService.info("Application finished.");
    }
}
