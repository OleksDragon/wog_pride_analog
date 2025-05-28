package com.example.wogprideanalog.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wogprideanalog.R;
import com.example.wogprideanalog.data.DatabaseHelper;
import com.example.wogprideanalog.data.model.User;
import com.example.wogprideanalog.ui.adapters.CoffeeCategoryAdapter;
import com.example.wogprideanalog.ui.viewmodel.FuelViewModel;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class CoffeeInfoFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private TextView coffeeCountText;
    private Button getCoffeeButton;
    private FuelViewModel fuelViewModel;
    private RecyclerView coffeeCategoriesRecycler;
    private CoffeeCategoryAdapter adapter;
    private User user;
    private Map<String, Integer> coffeeCupsByCategory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = new DatabaseHelper(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coffee_info, container, false);

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        coffeeCountText = view.findViewById(R.id.coffee_count_text);
        getCoffeeButton = view.findViewById(R.id.get_coffee_button);
        coffeeCategoriesRecycler = view.findViewById(R.id.coffee_categories_recycler);
        ImageButton backButton = view.findViewById(R.id.back_button);

        fuelViewModel = new ViewModelProvider(requireActivity()).get(FuelViewModel.class);

        String email = sharedPreferences.getString("userEmail", "");
        String password = sharedPreferences.getString("userPassword", "");
        user = dbHelper.getUser(email, password);

        if (user != null) {
            coffeeCupsByCategory = new HashMap<>();
            for (Map.Entry<String, Integer> entry : user.coffeeCupsByCategory.entrySet()) {
                if (entry.getValue() > 0) {
                    coffeeCupsByCategory.put(entry.getKey(), entry.getValue());
                }
            }
            Log.d("CoffeeInfoFragment", "Filtered coffee categories: " + coffeeCupsByCategory.toString());

            coffeeCountText.setText("Загальна кількість чашок: " + user.getTotalCoffeeCups());
            adapter = new CoffeeCategoryAdapter(coffeeCupsByCategory);
            coffeeCategoriesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            coffeeCategoriesRecycler.setAdapter(adapter);
            getCoffeeButton.setEnabled(user.getTotalCoffeeCups() > 0);
        } else {
            coffeeCountText.setText("Помилка: користувач не знайдено");
            getCoffeeButton.setEnabled(false);
        }

        getCoffeeButton.setOnClickListener(v -> {
            if (user != null && user.getTotalCoffeeCups() > 0) {
                final String[] categories = coffeeCupsByCategory.keySet().toArray(new String[0]);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Виберіть категорію кави")
                        .setItems(categories, (dialog, which) -> {
                            String selectedCategory = categories[which];
                            int availableCups = coffeeCupsByCategory.get(selectedCategory);
                            if (availableCups <= 0) {
                                Toast.makeText(getContext(), "Немає кави в категорії " + selectedCategory, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            AlertDialog.Builder amountBuilder = new AlertDialog.Builder(getContext());
                            amountBuilder.setTitle("Виберіть кількість чашок");

                            final EditText input = new EditText(getContext());
                            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                            amountBuilder.setView(input);

                            amountBuilder.setPositiveButton("ОК", (amountDialog, amountWhich) -> {
                                String amountStr = input.getText().toString();
                                if (amountStr.isEmpty()) {
                                    Toast.makeText(getContext(), "Введіть кількість чашок!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                int cups;
                                try {
                                    cups = Integer.parseInt(amountStr);
                                } catch (NumberFormatException e) {
                                    Toast.makeText(getContext(), "Введіть коректну кількість!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (cups <= 0 || cups > availableCups) {
                                    Toast.makeText(getContext(), "Кількість чашок має бути від 1 до " + availableCups + "!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (user.getCoffee(selectedCategory, cups)) {
                                    dbHelper.updateUser(user);
                                    fuelViewModel.refreshData();
                                    coffeeCupsByCategory.put(selectedCategory, user.coffeeCupsByCategory.get(selectedCategory));
                                    coffeeCupsByCategory.entrySet().removeIf(entry -> entry.getValue() <= 0);
                                    adapter.notifyDataSetChanged();
                                    coffeeCountText.setText("Загальна кількість чашок: " + user.getTotalCoffeeCups());
                                    Toast.makeText(getContext(), "Кава (" + selectedCategory + ") отримана! Залишилося: " + user.coffeeCupsByCategory.get(selectedCategory) + " чашок", Toast.LENGTH_SHORT).show();
                                    getCoffeeButton.setEnabled(user.getTotalCoffeeCups() > 0);
                                } else {
                                    Toast.makeText(getContext(), "Помилка при отриманні кави", Toast.LENGTH_SHORT).show();
                                }
                            });
                            amountBuilder.setNegativeButton("Скасувати", (amountDialog, amountWhich) -> amountDialog.dismiss());
                            amountBuilder.show();
                        })
                        .setNegativeButton("Скасувати", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

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