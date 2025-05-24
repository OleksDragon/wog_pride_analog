package com.example.wogprideanalog.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.wogprideanalog.R;
import com.example.wogprideanalog.ui.viewmodel.FuelViewModel;
import com.example.wogprideanalog.utils.QrCodeGenerator;

public class HomeFragment extends Fragment {

    private FuelViewModel fuelViewModel;
    private ImageView qrCodeImageView;
    private TextView balanceTextView, fuelIndicator, coffeeIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fuelViewModel = new ViewModelProvider(this).get(FuelViewModel.class);

        balanceTextView = view.findViewById(R.id.balance_text_view);
        fuelIndicator = view.findViewById(R.id.fuel_indicator);
        coffeeIndicator = view.findViewById(R.id.coffee_indicator);
        qrCodeImageView = view.findViewById(R.id.qr_code_image_view);

        if (balanceTextView == null || fuelIndicator == null || coffeeIndicator == null || qrCodeImageView == null) {
            throw new IllegalStateException("One or more views are null");
        }

        fuelViewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            balanceTextView.setText(balance != null ? balance + " ₴" : "0 ₴");
        });

        fuelViewModel.getFuelAmount().observe(getViewLifecycleOwner(), fuel -> {
            fuelIndicator.setText(fuel != null ? fuel + " л" : "0.0 л");
        });

        fuelViewModel.getCoffeeAmount().observe(getViewLifecycleOwner(), coffee -> {
            coffeeIndicator.setText(coffee != null ? coffee + " чашок" : "0 чашок");
        });

        String cardId = "USER123";
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


        return view;
    }
}