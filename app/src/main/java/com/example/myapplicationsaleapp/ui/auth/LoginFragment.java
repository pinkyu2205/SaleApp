package com.example.myapplicationsaleapp.ui.auth;

import android.os.Bundle;
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
import network.models.LoginRequest;
import network.models.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    public LoginFragment(){ super(R.layout.fragment_login); }

    @Override public void onViewCreated(View v, @Nullable Bundle s){
        v.<Button>findViewById(R.id.btnLogin).setOnClickListener(btn -> {
            String username = v.<TextView>findViewById(R.id.etEmail).getText().toString().trim();
            String password = v.<TextView>findViewById(R.id.etPassword).getText().toString();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }
            ApiService api = RetrofitClient.get().create(ApiService.class);
            api.login(new LoginRequest(username, password)).enqueue(new Callback<LoginResponse>() {
                @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> res) {
                    if (res.isSuccessful() && res.body() != null) {
                        String token = res.body().getToken();
                        TokenStore.save(requireContext(), token);
                        Navigation.findNavController(v).navigate(R.id.action_login_to_productList);
                    } else {
                        Toast.makeText(requireContext(), "Login failed: " + res.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        v.<TextView>findViewById(R.id.tvSignup).setOnClickListener(tv ->
                Navigation.findNavController(v).navigate(R.id.action_login_to_signup)
        );
    }
}

