package com.example.textmetrics.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textmetrics.R;
import com.example.textmetrics.metrics.TextMetrics;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter to display runtime calculation history.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(HistoryItem item);
    }

    private final List<HistoryItem> items = new ArrayList<>();
    private final OnItemClickListener clickListener;

    public HistoryAdapter(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setItems(List<HistoryItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void addItem(HistoryItem item) {
        // Prepend new calculation so the latest appears at top
        items.add(0, item);
        notifyItemInserted(0);
    }

    public void clearItems() {
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = items.get(position);
        Context context = holder.itemView.getContext();
        TextMetrics metrics = item.getMetrics();

        holder.tvSummary.setText(context.getString(
                R.string.format_history_item,
                item.getInputText(),
                metrics.getSentenceCount(),
                metrics.getWordCount(),
                metrics.getPunctuationCount(),
                metrics.getDigitCount()
        ));

        holder.tvTime.setText(item.getFormattedTime());

        holder.tvSentences.setText(context.getString(R.string.format_sentences, metrics.getSentenceCount()));
        holder.tvWords.setText(context.getString(R.string.format_words, metrics.getWordCount()));
        holder.tvPunctuation.setText(context.getString(R.string.format_punctuation, metrics.getPunctuationCount()));
        holder.tvDigits.setText(context.getString(R.string.format_digits, metrics.getDigitCount()));

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        final TextView tvSummary;
        final TextView tvTime;
        final TextView tvSentences;
        final TextView tvWords;
        final TextView tvPunctuation;
        final TextView tvDigits;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSummary = itemView.findViewById(R.id.tvHistorySummary);
            tvTime = itemView.findViewById(R.id.tvHistoryTime);
            tvSentences = itemView.findViewById(R.id.tvBadgeSentences);
            tvWords = itemView.findViewById(R.id.tvBadgeWords);
            tvPunctuation = itemView.findViewById(R.id.tvBadgePunctuation);
            tvDigits = itemView.findViewById(R.id.tvBadgeDigits);
        }
    }
}
