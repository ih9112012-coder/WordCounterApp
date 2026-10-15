package com.example.textmetrics.metrics;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Immutable data holder representing the calculated metrics for a given text.
 */
public final class TextMetrics implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int sentenceCount;
    private final int wordCount;
    private final int punctuationCount;
    private final int digitCount;

    public TextMetrics(int sentenceCount, int wordCount, int punctuationCount, int digitCount) {
        this.sentenceCount = sentenceCount;
        this.wordCount = wordCount;
        this.punctuationCount = punctuationCount;
        this.digitCount = digitCount;
    }

    public int getSentenceCount() {
        return sentenceCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getPunctuationCount() {
        return punctuationCount;
    }

    public int getDigitCount() {
        return digitCount;
    }

    /**
     * Formats history representation as required:
     * "[input]" => sentences, words, punctuation, digits
     */
    public String toHistoryString(String originalText) {
        return String.format(Locale.getDefault(), "\"%s\" => %d, %d, %d, %d",
                originalText, sentenceCount, wordCount, punctuationCount, digitCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextMetrics that = (TextMetrics) o;
        return sentenceCount == that.sentenceCount &&
                wordCount == that.wordCount &&
                punctuationCount == that.punctuationCount &&
                digitCount == that.digitCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sentenceCount, wordCount, punctuationCount, digitCount);
    }

    @Override
    public String toString() {
        return "TextMetrics{" +
                "sentenceCount=" + sentenceCount +
                ", wordCount=" + wordCount +
                ", punctuationCount=" + punctuationCount +
                ", digitCount=" + digitCount +
                '}';
    }
}
