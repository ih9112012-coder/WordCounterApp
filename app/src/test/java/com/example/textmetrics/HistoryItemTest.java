package com.example.textmetrics;

import com.example.textmetrics.history.HistoryItem;
import com.example.textmetrics.metrics.TextMetrics;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for HistoryItem model class.
 * Verifies 100% class and function coverage including getters, formatted summary,
 * timestamp, time formatting, and Serialization.
 */
public class HistoryItemTest {

    @Test
    public void testConstructorAndGetters() {
        long beforeCreation = System.currentTimeMillis();
        TextMetrics metrics = new TextMetrics(2, 8, 3, 1);
        String inputText = "Sample text for testing.";

        HistoryItem item = new HistoryItem(inputText, metrics);
        long afterCreation = System.currentTimeMillis();

        assertEquals("Input text mismatch", inputText, item.getInputText());
        assertEquals("Metrics mismatch", metrics, item.getMetrics());
        assertTrue("Timestamp should be within reasonable bounds",
                item.getTimestamp() >= beforeCreation && item.getTimestamp() <= afterCreation);
    }

    @Test
    public void testFormattedSummary() {
        TextMetrics metrics = new TextMetrics(1, 2, 0, 0);
        HistoryItem item = new HistoryItem("Good morning", metrics);

        String summary = item.getFormattedSummary();
        assertEquals("\"Good morning\" => 1, 2, 0, 0", summary);
    }

    @Test
    public void testFormattedTime() {
        TextMetrics metrics = new TextMetrics(1, 5, 1, 0);
        HistoryItem item = new HistoryItem("Hello world.", metrics);

        String formattedTime = item.getFormattedTime();
        assertNotNull("Formatted time should not be null", formattedTime);
        // Verify format matches HH:mm:ss
        assertTrue("Time should match HH:mm:ss pattern",
                Pattern.matches("^\\d{2}:\\d{2}:\\d{2}$", formattedTime));
    }

    @Test
    public void testSerialization() throws Exception {
        TextMetrics metrics = new TextMetrics(3, 12, 4, 2);
        HistoryItem original = new HistoryItem("Serialization test string 123", metrics);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(original);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        HistoryItem deserialized;
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            deserialized = (HistoryItem) ois.readObject();
        }

        assertNotNull(deserialized);
        assertEquals(original.getInputText(), deserialized.getInputText());
        assertEquals(original.getMetrics(), deserialized.getMetrics());
        assertEquals(original.getTimestamp(), deserialized.getTimestamp());
        assertEquals(original.getFormattedTime(), deserialized.getFormattedTime());
        assertEquals(original.getFormattedSummary(), deserialized.getFormattedSummary());
    }
}
