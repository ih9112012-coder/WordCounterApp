package com.example.textmetrics;

import androidx.lifecycle.ViewModel;

import com.example.textmetrics.history.HistoryItem;
import com.example.textmetrics.metrics.TextMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ViewModel to retain calculation history, latest results, and sample generation state
 * across configuration changes (orientation changes, theme toggles, screen resize).
 */
public class MainViewModel extends ViewModel {

    private final List<HistoryItem> historyItems = new ArrayList<>();
    private TextMetrics latestMetrics = null;
    private int sampleIndex = 0;

    public List<HistoryItem> getHistoryItems() {
        return Collections.unmodifiableList(historyItems);
    }

    public void addHistoryItem(HistoryItem item) {
        historyItems.add(0, item);
    }

    public void clearHistory() {
        historyItems.clear();
    }

    public boolean isHistoryEmpty() {
        return historyItems.isEmpty();
    }

    public TextMetrics getLatestMetrics() {
        return latestMetrics;
    }

    public void setLatestMetrics(TextMetrics latestMetrics) {
        this.latestMetrics = latestMetrics;
    }

    public int getAndIncrementSampleIndex(int totalSamples) {
        if (totalSamples <= 0) {
            return 0;
        }
        int currentIndex = sampleIndex % totalSamples;
        sampleIndex++;
        return currentIndex;
    }
}
