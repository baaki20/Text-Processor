package com.dataflow.regex;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class RegexService {

    /**
     * Finds all matches of the given regex pattern in the input text.
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
}
