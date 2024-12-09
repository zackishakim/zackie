package com.example.campusexpensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonRegister;
    private DatabaseHelper dbHelper;
    private SecretKey existingKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        try {
            // Use the same key from registration (load securely in production)
            // Lấy SecretKey từ SharedPreferences (nếu có)
            existingKey = function.getSecretKey(this);

            if (existingKey == null) {
                // Nếu chưa có SecretKey, tạo mới
                SecretKey newKey = EncryptionUtils.generateKey();
                // Lưu SecretKey vào SharedPreferences
                function.saveSecretKeyIfNotExists(this, newKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        // Fetch the encrypted password from the database
                        String encryptedPassword = EncryptionUtils.encrypt(password, existingKey);
                        Log.i("Login",encryptedPassword);
                        boolean validUser = dbHelper.checkUser(username, encryptedPassword);
                        if (validUser) {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}
