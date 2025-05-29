package com.example.wogprideanalog.ui.fragments;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.wogprideanalog.ui.viewmodel.FuelViewModel;
import com.example.wogprideanalog.utils.QrCodeGenerator;

import static android.content.Context.MODE_PRIVATE;

import java.util.ArrayList;
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

        return view;
    }
}