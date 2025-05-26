package com.example.wogprideanalog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.wogprideanalog.data.DatabaseHelper;
import com.example.wogprideanalog.data.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);
        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.register_button).setOnClickListener(v -> register());
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void register() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заповніть email і пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        // Значення fuel, balance і coffee встановлюємо на 0
        User user = new User(email, password, 0, 0, 0);
        long id = dbHelper.insertUser(user);
        if (id != -1) {
            Toast.makeText(this, "Реєстрація успішна", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Помилка при реєстрації", Toast.LENGTH_SHORT).show();
        }
    }
}