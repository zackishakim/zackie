package com.example.campusexpensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import javax.crypto.SecretKey;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonRegister;
    private DatabaseHelper dbHelper;
    private SecretKey existingKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
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

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                try {

                    if (username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        String encryptedPassword = EncryptionUtils.encrypt(password, existingKey);
                        boolean success = dbHelper.registerUser(username, encryptedPassword);

                        if (success) {
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }
}
