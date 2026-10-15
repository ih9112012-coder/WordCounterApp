package com.example.textmetrics;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.textmetrics.databinding.ActivityMainBinding;
import com.example.textmetrics.history.HistoryAdapter;
import com.example.textmetrics.history.HistoryItem;
import com.example.textmetrics.metrics.TextMetrics;
import com.example.textmetrics.metrics.TextMetricsCalculator;

/**
 * Main Activity for Text Metrics Analyzer.
 * Manages user interactions, validation, calculations, test generation, and runtime history.
 * Uses ViewBinding and ViewModel for lifecycle-aware state persistence across configuration changes.
 */
public class MainActivity extends AppCompatActivity implements HistoryAdapter.OnItemClickListener {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private HistoryAdapter historyAdapter;
    private String[] sampleSentences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        sampleSentences = getResources().getStringArray(R.array.sample_sentences);

        setupHistoryRecyclerView();
        restoreLatestMetricsDisplay();
        setupListeners();
    }

    private void setupHistoryRecyclerView() {
        historyAdapter = new HistoryAdapter(this);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(historyAdapter);

        // Restore runtime history preserved across configuration changes
        historyAdapter.setItems(viewModel.getHistoryItems());
        updateHistoryVisibility();
    }

    private void restoreLatestMetricsDisplay() {
        TextMetrics metrics = viewModel.getLatestMetrics();
        if (metrics != null) {
            binding.tvSentenceCount.setText(String.valueOf(metrics.getSentenceCount()));
            binding.tvWordCount.setText(String.valueOf(metrics.getWordCount()));
            binding.tvPunctuationCount.setText(String.valueOf(metrics.getPunctuationCount()));
            binding.tvDigitCount.setText(String.valueOf(metrics.getDigitCount()));
        }
    }

    private void setupListeners() {
        // Calculate Button
        binding.btnCalculate.setOnClickListener(v -> performCalculation());

        // Generate Test Sentence Button
        binding.btnGenerateTest.setOnClickListener(v -> generateTestSentence());

        // Clear Input Button
        binding.btnClearInput.setOnClickListener(v -> {
            if (binding.etInputText.getText() != null) {
                binding.etInputText.getText().clear();
            }
            binding.tilInputText.setError(null);
        });

        // Clear History Button
        binding.btnClearHistory.setOnClickListener(v -> {
            viewModel.clearHistory();
            historyAdapter.clearItems();
            updateHistoryVisibility();
            Toast.makeText(this, R.string.history_cleared_toast, Toast.LENGTH_SHORT).show();
        });

        // Clear error as user types
        binding.etInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.tilInputText.getError() != null) {
                    binding.tilInputText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    /**
     * Validates input and calculates metrics.
     * Notifies user if input is empty via inline error and Toast.
     */
    private void performCalculation() {
        String input = binding.etInputText.getText() != null ? binding.etInputText.getText().toString() : "";

        // Requirement 4: The application must check whether the input field is empty.
        if (input.trim().isEmpty()) {
            String errorMsg = getString(R.string.error_empty_input);
            binding.tilInputText.setError(errorMsg);
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear any previous error
        binding.tilInputText.setError(null);

        // Requirement 1 & 5: Calculate metrics using separate module/class
        TextMetrics metrics = TextMetricsCalculator.calculate(input);
        viewModel.setLatestMetrics(metrics);

        // Update current results display
        binding.tvSentenceCount.setText(String.valueOf(metrics.getSentenceCount()));
        binding.tvWordCount.setText(String.valueOf(metrics.getWordCount()));
        binding.tvPunctuationCount.setText(String.valueOf(metrics.getPunctuationCount()));
        binding.tvDigitCount.setText(String.valueOf(metrics.getDigitCount()));

        // Requirement 3: Add to runtime history and persist in ViewModel
        HistoryItem historyItem = new HistoryItem(input, metrics);
        viewModel.addHistoryItem(historyItem);
        historyAdapter.addItem(historyItem);
        binding.rvHistory.scrollToPosition(0);
        updateHistoryVisibility();
    }

    /**
     * Requirement 2: The application must provide an option to generate test sentences.
     * Cycles through curated sample sentences with index preserved across configuration changes.
     */
    private void generateTestSentence() {
        if (sampleSentences == null || sampleSentences.length == 0) {
            return;
        }

        int sampleIndex = viewModel.getAndIncrementSampleIndex(sampleSentences.length);
        String sample = sampleSentences[sampleIndex];

        binding.etInputText.setText(sample);
        binding.etInputText.setSelection(sample.length());
        binding.tilInputText.setError(null);

        Toast.makeText(this, R.string.sample_loaded_toast, Toast.LENGTH_SHORT).show();
    }

    private void updateHistoryVisibility() {
        boolean hasItems = !viewModel.isHistoryEmpty();
        binding.tvHistoryEmpty.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        binding.rvHistory.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        binding.btnClearHistory.setVisibility(hasItems ? View.VISIBLE : View.GONE);
    }

    /**
     * Callback when a history item is tapped: loads its text into the input field.
     */
    @Override
    public void onItemClick(HistoryItem item) {
        binding.etInputText.setText(item.getInputText());
        binding.etInputText.setSelection(item.getInputText().length());
        binding.tilInputText.setError(null);
    }
}
