package com.example.wogprideanalog.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wogprideanalog.R;
import com.example.wogprideanalog.data.DatabaseHelper;
import com.example.wogprideanalog.data.model.Transaction;
import com.example.wogprideanalog.ui.adapters.TransactionAdapter;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TransactionHistoryFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private RecyclerView transactionsRecyclerView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = new DatabaseHelper(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        transactionsRecyclerView = view.findViewById(R.id.transactions_recycler_view);
        ImageButton backButton = view.findViewById(R.id.back_button);

        String email = sharedPreferences.getString("userEmail", "");
        List<Transaction> transactions = dbHelper.getUserTransactions(email);

        TransactionAdapter adapter = new TransactionAdapter(transactions);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionsRecyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}