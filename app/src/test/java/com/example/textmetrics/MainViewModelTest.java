package com.example.textmetrics;

import com.example.textmetrics.history.HistoryItem;
import com.example.textmetrics.metrics.TextMetrics;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for MainViewModel verifying state retention and history operations.
 */
public class MainViewModelTest {

    private MainViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new MainViewModel();
    }

    @Test
    public void testInitialStateIsEmpty() {
        assertTrue("History should initially be empty", viewModel.isHistoryEmpty());
        assertEquals("History item count should be 0", 0, viewModel.getHistoryItems().size());
        assertNull("Latest metrics should initially be null", viewModel.getLatestMetrics());
    }

    @Test
    public void testAddAndClearHistory() {
        TextMetrics metrics = new TextMetrics(1, 2, 0, 0);
        HistoryItem item1 = new HistoryItem("Good morning", metrics);
        HistoryItem item2 = new HistoryItem("Second text", metrics);

        viewModel.addHistoryItem(item1);
        assertFalse(viewModel.isHistoryEmpty());
        assertEquals(1, viewModel.getHistoryItems().size());
        assertEquals("Good morning", viewModel.getHistoryItems().get(0).getInputText());

        // Second item prepends
        viewModel.addHistoryItem(item2);
        assertEquals(2, viewModel.getHistoryItems().size());
        assertEquals("Second text", viewModel.getHistoryItems().get(0).getInputText());

        viewModel.clearHistory();
        assertTrue(viewModel.isHistoryEmpty());
        assertEquals(0, viewModel.getHistoryItems().size());
    }

    @Test
    public void testLatestMetricsRetention() {
        TextMetrics metrics = new TextMetrics(2, 7, 2, 1);
        viewModel.setLatestMetrics(metrics);

        assertNotNull(viewModel.getLatestMetrics());
        assertEquals(2, viewModel.getLatestMetrics().getSentenceCount());
        assertEquals(7, viewModel.getLatestMetrics().getWordCount());
        assertEquals(2, viewModel.getLatestMetrics().getPunctuationCount());
        assertEquals(1, viewModel.getLatestMetrics().getDigitCount());
    }

    @Test
    public void testSampleSentenceIndexCycling() {
        int totalSamples = 3;
        assertEquals(0, viewModel.getAndIncrementSampleIndex(totalSamples));
        assertEquals(1, viewModel.getAndIncrementSampleIndex(totalSamples));
        assertEquals(2, viewModel.getAndIncrementSampleIndex(totalSamples));
        // Cycles back to 0
        assertEquals(0, viewModel.getAndIncrementSampleIndex(totalSamples));
        assertEquals(1, viewModel.getAndIncrementSampleIndex(totalSamples));
    }
}
