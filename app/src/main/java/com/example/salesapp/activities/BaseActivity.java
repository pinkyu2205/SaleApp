package com.example.salesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.salesapp.helpers.FirebaseHelper;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseHelper = FirebaseHelper.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verify user every time activity resumes
        if (requiresAuthentication()) {
            verifyUserAuthentication();
        }
    }

    // Override this in activities that need authentication check
    protected boolean requiresAuthentication() {
        return true;
    }

    protected void verifyUserAuthentication() {
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();

        if (currentUser == null) {
            Log.w(TAG, "No user logged in, redirecting to login");
            goToLogin();
            return;
        }

        // Reload user to check if still exists on server
        currentUser.reload()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "User reload failed - account may have been deleted");
                        Toast.makeText(this, "Your account is no longer valid. Please login again.", Toast.LENGTH_LONG).show();
                        firebaseHelper.signOut();
                        goToLogin();
                        return;
                    }

                    // User exists, now verify in database
                    verifyUserInDatabase(currentUser.getUid());
                });
    }

    private void verifyUserInDatabase(String userId) {
        firebaseHelper.getUsersRef()
                .child(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // User exists in database
                        String role = task.getResult().child("role").getValue(String.class);
                        onUserVerified(userId, role);
                    } else {
                        // User not in database - deleted or never created
                        Log.w(TAG, "User not found in database");
                        Toast.makeText(this, "User data not found. Please contact support.", Toast.LENGTH_LONG).show();
                        firebaseHelper.signOut();
                        goToLogin();
                    }
                });
    }

    // Override this to handle user verification
    protected void onUserVerified(String userId, String role) {
        Log.d(TAG, "User verified: " + userId + ", Role: " + role);
    }

    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}