package com.example.textmetrics;

import com.example.textmetrics.metrics.TextMetrics;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for TextMetrics model class.
 * Verifies 100% class and function coverage including getters, equals, hashCode,
 * toString, toHistoryString, and Serialization.
 */
public class TextMetricsTest {

    @Test
    public void testConstructorAndGetters() {
        TextMetrics metrics = new TextMetrics(3, 15, 5, 4);

        assertEquals("Sentence count mismatch", 3, metrics.getSentenceCount());
        assertEquals("Word count mismatch", 15, metrics.getWordCount());
        assertEquals("Punctuation count mismatch", 5, metrics.getPunctuationCount());
        assertEquals("Digit count mismatch", 4, metrics.getDigitCount());
    }

    @Test
    public void testToHistoryString() {
        TextMetrics metrics = new TextMetrics(1, 2, 0, 0);
        String historyString = metrics.toHistoryString("Good morning");

        assertEquals("\"Good morning\" => 1, 2, 0, 0", historyString);
    }

    @Test
    public void testEqualsSameInstance() {
        TextMetrics metrics = new TextMetrics(2, 5, 1, 0);
        assertEquals(metrics, metrics);
        assertTrue(metrics.equals(metrics));
    }

    @Test
    public void testEqualsNullAndDifferentType() {
        TextMetrics metrics = new TextMetrics(2, 5, 1, 0);

        assertNotNull(metrics);
        assertFalse(metrics.equals(null));
        assertFalse(metrics.equals("Not a TextMetrics instance"));
        assertFalse(metrics.equals(Integer.valueOf(42)));
    }

    @Test
    public void testEqualsAndHashCodeIdenticalValues() {
        TextMetrics m1 = new TextMetrics(2, 7, 2, 1);
        TextMetrics m2 = new TextMetrics(2, 7, 2, 1);

        assertEquals(m1, m2);
        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));
        assertEquals("Hash codes must match for equal objects", m1.hashCode(), m2.hashCode());
    }

    @Test
    public void testEqualsDifferentFields() {
        TextMetrics base = new TextMetrics(2, 7, 2, 1);

        TextMetrics diffSentence = new TextMetrics(3, 7, 2, 1);
        TextMetrics diffWord = new TextMetrics(2, 8, 2, 1);
        TextMetrics diffPunct = new TextMetrics(2, 7, 3, 1);
        TextMetrics diffDigit = new TextMetrics(2, 7, 2, 2);

        assertNotEquals(base, diffSentence);
        assertNotEquals(base, diffWord);
        assertNotEquals(base, diffPunct);
        assertNotEquals(base, diffDigit);

        assertFalse(base.equals(diffSentence));
        assertFalse(base.equals(diffWord));
        assertFalse(base.equals(diffPunct));
        assertFalse(base.equals(diffDigit));
    }

    @Test
    public void testToString() {
        TextMetrics metrics = new TextMetrics(1, 4, 2, 3);
        String str = metrics.toString();

        assertNotNull(str);
        assertTrue(str.contains("sentenceCount=1"));
        assertTrue(str.contains("wordCount=4"));
        assertTrue(str.contains("punctuationCount=2"));
        assertTrue(str.contains("digitCount=3"));
    }

    @Test
    public void testSerialization() throws Exception {
        TextMetrics original = new TextMetrics(4, 20, 6, 8);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(original);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        TextMetrics deserialized;
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            deserialized = (TextMetrics) ois.readObject();
        }

        assertNotNull(deserialized);
        assertEquals(original, deserialized);
        assertEquals(original.getSentenceCount(), deserialized.getSentenceCount());
        assertEquals(original.getWordCount(), deserialized.getWordCount());
        assertEquals(original.getPunctuationCount(), deserialized.getPunctuationCount());
        assertEquals(original.getDigitCount(), deserialized.getDigitCount());
    }
}
