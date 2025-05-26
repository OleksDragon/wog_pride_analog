package com.example.wogprideanalog.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.wogprideanalog.LoginActivity;
import com.example.wogprideanalog.R;
import com.example.wogprideanalog.RegisterActivity;
import com.example.wogprideanalog.data.DatabaseHelper;
import com.example.wogprideanalog.data.model.User;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private LinearLayout unauthenticatedLayout;
    private LinearLayout authenticatedLayout;
    private TextView userInfoText;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unauthenticatedLayout = view.findViewById(R.id.unauthenticated_layout);
        authenticatedLayout = view.findViewById(R.id.authenticated_layout);
        userInfoText = view.findViewById(R.id.user_info);
        Button loginButton = view.findViewById(R.id.login_button);
        Button registerButton = view.findViewById(R.id.register_button);
        dbHelper = new DatabaseHelper(requireContext());
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);

        checkAuthentication();

        loginButton.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        registerButton.setOnClickListener(v -> startActivity(new Intent(getContext(), RegisterActivity.class)));
    }

    public void checkAuthentication() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            // Користувач не авторизований — показуємо кнопки входу та реєстрації
            unauthenticatedLayout.setVisibility(View.VISIBLE);
            authenticatedLayout.setVisibility(View.GONE);
        } else {
            // Користувач авторизований — приховуємо кнопки та показуємо AuthenticatedProfileFragment
            unauthenticatedLayout.setVisibility(View.GONE);
            authenticatedLayout.setVisibility(View.VISIBLE);
            userInfoText.setVisibility(View.GONE);

            // Створюємо AuthenticatedProfileFragment і передаємо email та password
            String email = sharedPreferences.getString("userEmail", "");
            String password = sharedPreferences.getString("userPassword", "");
            AuthenticatedProfileFragment authenticatedFragment = new AuthenticatedProfileFragment();
            Bundle args = new Bundle();
            args.putString("userEmail", email);
            args.putString("userPassword", password);
            authenticatedFragment.setArguments(args);

            // Замінюємо контейнер authenticatedLayout на новий фрагмент
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.authenticated_layout, authenticatedFragment);
            transaction.commit();
        }
    }

    private void showSettings() {
        Toast.makeText(getContext(), "Налаштування відкрито", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}