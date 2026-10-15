package com.example.textmetrics;

import com.example.textmetrics.metrics.TextMetrics;
import com.example.textmetrics.metrics.TextMetricsCalculator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for TextMetricsCalculator verifying all requirements and edge cases.
 */
public class TextMetricsCalculatorTest {

    @Test
    public void testGoodMorningExample() {
        // "Good morning" => 1 sentence, 2 words, 0 punctuation, 0 digits
        String text = "Good morning";
        TextMetrics metrics = TextMetricsCalculator.calculate(text);

        assertEquals("Sentences mismatch", 1, metrics.getSentenceCount());
        assertEquals("Words mismatch", 2, metrics.getWordCount());
        assertEquals("Punctuation mismatch", 0, metrics.getPunctuationCount());
        assertEquals("Digits mismatch", 0, metrics.getDigitCount());
        assertEquals("\"Good morning\" => 1, 2, 0, 0", metrics.toHistoryString(text));
    }

    @Test
    public void testTheWarStartedExample() {
        // "The war started on September 1. Sad." => 2 sentences, 7 words, 2 punctuation, 1 digit
        String text = "The war started on September 1. Sad.";
        TextMetrics metrics = TextMetricsCalculator.calculate(text);

        assertEquals("Sentences mismatch", 2, metrics.getSentenceCount());
        assertEquals("Words mismatch", 7, metrics.getWordCount());
        assertEquals("Punctuation mismatch", 2, metrics.getPunctuationCount());
        assertEquals("Digits mismatch", 1, metrics.getDigitCount());
        assertEquals("\"The war started on September 1. Sad.\" => 2, 7, 2, 1", metrics.toHistoryString(text));
    }

    @Test
    public void testEmptyAndNullString() {
        TextMetrics metricsEmpty = TextMetricsCalculator.calculate("");
        assertEquals(0, metricsEmpty.getSentenceCount());
        assertEquals(0, metricsEmpty.getWordCount());
        assertEquals(0, metricsEmpty.getPunctuationCount());
        assertEquals(0, metricsEmpty.getDigitCount());

        TextMetrics metricsWhitespace = TextMetricsCalculator.calculate("    \n\t  ");
        assertEquals(0, metricsWhitespace.getSentenceCount());
        assertEquals(0, metricsWhitespace.getWordCount());
        assertEquals(0, metricsWhitespace.getPunctuationCount());
        assertEquals(0, metricsWhitespace.getDigitCount());

        TextMetrics metricsNull = TextMetricsCalculator.calculate(null);
        assertEquals(0, metricsNull.getSentenceCount());
        assertEquals(0, metricsNull.getWordCount());
        assertEquals(0, metricsNull.getPunctuationCount());
        assertEquals(0, metricsNull.getDigitCount());
    }

    @Test
    public void testPunctuationOnly() {
        String text = "... ,,, !!! ??? ;;;";
        TextMetrics metrics = TextMetricsCalculator.calculate(text);

        // No words or letters/digits, so no valid sentences
        assertEquals(0, metrics.getSentenceCount());
        assertEquals(0, metrics.getWordCount());
        assertEquals(15, metrics.getPunctuationCount());
        assertEquals(0, metrics.getDigitCount());
    }

    @Test
    public void testMultipleDigits() {
        // User clarified: count individual numeric digits (e.g., 100 has 3 digits)
        String text = "Room 101 has 2 chairs.";
        TextMetrics metrics = TextMetricsCalculator.calculate(text);

        assertEquals(1, metrics.getSentenceCount());
        assertEquals(5, metrics.getWordCount()); // Room, 101, has, 2, chairs
        assertEquals(1, metrics.getPunctuationCount()); // .
        assertEquals(4, metrics.getDigitCount()); // 1, 0, 1, 2 = 4 digits
    }

    @Test
    public void testMultipleSentenceDelimiters() {
        String text = "Wait! Are you sure? Yes... absolutely.";
        TextMetrics metrics = TextMetricsCalculator.calculate(text);

        assertEquals(4, metrics.getSentenceCount());
        assertEquals(6, metrics.getWordCount());
        assertEquals(6, metrics.getPunctuationCount()); // !, ?, ..., . (1+1+3+1 = 6)
        assertEquals(0, metrics.getDigitCount());
    }

    @Test
    public void testLithuanianCharacters() {
        // "Lietuva" (1), "yra" (2), "graži" (3), "šalis" (4), "2026" (5), "metais" (6), "čia" (7), "gyvena" (8), "daug" (9), "žmonių" (10)
        String text = "Lietuva yra graži šalis. 2026 metais čia gyvena daug žmonių!";
        TextMetrics metrics = TextMetricsCalculator.calculate(text);

        assertEquals(2, metrics.getSentenceCount());
        assertEquals(10, metrics.getWordCount());
        assertEquals(2, metrics.getPunctuationCount()); // . and !
        assertEquals(4, metrics.getDigitCount()); // 2, 0, 2, 6 = 4 digits
    }

    @Test
    public void testContractionsAndHyphenatedWords() {
        String text = "It's a state-of-the-art solution.";
        TextMetrics metrics = TextMetricsCalculator.calculate(text);

        assertEquals(1, metrics.getSentenceCount());
        // "It's" (1), "a" (2), "state-of-the-art" (3), "solution" (4)
        assertEquals(4, metrics.getWordCount());
        // ' (1), - (3), . (1) => 5 punctuation marks
        assertEquals(5, metrics.getPunctuationCount());
        assertEquals(0, metrics.getDigitCount());
    }

    @Test
    public void testDecimalNumbersInSentences() {
        // Decimal periods (3.14) must not break sentence boundaries
        String text = "Pi is approximately 3.14. It is an irrational number.";
        TextMetrics metrics = TextMetricsCalculator.calculate(text);

        assertEquals(2, metrics.getSentenceCount());
        assertEquals(10, metrics.getWordCount()); // Pi(1), is(2), approximately(3), 3(4), 14(5), It(6), is(7), an(8), irrational(9), number(10)
        assertEquals(3, metrics.getPunctuationCount()); // 3 periods total
        assertEquals(3, metrics.getDigitCount()); // 3, 1, 4
    }

    @Test
    public void testUnicodeEllipsisSentenceBoundary() {
        // Unicode horizontal ellipsis (\u2026) must delimit sentences
        String text = "Wait\u2026 What happened? Everything is fine.";
        TextMetrics metrics = TextMetricsCalculator.calculate(text);

        assertEquals(3, metrics.getSentenceCount());
        assertEquals(6, metrics.getWordCount());
        assertEquals(3, metrics.getPunctuationCount()); // \u2026, ?, .
        assertEquals(0, metrics.getDigitCount());
    }
}
