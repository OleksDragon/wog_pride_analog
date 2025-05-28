package com.example.wogprideanalog.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wogprideanalog.R;

import java.util.Map;

public class CoffeeCategoryAdapter extends RecyclerView.Adapter<CoffeeCategoryAdapter.ViewHolder> {
    private final Map<String, Integer> coffeeCupsByCategory;

    public CoffeeCategoryAdapter(Map<String, Integer> coffeeCupsByCategory) {
        this.coffeeCupsByCategory = coffeeCupsByCategory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coffee_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = (String) coffeeCupsByCategory.keySet().toArray()[position];
        int count = coffeeCupsByCategory.get(category);
        holder.categoryText.setText(category);
        holder.countText.setText(String.valueOf(count));
    }

    @Override
    public int getItemCount() {
        return coffeeCupsByCategory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryText;
        public TextView countText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.category_text);
            countText = itemView.findViewById(R.id.count_text);
        }
    }
}