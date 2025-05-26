package com.example.wogprideanalog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.wogprideanalog.data.DatabaseHelper;
import com.example.wogprideanalog.data.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        findViewById(R.id.login_button).setOnClickListener(v -> login());
    }

    public void onRegisterClick(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void login() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заповніть email і пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.getUser(email, password);
        if (user != null) {
            // Зберігаємо стан авторизації та email користувача
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("userEmail", email);
            editor.putString("userPassword", password);
            editor.apply();

            Toast.makeText(this, "Успішний вхід", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Невірний email або пароль", Toast.LENGTH_SHORT).show();
        }
    }
}