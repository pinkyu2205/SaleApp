package com.example.myapplicationsaleapp.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.myapplicationsaleapp.R;
import network.ApiService;
import network.RetrofitClient;
import network.models.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupFragment extends Fragment {
    private static final String TAG = "Signup";

    public SignupFragment(){ super(R.layout.fragment_signup); }

    @Override public void onViewCreated(View v, @Nullable Bundle s){
        v.<Button>findViewById(R.id.btnSignup).setOnClickListener(btn -> {
            String fullName = v.<TextView>findViewById(R.id.etName).getText().toString().trim();
            String email = v.<TextView>findViewById(R.id.etEmail).getText().toString().trim();
            String password = v.<TextView>findViewById(R.id.etPassword).getText().toString();
            String confirm = v.<TextView>findViewById(R.id.etConfirmPassword).getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm)) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            RegisterRequest body = new RegisterRequest(email, email, fullName, password);
            ApiService api = RetrofitClient.get().create(ApiService.class);
            Log.d(TAG, "Register request: email=" + email + ", fullName=" + fullName);
            api.register(body).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> res) {
                    if (res.isSuccessful()) {
                        Log.i(TAG, "Register success: code=" + res.code());
                        Toast.makeText(requireContext(), "Register success. Please login.", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.action_signup_to_login);
                    } else {
                        String raw = null;
                        try { raw = res.errorBody() != null ? res.errorBody().string() : null; } catch (Exception ignored) {}
                        String msg = "Register failed: " + res.code();
                        try { msg = new org.json.JSONObject(raw).optString("message", msg); } catch (Exception ignored) {}
                        Log.e(TAG, "Register failed: code=" + res.code() + ", body=" + raw);
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                    }
                }
                @Override public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Register network error", t);
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}

