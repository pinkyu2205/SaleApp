package com.example.salesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.salesapp.R;
import com.example.salesapp.helpers.FirebaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseHelper = FirebaseHelper.getInstance();

        // Check if user is already logged in
        if (firebaseHelper.isUserLoggedIn()) {
            goToMainActivity();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> login());
        tvSignUp.setOnClickListener(v -> goToSignUp());
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        firebaseHelper.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Verify user exists in database before proceeding
                        String userId = task.getResult().getUser().getUid();

                        firebaseHelper.getUsersRef()
                                .child(userId)
                                .get()
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful() && dbTask.getResult().exists()) {
                                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                                        goToMainActivity();
                                    } else {
                                        Toast.makeText(this, "User data not found. Please contact support.", Toast.LENGTH_LONG).show();
                                        firebaseHelper.signOut();
                                        btnLogin.setEnabled(true);
                                        btnLogin.setText("Login");
                                    }
                                });
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Login failed";
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                    }
                });
    }

    private void goToSignUp() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}