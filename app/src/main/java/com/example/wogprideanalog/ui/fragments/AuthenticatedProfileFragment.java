package com.example.wogprideanalog.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.wogprideanalog.R;
import com.example.wogprideanalog.data.DatabaseHelper;
import com.example.wogprideanalog.data.model.User;

import static android.content.Context.MODE_PRIVATE;

public class AuthenticatedProfileFragment extends Fragment {

    private TextView userInfoText;
    private DatabaseHelper dbHelper;
    private String email;
    private String password;
    private SharedPreferences sharedPreferences;

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
        dbHelper = new DatabaseHelper(requireContext());
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);

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
    }

    private void updateUserInfo(User user) {
        String info = String.format("Email: %s\nПаливо: %d л\nРахунок: %d грн\nКава: %d чашок",
                user.email, user.fuelAmount, user.accountBalance, user.coffeeCups);
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

        // Оновлюємо ProfileFragment
        if (getParentFragment() != null) {
            ((ProfileFragment) getParentFragment()).checkAuthentication();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}