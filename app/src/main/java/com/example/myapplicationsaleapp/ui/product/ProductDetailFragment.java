package com.example.myapplicationsaleapp.ui.product;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import coil.Coil;
import coil.request.ImageRequest;
import com.example.myapplicationsaleapp.R;
import com.example.myapplicationsaleapp.Data.FakeRepository;
import com.example.myapplicationsaleapp.Data.Product;
import com.example.myapplicationsaleapp.shared.CartViewModel;
import network.ApiService;
import network.RetrofitClient;
import network.models.ProductDetail;
import network.models.Cart;
import network.models.AddCartItemRequest;
import com.example.myapplicationsaleapp.ui.auth.TokenStore; //
import android.widget.Toast;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment {
    private int currentProductId;
    private CartViewModel cartVM;
    private Product product;
    private int qty = 1;

    public ProductDetailFragment() {
        super(R.layout.fragment_product_detail);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartVM = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        currentProductId = requireArguments().getInt("id");
        loadProductDetails(currentProductId);
        TextView tvQty = view.findViewById(R.id.tvQty);

        view.findViewById(R.id.btnPlus).setOnClickListener(b -> {
            qty++;
            tvQty.setText(String.valueOf(qty));
        });
        view.findViewById(R.id.btnMinus).setOnClickListener(b -> {
            if (qty > 1) qty--;
            tvQty.setText(String.valueOf(qty));
        });
        view.findViewById(R.id.btnAdd).setOnClickListener(b -> {
            addToCart(currentProductId, qty);

            Navigation.findNavController(view).popBackStack();
        });
    }

    // --- HÀM TẢI CHI TIẾT SẢN PHẨM ---
    private void loadProductDetails(int productId) {
        ApiService apiService = RetrofitClient.get().create(ApiService.class);
        Call<ProductDetail> call = apiService.getProductDetail(productId);

        call.enqueue(new Callback<ProductDetail>() {
            @Override
            public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
                if (getView() == null) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    ProductDetail detail = response.body();

                    // !!! NƠI CẬP NHẬT UI BẰNG DATA THẬT !!!
                    TextView tvName = getView().findViewById(R.id.tvName);
                    TextView tvDesc = getView().findViewById(R.id.tvDesc);
                    ImageView img = getView().findViewById(R.id.img);

                    tvName.setText(detail.productName);
                    // Dùng mô tả đầy đủ (hoặc briefDescription tùy bạn)
                    tvDesc.setText(detail.fullDescription);

                    Coil.imageLoader(requireContext()).enqueue(new ImageRequest.Builder(requireContext())
                            .data(detail.imageURL)
                            .target(img)
                            .build());

                } else {
                    Log.e("ProductDetail", "Lỗi khi tải chi tiết: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ProductDetail> call, Throwable t) {
                Log.e("ProductDetail", "Thất bại: " + t.getMessage());
            }
        });
    }

    // --- HÀM THÊM VÀO GIỎ HÀNG ---
    private void addToCart(int productId, int quantity) {
        // 1. Lấy token
        String token = TokenStore.get(getContext());
        if (token == null) {
            // (Điều hướng người dùng về trang Login)
            Toast.makeText(getContext(), "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        String bearerToken = "Bearer " + token;

        // 2. Tạo request body
        AddCartItemRequest requestBody = new AddCartItemRequest(productId, quantity);



        ApiService apiService = RetrofitClient.get().create(ApiService.class);
        Call<Cart> call = apiService.addItemToCart(bearerToken, requestBody);

        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    // Thêm thành công!
                    Toast.makeText(getContext(), "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
                    // (Có thể cập nhật số lượng trên icon giỏ hàng của MainActivity)
                } else {
                    // Xử lý lỗi (ví dụ: 401 Unauthorized, 400 Bad Request)
                    Log.e("AddToCart", "Lỗi: " + response.message());
                    Toast.makeText(getContext(), "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


