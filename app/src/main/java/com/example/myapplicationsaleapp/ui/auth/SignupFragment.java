package com.example.myapplicationsaleapp.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.myapplicationsaleapp.R;
import com.google.android.material.textfield.TextInputEditText;
import network.ApiService;
import network.RetrofitClient;
import network.models.RegisterRequest;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupFragment extends Fragment {
    private static final String TAG = "SignupFragment";

    public SignupFragment() { super(R.layout.fragment_signup); }

    @Override
    public void onViewCreated(View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        TextInputEditText etUsername = v.findViewById(R.id.editTextUsername);
        TextInputEditText etEmail = v.findViewById(R.id.editTextEmail);
        TextInputEditText etPassword = v.findViewById(R.id.editTextPassword);
        TextInputEditText etPhone = v.findViewById(R.id.editTextPhone);
        TextInputEditText etAddress = v.findViewById(R.id.editTextAddress);
        Button btnRegister = v.findViewById(R.id.buttonRegister);
        ProgressBar progressBar = v.findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(btn -> {
            String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
            String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
            String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

            // Basic validation
            if (username.isEmpty()) {
                etUsername.setError("Username required");
                etUsername.requestFocus();
                return;
            }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Valid email required");
                etEmail.requestFocus();
                return;
            }
            if (password.isEmpty() || password.length() < 6) {
                etPassword.setError("Password (min 6 chars)");
                etPassword.requestFocus();
                return;
            }

            // Show loading
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);

            // Prepare body (adjust constructor according to your model)
            RegisterRequest body = new RegisterRequest(username, email, password, phone, address);
            ApiService api = RetrofitClient.get().create(ApiService.class);
            Log.d(TAG, "Register request: username=" + username + ", email=" + email);

            api.register(body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> res) {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);

                    if (res.isSuccessful()) {
                        Log.i(TAG, "Register success: code=" + res.code());
                        Toast.makeText(requireContext(), "Đăng ký thành công. Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.action_signup_to_login);
                    } else {
                        String raw = null;
                        try {
                            raw = res.errorBody() != null ? res.errorBody().string() : null;
                            String msg = "Register failed: " + res.code();
                            if (raw != null) {
                                try {
                                    JSONObject jo = new JSONObject(raw);
                                    msg = jo.optString("message", msg);
                                } catch (Exception ignore) { /* không parse được JSON */ }
                            }
                            Log.e(TAG, "Register failed: code=" + res.code() + ", body=" + raw);
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading errorBody", e);
                            Toast.makeText(requireContext(), "Register failed: " + res.code(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    Log.e(TAG, "Register network error", t);
                    Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // optional: back button (imageViewBack) behavior
        View imgBack = v.findViewById(R.id.imageViewBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(view -> {
                try {
                    Navigation.findNavController(v).popBackStack();
                } catch (Exception e) { requireActivity().onBackPressed(); }
            });
        }
    }
}
