package com.dataflow;

import com.dataflow.regex.RegexService;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainApp {

    public static void main(String[] args) {
        RegexService regexService = new RegexService();
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

        // Example usage
        String pattern = "\\bdata\\b";  // matches the word "data"
        List<String> matches = regexService.findMatches(inputText, pattern);

        System.out.println("Matches Found:");
        matches.forEach(System.out::println);

        String replacedText = regexService.replaceMatches(inputText, pattern, "DATA");
        System.out.println("\nModified Text:\n" + replacedText);

        long count = regexService.countMatches(inputText, pattern);
        System.out.println("\nNumber of Matches: " + count);
    }
}
