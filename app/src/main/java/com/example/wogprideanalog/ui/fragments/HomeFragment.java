package com.example.wogprideanalog.ui.fragments;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.wogprideanalog.R;
import com.example.wogprideanalog.data.DatabaseHelper;
import com.example.wogprideanalog.data.StoreConfig;
import com.example.wogprideanalog.data.model.User;
import com.example.wogprideanalog.ui.viewmodel.FuelViewModel;
import com.example.wogprideanalog.utils.QrCodeGenerator;

import static android.content.Context.MODE_PRIVATE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FuelViewModel fuelViewModel;
    private ImageView qrCodeImageView;
    private TextView balanceTextView, fuelIndicator, coffeeIndicator;
    private SharedPreferences sharedPreferences;
    private ViewPager2 viewPager;
    private LinearLayout fuelPricesButton;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        fuelViewModel = new ViewModelProvider(requireActivity()).get(FuelViewModel.class);
        fuelViewModel.setContext(requireContext());
        fuelViewModel.refreshData();

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);

        balanceTextView = view.findViewById(R.id.balance_text_view);
        fuelIndicator = view.findViewById(R.id.fuel_indicator);
        coffeeIndicator = view.findViewById(R.id.coffee_indicator);
        qrCodeImageView = view.findViewById(R.id.qr_code_image_view);
        ImageButton walletButton = view.findViewById(R.id.wallet_button);
        ImageButton fuelButton = view.findViewById(R.id.fuel_button);
        ImageButton coffeeButton = view.findViewById(R.id.coffee_button);
        fuelPricesButton = view.findViewById(R.id.fuel_prices_button);
        LinearLayout calculatorButton = view.findViewById(R.id.calculator_button);
        LinearLayout addFuelButton = view.findViewById(R.id.add_fuel_button);
        LinearLayout addCoffeeButton = view.findViewById(R.id.add_coffee_button);

        viewPager = requireActivity().findViewById(R.id.view_pager);

        if (balanceTextView == null || fuelIndicator == null || coffeeIndicator == null || qrCodeImageView == null) {
            throw new IllegalStateException("One or more views are null");
        }

        fuelViewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            balanceTextView.setText(balance != null ? balance + " ₴" : "0 ₴");
        });

        fuelViewModel.getFuelAmount().observe(getViewLifecycleOwner(), fuel -> {
            fuelIndicator.setText(fuel != null ? fuel + " л" : "0 л");
        });

        fuelViewModel.getCoffeeAmount().observe(getViewLifecycleOwner(), coffee -> {
            coffeeIndicator.setText(coffee != null ? coffee + " чашок" : "0 чашок");
        });

        String cardId = sharedPreferences.getString("userEmail", "USER123");
        Bitmap qrCode = QrCodeGenerator.generateQrCode(cardId);
        if (qrCode != null) {
            qrCodeImageView.setImageBitmap(qrCode);
            qrCodeImageView.setOnClickListener(v -> {
                QrInfoDialogFragment dialog = QrInfoDialogFragment.newInstance(qrCode);
                dialog.show(getParentFragmentManager(), "qr_info_dialog");
            });
        } else {
            qrCodeImageView.setImageResource(android.R.drawable.ic_dialog_alert);
        }

        walletButton.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            if (!isLoggedIn) {
                Toast.makeText(getContext(), "Будь ласка, увійдіть, щоб переглянути історію транзакцій", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("HomeFragment", "Открываем TransactionHistoryFragment");
            TransactionHistoryFragment transactionHistoryFragment = new TransactionHistoryFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.overlay_fragment_container, transactionHistoryFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        coffeeButton.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            if (!isLoggedIn) {
                Toast.makeText(getContext(), "Будь ласка, увійдіть, щоб переглянути інформацію про каву", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("HomeFragment", "Відкриваємо CoffeeInfoFragment");
            CoffeeInfoFragment coffeeInfoFragment = new CoffeeInfoFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.overlay_fragment_container, coffeeInfoFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        fuelButton.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            if (!isLoggedIn) {
                Toast.makeText(getContext(), "Будь ласка, увійдіть, щоб переглянути інформацію про паливо", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("HomeFragment", "Відкриваємо FuelInfoFragment");
            FuelInfoFragment fuelInfoFragment = new FuelInfoFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.overlay_fragment_container, fuelInfoFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        fuelPricesButton.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            if (!isLoggedIn) {
                Toast.makeText(getContext(), "Будь ласка, увійдіть, щоб переглянути ціни на паливо", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, StoreConfig.Item> fuelItems = dbHelper.getFuelItems();
            ArrayList<String> fuelPricesList = new ArrayList<>();
            for (Map.Entry<String, StoreConfig.Item> entry : fuelItems.entrySet()) {
                fuelPricesList.add(entry.getKey() + ": " + entry.getValue().getPrice() + " ₴/л");
            }
            String[] fuelPrices = fuelPricesList.toArray(new String[0]);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Ціни на паливо");
            builder.setItems(fuelPrices, (dialog, which) -> {
                Toast.makeText(getContext(), "Ви обрали: " + fuelPrices[which], Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Закрити", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        calculatorButton.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            if (!isLoggedIn) {
                Toast.makeText(getContext(), "Будь ласка, увійдіть, щоб використовувати калькулятор", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, StoreConfig.Item> fuelItems = dbHelper.getFuelItems();
            final String[] fuelTypes = fuelItems.keySet().toArray(new String[0]);

            View calculatorView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_calculator, null);
            EditText mileageInput = calculatorView.findViewById(R.id.mileage_input);
            EditText fuelConsumptionInput = calculatorView.findViewById(R.id.fuel_consumption_input);
            Spinner fuelTypeSpinner = calculatorView.findViewById(R.id.fuel_type_spinner);
            CheckBox roundTripCheckbox = calculatorView.findViewById(R.id.round_trip_checkbox);
            TextView resultText = calculatorView.findViewById(R.id.result_text);
            Button calculateButton = calculatorView.findViewById(R.id.calculate_button);
            Button closeButton = calculatorView.findViewById(R.id.close_button);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, fuelTypes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fuelTypeSpinner.setAdapter(adapter);

            calculateButton.setOnClickListener(v1 -> {
                String mileageStr = mileageInput.getText().toString().trim();
                String consumptionStr = fuelConsumptionInput.getText().toString().trim();
                if (mileageStr.isEmpty() || consumptionStr.isEmpty()) {
                    Toast.makeText(getContext(), "Будь ласка, введіть кілометраж і витрату!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int mileage, consumption;
                try {
                    mileage = Integer.parseInt(mileageStr);
                    consumption = Integer.parseInt(consumptionStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Введіть коректні числа!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mileage <= 0 || consumption <= 0) {
                    Toast.makeText(getContext(), "Кілометраж і витрата повинні бути більше 0!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String selectedFuelType = fuelTypeSpinner.getSelectedItem().toString();
                int fuelPrice = fuelItems.get(selectedFuelType).getPrice();
                boolean roundTrip = roundTripCheckbox.isChecked();

                double fuelNeeded = (mileage * consumption) / 100.0;
                if (roundTrip) {
                    fuelNeeded *= 2;
                }
                double totalCost = fuelNeeded * fuelPrice;

                String result = String.format("Необхідно палива: %.2f л\nВартість поїздки: %.2f ₴", fuelNeeded, totalCost);
                resultText.setText(result);
                resultText.setVisibility(View.VISIBLE);
            });

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(calculatorView)
                    .create();

            closeButton.setOnClickListener(v1 -> dialog.dismiss());

            dialog.show();
        });

        addFuelButton.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            if (!isLoggedIn) {
                Toast.makeText(getContext(), "Будь ласка, увійдіть, щоб придбати паливо", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = sharedPreferences.getString("userEmail", "");
            String password = sharedPreferences.getString("userPassword", "");
            User user = dbHelper.getUser(email, password);
            if (user == null) {
                Toast.makeText(getContext(), "Помилка: користувач не знайдено", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, StoreConfig.Item> fuelItems = dbHelper.getFuelItems();
            final String[] fuelTypes = fuelItems.keySet().toArray(new String[0]);

            View purchaseView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fuel_purchase, null);
            Spinner fuelTypeSpinner = purchaseView.findViewById(R.id.fuel_type_spinner);
            EditText quantityInput = purchaseView.findViewById(R.id.quantity_input);
            TextView priceText = purchaseView.findViewById(R.id.price_text);
            Button purchaseButton = purchaseView.findViewById(R.id.purchase_button);
            Button closeButton = purchaseView.findViewById(R.id.close_button);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, fuelTypes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fuelTypeSpinner.setAdapter(adapter);

            fuelTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedFuelType = fuelTypes[position];
                    int pricePerLiter = fuelItems.get(selectedFuelType).getPrice();
                    String quantityStr = quantityInput.getText().toString().trim();
                    if (!quantityStr.isEmpty()) {
                        try {
                            int quantity = Integer.parseInt(quantityStr);
                            int totalPrice = quantity * pricePerLiter;
                            priceText.setText(String.format("Вартість: %d ₴", totalPrice));
                        } catch (NumberFormatException e) {
                            priceText.setText("Введіть коректну кількість!");
                        }
                    } else {
                        priceText.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    priceText.setText("");
                }
            });

            quantityInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String selectedFuelType = fuelTypeSpinner.getSelectedItem().toString();
                    int pricePerLiter = fuelItems.get(selectedFuelType).getPrice();
                    String quantityStr = s.toString().trim();
                    if (!quantityStr.isEmpty()) {
                        try {
                            int quantity = Integer.parseInt(quantityStr);
                            int totalPrice = quantity * pricePerLiter;
                            priceText.setText(String.format("Вартість: %d ₴", totalPrice));
                        } catch (NumberFormatException e) {
                            priceText.setText("Введіть коректну кількість!");
                        }
                    } else {
                        priceText.setText("");
                    }
                }
            });

            purchaseButton.setOnClickListener(v1 -> {
                String selectedFuelType = fuelTypeSpinner.getSelectedItem().toString();
                String quantityStr = quantityInput.getText().toString().trim();
                if (quantityStr.isEmpty()) {
                    Toast.makeText(getContext(), "Введіть кількість літрів!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Введіть коректну кількість!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (quantity <= 0) {
                    Toast.makeText(getContext(), "Кількість повинна бути більше 0!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int pricePerLiter = fuelItems.get(selectedFuelType).getPrice();
                int totalCost = quantity * pricePerLiter;

                if (user.accountBalance < totalCost) {
                    Toast.makeText(getContext(), "Недостатньо коштів на балансі!", Toast.LENGTH_SHORT).show();
                    return;
                }

                user.accountBalance -= totalCost;
                user.addFuel(selectedFuelType, quantity);
                dbHelper.updateUser(user);
                fuelViewModel.refreshData();

                dbHelper.addTransaction(user.email, "purchase", selectedFuelType, quantity, totalCost, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                Toast.makeText(getContext(), String.format("Придбано %d л %s за %d ₴", quantity, selectedFuelType, totalCost), Toast.LENGTH_SHORT).show();
                ((AlertDialog) v1.getTag()).dismiss();
            });

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(purchaseView)
                    .create();

            purchaseButton.setTag(dialog);

            closeButton.setOnClickListener(v1 -> dialog.dismiss());

            dialog.show();
        });

        addCoffeeButton.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            if (!isLoggedIn) {
                Toast.makeText(getContext(), "Будь ласка, увійдіть, щоб придбати каву", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = sharedPreferences.getString("userEmail", "");
            String password = sharedPreferences.getString("userPassword", "");
            User user = dbHelper.getUser(email, password);
            if (user == null) {
                Toast.makeText(getContext(), "Помилка: користувач не знайдено", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, StoreConfig.Item> coffeeItems = dbHelper.getCoffeeItems();
            final String[] coffeeTypes = coffeeItems.keySet().toArray(new String[0]);

            View purchaseView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_coffee_purchase, null);
            Spinner coffeeTypeSpinner = purchaseView.findViewById(R.id.coffee_type_spinner);
            EditText quantityInput = purchaseView.findViewById(R.id.quantity_input);
            TextView priceText = purchaseView.findViewById(R.id.price_text);
            Button purchaseButton = purchaseView.findViewById(R.id.purchase_button);
            Button closeButton = purchaseView.findViewById(R.id.close_button);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, coffeeTypes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            coffeeTypeSpinner.setAdapter(adapter);

            coffeeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCoffeeType = coffeeTypes[position];
                    int pricePerCup = coffeeItems.get(selectedCoffeeType).getPrice();
                    String quantityStr = quantityInput.getText().toString().trim();
                    if (!quantityStr.isEmpty()) {
                        try {
                            int quantity = Integer.parseInt(quantityStr);
                            int totalPrice = quantity * pricePerCup;
                            priceText.setText(String.format("Вартість: %d ₴", totalPrice));
                        } catch (NumberFormatException e) {
                            priceText.setText("Введіть коректну кількість!");
                        }
                    } else {
                        priceText.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    priceText.setText("");
                }
            });

            quantityInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String selectedCoffeeType = coffeeTypeSpinner.getSelectedItem().toString();
                    int pricePerCup = coffeeItems.get(selectedCoffeeType).getPrice();
                    String quantityStr = s.toString().trim();
                    if (!quantityStr.isEmpty()) {
                        try {
                            int quantity = Integer.parseInt(quantityStr);
                            int totalPrice = quantity * pricePerCup;
                            priceText.setText(String.format("Вартість: %d ₴", totalPrice));
                        } catch (NumberFormatException e) {
                            priceText.setText("Введіть коректну кількість!");
                        }
                    } else {
                        priceText.setText("");
                    }
                }
            });

            purchaseButton.setOnClickListener(v1 -> {
                String selectedCoffeeType = coffeeTypeSpinner.getSelectedItem().toString();
                String quantityStr = quantityInput.getText().toString().trim();
                if (quantityStr.isEmpty()) {
                    Toast.makeText(getContext(), "Введіть кількість чашок!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Введіть коректну кількість!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (quantity <= 0) {
                    Toast.makeText(getContext(), "Кількість повинна бути більше 0!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int pricePerCup = coffeeItems.get(selectedCoffeeType).getPrice();
                int totalCost = quantity * pricePerCup;

                if (user.accountBalance < totalCost) {
                    Toast.makeText(getContext(), "Недостатньо коштів на балансі!", Toast.LENGTH_SHORT).show();
                    return;
                }

                user.accountBalance -= totalCost;
                user.addCoffee(selectedCoffeeType, quantity);
                dbHelper.updateUser(user);
                fuelViewModel.refreshData();

                dbHelper.addTransaction(user.email, "purchase", selectedCoffeeType, quantity, totalCost, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                Toast.makeText(getContext(), String.format("Придбано %d чашок %s за %d ₴", quantity, selectedCoffeeType, totalCost), Toast.LENGTH_SHORT).show();
                ((AlertDialog) v1.getTag()).dismiss();
            });

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(purchaseView)
                    .create();

            purchaseButton.setTag(dialog);

            closeButton.setOnClickListener(v1 -> dialog.dismiss());

            dialog.show();
        });

        return view;
    }
}