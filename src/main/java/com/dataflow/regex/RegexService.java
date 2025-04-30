package com.dataflow.regex;

import java.util.*;
import java.util.regex.*;

public class RegexService {

    /**
     * Validates if the given regex pattern is syntactically correct.
     * @param regexPattern the regex pattern to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidRegex(String regexPattern) {
        try {
            Pattern.compile(regexPattern);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    /**
     * Finds all matches of the given regex pattern in the input text.
     * Supports sets, ranges, alternations, shorthands, and quantifiers.
     * @param inputText the text to search
     * @param regexPattern the regex pattern
     * @return list of matched strings
     */
    public List<String> findMatches(String inputText, String regexPattern) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(inputText);

        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    /**
     * Replaces all occurrences of the regex pattern in the text with the replacement string.
     * @param inputText the text to modify
     * @param regexPattern the regex pattern
     * @param replacement the replacement string
     * @return modified text
     */
    public String replaceMatches(String inputText, String regexPattern, String replacement) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(inputText);
        return matcher.replaceAll(replacement);
    }

    /**
     * Counts the number of occurrences of the regex pattern in the input text.
     * @param inputText the text to search
     * @param regexPattern the regex pattern
     * @return number of matches
     */
    public long countMatches(String inputText, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(inputText);
        return matcher.results().count();
    }

    /**
     * Provides detailed match information (matched text, start index, end index)
     * @param inputText the text to search
     * @param regexPattern the regex pattern
     * @return list of match details
     */
    public List<Map<String, Object>> getMatchDetails(String inputText, String regexPattern) {
        List<Map<String, Object>> details = new ArrayList<>();
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(inputText);

        while (matcher.find()) {
            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("Match", matcher.group());
            detail.put("Start", matcher.start());
            detail.put("End", matcher.end());
            details.add(detail);
        }

        return details;
    }
}
