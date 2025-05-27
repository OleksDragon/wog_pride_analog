package com.example.wogprideanalog.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.wogprideanalog.R;
import com.example.wogprideanalog.data.DatabaseHelper;
import com.example.wogprideanalog.data.StoreConfig;
import com.example.wogprideanalog.data.model.User;
import com.example.wogprideanalog.ui.viewmodel.FuelViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class StoreFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private FuelViewModel fuelViewModel;
    private Spinner fuelTypeSpinner, coffeeTypeSpinner;
    private EditText fuelQuantityInput, coffeeQuantityInput;
    private TextView fuelPriceDisplay, coffeePriceDisplay, fuelListDisplay, coffeeListDisplay;
    private Button buyFuelButton, buyCoffeeButton;
    private ArrayAdapter<String> fuelAdapter, coffeeAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = new DatabaseHelper(context);
        StoreConfig.initialize(dbHelper);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        fuelViewModel = new ViewModelProvider(requireActivity()).get(FuelViewModel.class);

        fuelTypeSpinner = view.findViewById(R.id.fuel_type_spinner);
        coffeeTypeSpinner = view.findViewById(R.id.coffee_type_spinner);
        fuelQuantityInput = view.findViewById(R.id.fuel_quantity_input);
        coffeeQuantityInput = view.findViewById(R.id.coffee_quantity_input);
        buyFuelButton = view.findViewById(R.id.buy_fuel_button);
        buyCoffeeButton = view.findViewById(R.id.buy_coffee_button);
        fuelPriceDisplay = view.findViewById(R.id.fuel_price_display);
        coffeePriceDisplay = view.findViewById(R.id.coffee_price_display);
        fuelListDisplay = view.findViewById(R.id.fuel_list_display);
        coffeeListDisplay = view.findViewById(R.id.coffee_list_display);

        setupFuelSection();
        setupCoffeeSection();

        buyFuelButton.setOnClickListener(v -> buyFuel());
        buyCoffeeButton.setOnClickListener(v -> buyCoffee());

        return view;
    }

    private void setupFuelSection() {
        // Формуємо список палива для відображення
        Map<String, StoreConfig.Item> fuelItems = StoreConfig.getFuelItems();
        StringBuilder fuelListText = new StringBuilder();
        for (Map.Entry<String, StoreConfig.Item> entry : fuelItems.entrySet()) {
            fuelListText.append(entry.getKey()).append(": ").append(entry.getValue().getPrice()).append(" ₴/л\n");
        }
        fuelListDisplay.setText(fuelListText.toString().trim());

        // Налаштування Spinner для палива
        ArrayList<String> fuelTypes = new ArrayList<>(fuelItems.keySet());
        fuelAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, fuelTypes);
        fuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fuelTypeSpinner.setAdapter(fuelAdapter);

        fuelTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFuel = (String) parent.getItemAtPosition(position);
                StoreConfig.Item item = StoreConfig.getFuelItems().get(selectedFuel);
                fuelPriceDisplay.setText("Ціна: " + item.getPrice() + " ₴/л");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fuelPriceDisplay.setText("Ціна: - ₴/л");
            }
        });
    }

    private void setupCoffeeSection() {
        // Формуємо список кави для відображення
        Map<String, StoreConfig.Item> coffeeItems = StoreConfig.getCoffeeItems();
        StringBuilder coffeeListText = new StringBuilder();
        for (Map.Entry<String, StoreConfig.Item> entry : coffeeItems.entrySet()) {
            coffeeListText.append(entry.getKey()).append(": ").append(entry.getValue().getPrice()).append(" ₴\n");
        }
        coffeeListDisplay.setText(coffeeListText.toString().trim());

        // Налаштування Spinner для кави
        ArrayList<String> coffeeTypes = new ArrayList<>(coffeeItems.keySet());
        coffeeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, coffeeTypes);
        coffeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coffeeTypeSpinner.setAdapter(coffeeAdapter);

        coffeeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCoffee = (String) parent.getItemAtPosition(position);
                StoreConfig.Item item = StoreConfig.getCoffeeItems().get(selectedCoffee);
                coffeePriceDisplay.setText("Ціна: " + item.getPrice() + " ₴");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                coffeePriceDisplay.setText("Ціна: - ₴");
            }
        });
    }

    private void buyFuel() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            Toast.makeText(getContext(), "Будь ласка, увійдіть, щоб зробити покупку", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = sharedPreferences.getString("userEmail", "");
        String password = sharedPreferences.getString("userPassword", "");
        User user = dbHelper.getUser(email, password);
        if (user == null) {
            Toast.makeText(getContext(), "Помилка: користувача не знайдено", Toast.LENGTH_SHORT).show();
            return;
        }

        String fuelType = fuelTypeSpinner.getSelectedItem().toString();
        int quantity;
        try {
            quantity = Integer.parseInt(fuelQuantityInput.getText().toString());
            if (quantity <= 0) {
                Toast.makeText(getContext(), "Введіть коректну кількість", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Введіть коректну кількість", Toast.LENGTH_SHORT).show();
            return;
        }

        StoreConfig.Item item = StoreConfig.getFuelItems().get(fuelType);
        int totalCost = item.getPrice() * quantity;

        if (user.accountBalance < totalCost) {
            Toast.makeText(getContext(), "Недостатньо коштів на балансі", Toast.LENGTH_SHORT).show();
            return;
        }

        user.accountBalance -= totalCost;
        user.fuelAmount += quantity;

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        dbHelper.addTransaction(email, "fuel", fuelType, quantity, totalCost, date);

        updateUserInDatabase(user);
        fuelViewModel.refreshData();
        Toast.makeText(getContext(), "Куплено " + quantity + " л " + fuelType + " за " + totalCost + " ₴", Toast.LENGTH_SHORT).show();
    }

    private void buyCoffee() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            Toast.makeText(getContext(), "Будь ласка, увійдіть, щоб зробити покупку", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = sharedPreferences.getString("userEmail", "");
        String password = sharedPreferences.getString("userPassword", "");
        User user = dbHelper.getUser(email, password);
        if (user == null) {
            Toast.makeText(getContext(), "Помилка: користувача не знайдено", Toast.LENGTH_SHORT).show();
            return;
        }

        String coffeeType = coffeeTypeSpinner.getSelectedItem().toString();
        int quantity;
        try {
            quantity = Integer.parseInt(coffeeQuantityInput.getText().toString());
            if (quantity <= 0) {
                Toast.makeText(getContext(), "Введіть коректну кількість", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Введіть коректну кількість", Toast.LENGTH_SHORT).show();
            return;
        }

        StoreConfig.Item item = StoreConfig.getCoffeeItems().get(coffeeType);
        int totalCost = item.getPrice() * quantity;

        if (user.accountBalance < totalCost) {
            Toast.makeText(getContext(), "Недостатньо коштів на балансі", Toast.LENGTH_SHORT).show();
            return;
        }

        user.accountBalance -= totalCost;
        user.coffeeCups += quantity;

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        dbHelper.addTransaction(email, "coffee", coffeeType, quantity, totalCost, date);

        updateUserInDatabase(user);
        fuelViewModel.refreshData();
        Toast.makeText(getContext(), "Куплено " + quantity + " чашок " + coffeeType + " за " + totalCost + " ₴", Toast.LENGTH_SHORT).show();
    }

    private void updateUserInDatabase(User user) {
        dbHelper.updateUser(user);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}