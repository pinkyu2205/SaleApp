package com.example.salesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;  // ← THÊM DÒNG NÀY
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.salesapp.R;
import com.example.salesapp.helpers.FirebaseHelper;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private EditText etUsername, etEmail, etPassword, etConfirmPassword, etPhone, etAddress;
    private Button btnSignUp;
    private TextView tvLogin;
    private ImageView btnBack;  // ← THÊM DÒNG NÀY
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseHelper = FirebaseHelper.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);  // ← THÊM DÒNG NÀY
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());  // ← THÊM DÒNG NÀY
        btnSignUp.setOnClickListener(v -> signUp());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void signUp() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        Log.d(TAG, "SignUp clicked - Username: " + username + ", Email: " + email);

        // Validation
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        btnSignUp.setEnabled(false);
        btnSignUp.setText("Creating account...");

        Log.d(TAG, "Starting Firebase Auth...");

        // Create user in Firebase Auth
        firebaseHelper.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = task.getResult().getUser().getUid();
                        Log.d(TAG, "Auth successful. User ID: " + userId);

                        // Save user info to Realtime Database
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("userId", userId);
                        userInfo.put("username", username);
                        userInfo.put("email", email);
                        userInfo.put("phoneNumber", phone);
                        userInfo.put("address", address);
                        userInfo.put("role", "Customer");
                        userInfo.put("createdAt", System.currentTimeMillis());

                        Log.d(TAG, "Saving to database...");

                        firebaseHelper.getDatabaseReference()
                                .child("users")
                                .child(userId)
                                .setValue(userInfo)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Log.d(TAG, "Database save successful!");
                                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                        goToMainActivity();
                                    } else {
                                        Log.e(TAG, "Database save failed: " + dbTask.getException());
                                        Toast.makeText(this, "Failed to save user info: " +
                                                        (dbTask.getException() != null ? dbTask.getException().getMessage() : "Unknown error"),
                                                Toast.LENGTH_LONG).show();
                                        btnSignUp.setEnabled(true);
                                        btnSignUp.setText("Sign Up");
                                    }
                                });
                    } else {
                        Log.e(TAG, "Auth failed: " + task.getException());
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Sign up failed";
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                        btnSignUp.setEnabled(true);
                        btnSignUp.setText("Sign Up");
                    }
                });
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}