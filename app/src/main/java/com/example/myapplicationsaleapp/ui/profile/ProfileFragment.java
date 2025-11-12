package com.example.myapplicationsaleapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.NavOptions;

import com.example.myapplicationsaleapp.R;
import com.example.myapplicationsaleapp.ui.auth.TokenStore; // Import TokenStore

import network.ApiService;
import network.RetrofitClient;
import network.models.UserProfile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ApiService apiService;
//    private TokenStore tokenStore;

    private TextView tvUsername, tvEmail, tvPhone, tvAddress, tvError;
    private ProgressBar progressBar;
    private Button btnLogout;
    private Group contentGroup; // Dùng để ẩn/hiện toàn bộ nội dung

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.get().create(ApiService.class);
        // Khởi tạo TokenStore để lấy token
//        tokenStore = new TokenStore(requireActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ánh xạ View
        tvUsername = view.findViewById(R.id.profile_tv_username);
        tvEmail = view.findViewById(R.id.profile_tv_email);
        tvPhone = view.findViewById(R.id.profile_tv_phone);
        tvAddress = view.findViewById(R.id.profile_tv_address);
        tvError = view.findViewById(R.id.profile_tv_error);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        btnLogout = view.findViewById(R.id.profile_btn_logout);
        contentGroup = view.findViewById(R.id.profile_content_group);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gọi API để lấy thông tin profile
        fetchUserProfile();

        // Xử lý sự kiện click Đăng xuất
        btnLogout.setOnClickListener(v -> {
            // Xóa token đã lưu
            TokenStore.clear(requireContext());

            // Điều hướng về màn hình Login và xóa sạch back stack
            NavController navController = Navigation.findNavController(v);
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true) // Xóa toàn bộ back stack
                    .build();
            navController.navigate(R.id.action_profile_to_login, null, navOptions);
        });
    }

    private void fetchUserProfile() {
        showLoading(true);
        String token = TokenStore.get(requireContext());

        if (token == null || token.isEmpty()) {
            showError("Bạn chưa đăng nhập.");
            // Nếu muốn, có thể tự động logout tại đây
            // btnLogout.performClick();
            return;
        }

        // API yêu cầu "Bearer [token]"
        String bearerToken = "Bearer " + token;

        apiService.getMyProfile(bearerToken).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Hiển thị thông tin thành công
                    displayProfile(response.body());
                } else if (response.code() == 401) {
                    // Lỗi 401 (Unauthorized): Token sai hoặc hết hạn
                    showError("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
                    // Tự động đăng xuất
                    btnLogout.performClick();
                } else {
                    // Các lỗi khác (500, 404...)
                    showError("Lỗi khi tải thông tin: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                // Lỗi mạng (không có kết nối, server sập...)
                showLoading(false);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Hiển thị dữ liệu profile lên UI
     */
    private void displayProfile(UserProfile profile) {
        contentGroup.setVisibility(View.VISIBLE); // Hiện nội dung
        tvError.setVisibility(View.GONE); // Ẩn lỗi

        tvUsername.setText(profile.getUsername());
        tvEmail.setText(profile.getEmail());

        // Kiểm tra giá trị null để hiển thị text thân thiện
        tvPhone.setText(profile.getPhoneNumber() != null && !profile.getPhoneNumber().isEmpty()
                ? profile.getPhoneNumber()
                : "Chưa cập nhật");

        tvAddress.setText(profile.getAddress() != null && !profile.getAddress().isEmpty()
                ? profile.getAddress()
                : "Chưa cập nhật");
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            contentGroup.setVisibility(View.GONE); // Ẩn nội dung
            tvError.setVisibility(View.GONE); // Ẩn lỗi
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        contentGroup.setVisibility(View.GONE); // Ẩn nội dung
        tvError.setVisibility(View.VISIBLE); // Hiện lỗi
        tvError.setText(message);
        // Có thể dùng Toast nếu bạn muốn
        // Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}