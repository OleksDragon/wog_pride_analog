package com.example.wogprideanalog.ui.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wogprideanalog.R;
import com.example.wogprideanalog.data.model.Transaction;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.itemNameTextView.setText(transaction.itemName);
        holder.quantityTextView.setText("Кількість: " + transaction.quantity);
        holder.totalCostTextView.setText("Вартість: " + transaction.totalCost + " ₴");
        holder.dateTextView.setText("Дата: " + transaction.date);
        holder.typeTextView.setText(transaction.type.equals("fuel") ? "Паливо" : "Кава");
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, quantityTextView, totalCostTextView, dateTextView, typeTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.transaction_item_name);
            quantityTextView = itemView.findViewById(R.id.transaction_quantity);
            totalCostTextView = itemView.findViewById(R.id.transaction_total_cost);
            dateTextView = itemView.findViewById(R.id.transaction_date);
            typeTextView = itemView.findViewById(R.id.transaction_type);
        }
    }
}