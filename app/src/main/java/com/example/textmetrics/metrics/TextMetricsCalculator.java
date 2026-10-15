package com.example.textmetrics.metrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reusable text metric calculation engine.
 * Calculates:
 * 1. Number of sentences
 * 2. Number of words
 * 3. Number of punctuation marks
 * 4. Number of numeric digits
 */
public final class TextMetricsCalculator {

    // Regex matching sentence chunks: sequence of non-delimiters (preserving decimal periods like 3.14)
    // followed by [.!?\u2026]+ or end-of-string
    private static final Pattern SENTENCE_PATTERN =
            Pattern.compile("(?:[^.!?\\u2026]|(?<=\\d)\\.(?=\\d))+(?:[.!?\\u2026]+|$)");

    // Regex matching any word (letters and digits, including interior apostrophes or hyphens)
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}\\p{N}]+(?:['’\\-][\\p{L}\\p{N}]+)*");

    // Regex matching any Unicode punctuation mark (\p{P})
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("\\p{P}");

    // Pattern to verify a sentence chunk contains actual alphanumeric characters
    private static final Pattern HAS_WORD_CONTENT = Pattern.compile(".*[\\p{L}\\p{N}].*");

    private TextMetricsCalculator() {
        // Private constructor for utility class
    }

    /**
     * Calculates all 4 metrics for the given text.
     *
     * @param text input string to analyze
     * @return TextMetrics object containing counts
     */
    public static TextMetrics calculate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new TextMetrics(0, 0, 0, 0);
        }

        int sentences = countSentences(text);
        int words = countWords(text);
        int punctuation = countPunctuation(text);
        int digits = countDigits(text);

        return new TextMetrics(sentences, words, punctuation, digits);
    }

    /**
     * Counts the number of sentences in the text.
     * Handles sentences terminated by [.!?] as well as open-ended final sentences without terminal punctuation.
     *
     * @param text input string
     * @return number of valid sentences
     */
    public static int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        int count = 0;
        Matcher matcher = SENTENCE_PATTERN.matcher(text);
        while (matcher.find()) {
            String segment = matcher.group().trim();
            if (HAS_WORD_CONTENT.matcher(segment).matches()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of words in the text.
     *
     * @param text input string
     * @return number of words
     */
    public static int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        int count = 0;
        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Counts the number of punctuation marks in the text.
     *
     * @param text input string
     * @return number of punctuation marks
     */
    public static int countPunctuation(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int count = 0;
        Matcher matcher = PUNCTUATION_PATTERN.matcher(text);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Counts individual numeric digits (0-9).
     *
     * @param text input string
     * @return count of numeric digits
     */
    public static int countDigits(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i))) {
                count++;
            }
        }
        return count;
    }
}
