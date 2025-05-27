package com.example.wogprideanalog.ui.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.wogprideanalog.R;
import com.example.wogprideanalog.ui.viewmodel.FuelViewModel;
import com.example.wogprideanalog.utils.QrCodeGenerator;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private FuelViewModel fuelViewModel;
    private ImageView qrCodeImageView;
    private TextView balanceTextView, fuelIndicator, coffeeIndicator;
    private SharedPreferences sharedPreferences;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fuelViewModel = new ViewModelProvider(requireActivity()).get(FuelViewModel.class);
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);

        balanceTextView = view.findViewById(R.id.balance_text_view);
        fuelIndicator = view.findViewById(R.id.fuel_indicator);
        coffeeIndicator = view.findViewById(R.id.coffee_indicator);
        qrCodeImageView = view.findViewById(R.id.qr_code_image_view);
        ImageButton walletButton = view.findViewById(R.id.wallet_button);

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
            transaction.addToBackStack(null); // Добавляем в стек, чтобы можно было вернуться назад
            transaction.commit();
        });

        return view;
    }
}