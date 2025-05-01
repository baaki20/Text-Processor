package com.dataflow.service;

import java.util.List;
import java.util.Map;

public class TextProcessingService {

    private final RegexService regexService;

    public TextProcessingService() {
        this.regexService = new RegexService();
    }

    /**
     * Searches the input text for matches of the given regex pattern.
     * Returns detailed match info.
     * @param inputText text to search
     * @param regexPattern regex pattern
     * @return list of match details (match text, start index, end index)
     */
    public List<Map<String, Object>> searchPatternInText(String inputText, String regexPattern) {
        if (!regexService.isValidRegex(regexPattern)) {
            throw new IllegalArgumentException("Invalid regex pattern: " + regexPattern);
        }

        return regexService.getMatchDetails(inputText, regexPattern);
    }

    /**
     * Replaces occurrences of the pattern in text with the replacement string.
     * @param inputText text to modify
     * @param regexPattern regex pattern to match
     * @param replacement replacement string
     * @return modified text
     */
    public String replacePatternInText(String inputText, String regexPattern, String replacement) {
        if (!regexService.isValidRegex(regexPattern)) {
            throw new IllegalArgumentException("Invalid regex pattern: " + regexPattern);
        }

        return regexService.replaceMatches(inputText, regexPattern, replacement);
    }
}
