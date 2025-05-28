package com.example.wogprideanalog.ui.fragments;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.wogprideanalog.R;
import com.example.wogprideanalog.data.DatabaseHelper;
import com.example.wogprideanalog.data.model.User;
import com.example.wogprideanalog.ui.viewmodel.FuelViewModel;

import static android.content.Context.MODE_PRIVATE;

public class AuthenticatedProfileFragment extends Fragment {

    private TextView userInfoText;
    private DatabaseHelper dbHelper;
    private String email;
    private String password;
    private SharedPreferences sharedPreferences;
    private FuelViewModel fuelViewModel;
    private EditText rechargeAmountInput;
    private Button rechargeButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_authenticated_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userInfoText = view.findViewById(R.id.user_info_authenticated);
        Button settingsButton = view.findViewById(R.id.settings_button_authenticated);
        Button aboutButton = view.findViewById(R.id.about_button);
        Button logoutButton = view.findViewById(R.id.logout_button);
        rechargeAmountInput = view.findViewById(R.id.recharge_amount_input);
        rechargeButton = view.findViewById(R.id.recharge_button);
        dbHelper = new DatabaseHelper(requireContext());
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        fuelViewModel = new ViewModelProvider(requireActivity()).get(FuelViewModel.class);

        if (getArguments() != null) {
            email = getArguments().getString("userEmail");
            password = getArguments().getString("userPassword");
        }

        User user = dbHelper.getUser(email, password);
        if (user != null) {
            updateUserInfo(user);
        }

        settingsButton.setOnClickListener(v -> showSettings());
        aboutButton.setOnClickListener(v -> showAbout());
        logoutButton.setOnClickListener(v -> logout());
        rechargeButton.setOnClickListener(v -> rechargeBalance());
    }

    private void updateUserInfo(User user) {
        String info = String.format("Email: %s\nПаливо: %d л\nРахунок: %d ₴\nКава: %d чашок",
                user.email, user.getTotalFuelLiters(), user.accountBalance, user.getTotalCoffeeCups());
        userInfoText.setText(info);
    }

    private void showSettings() {
        Toast.makeText(getContext(), "Налаштування відкрито", Toast.LENGTH_SHORT).show();
    }

    private void showAbout() {
        Toast.makeText(getContext(), "Інформація про програму: WOG Pride Analog v1.0", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userEmail");
        editor.remove("userPassword");
        editor.apply();

        Toast.makeText(getContext(), "Ви вийшли з аккаунта", Toast.LENGTH_SHORT).show();
        if (getParentFragment() != null) {
            ((ProfileFragment) getParentFragment()).checkAuthentication();
        }
    }

    private void rechargeBalance() {
        int amount;
        try {
            amount = Integer.parseInt(rechargeAmountInput.getText().toString());
            if (amount <= 0) {
                Toast.makeText(getContext(), "Введіть коректну суму", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Введіть коректну суму", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.getUser(email, password);
        if (user == null) {
            Toast.makeText(getContext(), "Помилка: користувача не знайдено", Toast.LENGTH_SHORT).show();
            return;
        }

        user.accountBalance += amount;
        updateUserInDatabase(user);
        fuelViewModel.refreshData(); // Оновлюємо дані в FuelViewModel
        updateUserInfo(user);
        Toast.makeText(getContext(), "Баланс поповнено на " + amount + " ₴", Toast.LENGTH_SHORT).show();
        rechargeAmountInput.setText("");
    }

    private void updateUserInDatabase(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("users", "email = ? AND password = ?",
                new String[]{user.email, user.password});
        dbHelper.insertUser(user);
        db.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}