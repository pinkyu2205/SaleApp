package com.example.salesapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesapp.R;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private TextView tvWelcome, tvRole;
    private Button btnLogout;
    private EditText etSearch;
    private ImageView btnCart, btnNotification;
    private String currentUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvRole = findViewById(R.id.tvRole);
        btnLogout = findViewById(R.id.btnLogout);
        etSearch = findViewById(R.id.etSearch);
        btnCart = findViewById(R.id.btnCart);
        btnNotification = findViewById(R.id.btnNotification);
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> {
            firebaseHelper.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            goToLogin();
        });

        btnCart.setOnClickListener(v -> {
            Toast.makeText(this, "Cart clicked", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Cart Activity
        });

        btnNotification.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Notifications Activity
        });

        // Bottom Navigation
        findViewById(R.id.btnNavHome).setOnClickListener(v ->
                Toast.makeText(this, "Already on Home", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnNavCategories).setOnClickListener(v ->
                Toast.makeText(this, "Categories coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnNavOrders).setOnClickListener(v ->
                Toast.makeText(this, "Orders coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnNavProfile).setOnClickListener(v ->
                Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onUserVerified(String userId, String role) {
        super.onUserVerified(userId, role);

        currentUserRole = role;

        if (firebaseHelper.getCurrentUser() != null) {
            String email = firebaseHelper.getCurrentUser().getEmail();
            String displayName = email != null ? email.split("@")[0] : "User";
            tvWelcome.setText("Hello, " + displayName + "!");
            tvRole.setText(role);
        }

        // Check permissions based on role
        if ("Admin".equals(role)) {
            Log.d(TAG, "Admin user logged in");
            // Show admin features
        } else if ("Customer".equals(role)) {
            Log.d(TAG, "Customer user logged in");
            // Show customer features
        } else {
            Log.w(TAG, "Unknown role: " + role);
            Toast.makeText(this, "Invalid user role", Toast.LENGTH_SHORT).show();
            firebaseHelper.signOut();
            goToLogin();
        }
    }
}