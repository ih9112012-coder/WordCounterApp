package com.example.textmetrics.history;

import com.example.textmetrics.metrics.TextMetrics;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Represents a single calculation history entry recorded during the current app runtime.
 */
public class HistoryItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String inputText;
    private final TextMetrics metrics;
    private final long timestamp;
    private final String formattedTime;

    public HistoryItem(String inputText, TextMetrics metrics) {
        this.inputText = inputText;
        this.metrics = metrics;
        this.timestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        this.formattedTime = sdf.format(new Date(this.timestamp));
    }

    public String getInputText() {
        return inputText;
    }

    public TextMetrics getMetrics() {
        return metrics;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Formats history string as specified in requirements:
     * "Good morning" => 1, 2, 0, 0
     */
    public String getFormattedSummary() {
        return metrics.toHistoryString(inputText);
    }

    public String getFormattedTime() {
        return formattedTime;
    }
}
