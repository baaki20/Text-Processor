package com.dataflow.service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.*;

public class StreamProcessingService {

    /**
     * Calculates the frequency of each word in the given text.
     * @param inputText the text to analyze
     * @return map of word -> frequency
     */
    public Map<String, Long> computeWordFrequency(String inputText) {
        return Arrays.stream(inputText.split("\\W+"))
                .map(String::toLowerCase)
                .filter(word -> !word.isBlank())
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()));
    }

    /**
     * Filters lines in the text that match the given regex pattern.
     * @param inputText full text
     * @param regexPattern regex pattern to match
     * @return list of matching lines
     */
    public List<String> filterLinesByPattern(String inputText, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);

        return Arrays.stream(inputText.split("\\R"))  // splits by any line separator
                .filter(line -> pattern.matcher(line).find())
                .collect(Collectors.toList());
    }

    /**
     * Provides basic text summarization: word count, line count, character count.
     * @param inputText text to summarize
     * @return map of summary metrics
     */
    public Map<String, Long> summarizeText(String inputText) {
        long wordCount = Arrays.stream(inputText.split("\\W+"))
                .filter(w -> !w.isBlank()).count();

        long lineCount = inputText.lines().count();

        long charCount = inputText.chars().count();

        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("Word Count", wordCount);
        summary.put("Line Count", lineCount);
        summary.put("Character Count", charCount);

        return summary;
    }
}
