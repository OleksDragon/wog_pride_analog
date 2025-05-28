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
import com.example.wogprideanalog.ui.adapters.FuelCategoryAdapter;
import com.example.wogprideanalog.ui.viewmodel.FuelViewModel;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class FuelInfoFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private TextView fuelCountText;
    private Button getFuelButton;
    private FuelViewModel fuelViewModel;
    private RecyclerView fuelCategoriesRecycler;
    private FuelCategoryAdapter adapter;
    private User user;
    private Map<String, Integer> fuelLitersByCategory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = new DatabaseHelper(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fuel_info, container, false);

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        fuelCountText = view.findViewById(R.id.fuel_count_text);
        getFuelButton = view.findViewById(R.id.get_fuel_button);
        fuelCategoriesRecycler = view.findViewById(R.id.fuel_categories_recycler);
        ImageButton backButton = view.findViewById(R.id.back_button);

        fuelViewModel = new ViewModelProvider(requireActivity()).get(FuelViewModel.class);

        String email = sharedPreferences.getString("userEmail", "");
        String password = sharedPreferences.getString("userPassword", "");
        user = dbHelper.getUser(email, password);

        if (user != null) {
            fuelLitersByCategory = new HashMap<>();
            for (Map.Entry<String, Integer> entry : user.fuelLitersByCategory.entrySet()) {
                if (entry.getValue() > 0) {
                    fuelLitersByCategory.put(entry.getKey(), entry.getValue());
                }
            }
            Log.d("FuelInfoFragment", "Filtered fuel categories: " + fuelLitersByCategory.toString());

            fuelCountText.setText("Загальна кількість літрів: " + user.getTotalFuelLiters());
            fuelCategoriesRecycler.setMinimumHeight(100);
            adapter = new FuelCategoryAdapter(fuelLitersByCategory);
            fuelCategoriesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            fuelCategoriesRecycler.setAdapter(adapter);
            getFuelButton.setEnabled(user.getTotalFuelLiters() > 0);
        } else {
            fuelCountText.setText("Помилка: користувач не знайдено");
            getFuelButton.setEnabled(false);
        }

        getFuelButton.setOnClickListener(v -> {
            if (user != null && user.getTotalFuelLiters() > 0) {
                final String[] categories = fuelLitersByCategory.keySet().toArray(new String[0]);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Виберіть категорію палива")
                        .setItems(categories, (dialog, which) -> {
                            String selectedCategory = categories[which];
                            int availableLiters = fuelLitersByCategory.get(selectedCategory);
                            if (availableLiters <= 0) {
                                Toast.makeText(getContext(), "Немає палива в категорії " + selectedCategory, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // Діалог для введення кількості літрів
                            AlertDialog.Builder amountBuilder = new AlertDialog.Builder(getContext());
                            amountBuilder.setTitle("Введіть кількість літрів");

                            final EditText input = new EditText(getContext());
                            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                            amountBuilder.setView(input);

                            amountBuilder.setPositiveButton("ОК", (amountDialog, amountWhich) -> {
                                String amountStr = input.getText().toString();
                                if (amountStr.isEmpty()) {
                                    Toast.makeText(getContext(), "Введіть кількість літрів!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                int liters = Integer.parseInt(amountStr);
                                if (liters <= 0 || liters > availableLiters) {
                                    Toast.makeText(getContext(), "Кількість літрів має бути від 1 до " + availableLiters + "!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (user.getFuel(selectedCategory, liters)) {
                                    dbHelper.updateUser(user);
                                    fuelViewModel.refreshData();
                                    fuelLitersByCategory.put(selectedCategory, user.fuelLitersByCategory.get(selectedCategory));
                                    fuelLitersByCategory.entrySet().removeIf(entry -> entry.getValue() <= 0);
                                    adapter.notifyDataSetChanged();
                                    fuelCountText.setText("Загальна кількість літрів: " + user.getTotalFuelLiters());
                                    Toast.makeText(getContext(), "Паливо (" + selectedCategory + ") отримано! Залишилося: " + user.fuelLitersByCategory.get(selectedCategory) + " л", Toast.LENGTH_SHORT).show();
                                    getFuelButton.setEnabled(user.getTotalFuelLiters() > 0);
                                } else {
                                    Toast.makeText(getContext(), "Помилка при отриманні палива", Toast.LENGTH_SHORT).show();
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