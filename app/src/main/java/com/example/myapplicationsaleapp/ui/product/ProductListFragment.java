package com.example.myapplicationsaleapp.ui.product;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.*;
import coil.Coil;
import coil.request.ImageRequest;
import com.example.myapplicationsaleapp.R;
import com.example.myapplicationsaleapp.Data.FakeRepository;
import com.example.myapplicationsaleapp.Data.Product;
import com.example.myapplicationsaleapp.shared.CartViewModel;

import java.util.ArrayList;
import java.util.List;
import network.ApiService;
import network.RetrofitClient;
import network.models.ProductListItem;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
import java.util.ArrayList;
import java.util.Locale;
import android.annotation.SuppressLint;
import network.models.ProductListItem;
import com.example.myapplicationsaleapp.Data.Product;


public class ProductListFragment extends Fragment {
    private CartViewModel cartVM;
    private ProductAdapter productAdapter;

    public ProductListFragment() {
        super(R.layout.fragment_product_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartVM = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        RecyclerView rv = view.findViewById(R.id.rvProducts);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        productAdapter = new ProductAdapter(new ArrayList<>(), new ProductAdapter.Listener() {
            @Override
            public void onItemClick(ProductListItem p) { // <-- 4. Sửa model thành ProductListItem
                Bundle b = new Bundle();

                // 5. Gửi ID kiểu int (vì productID là int)
                b.putInt("id", p.productID);

                Navigation.findNavController(view).navigate(R.id.action_list_to_detail, b);
            }

            @Override
            public void onAdd(ProductListItem p) { // <-- 6. Sửa model thành ProductListItem
                Product fakeProduct = new Product(
                        String.valueOf(p.productID),
                        p.productName,
                        p.price,
                        p.briefDescription,
                        p.imageURL,
                        p.categoryName,
                        p.categoryName,
                        0.0 // (Rating giả)
                );
                cartVM.add(fakeProduct, 1);
                requireActivity().setTitle("Cart(" + cartVM.count() + ")");
            }
        });
        rv.setAdapter(productAdapter);

        view.<Button>findViewById(R.id.btnCart).setOnClickListener(btn ->
                Navigation.findNavController(view).navigate(R.id.action_list_to_cart)
        );

        Spinner spSort = view.findViewById(R.id.spSort);
        Spinner spFilter = view.findViewById(R.id.spFilter);
        spSort.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                new String[]{"Default", "Price ↑", "Price ↓", "Rating"}));
        spFilter.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                new String[]{"All", "Drink", "Machine"}));
        loadProducts();
    }


    // --- Adapter ---
    static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {
        interface Listener {
            void onItemClick(ProductListItem p);

            void onAdd(ProductListItem p);
        }

        private List<ProductListItem> items;
        private final Listener ls;
        ProductAdapter(List<ProductListItem> items, Listener ls) {
            this.items = items;
            this.ls = ls;
        }
        @SuppressLint("NotifyDataSetChanged")
        public void updateData(List<ProductListItem> newItems) {
            this.items.clear();
            if (newItems != null) {
                this.items.addAll(newItems);
            }
            notifyDataSetChanged();
        }
        static class VH extends RecyclerView.ViewHolder {
            ImageView img;
            TextView name, desc, price;
            Button add;
            VH(View v) {
                super(v);
                img = v.findViewById(R.id.img);
                name = v.findViewById(R.id.tvName);
                desc = v.findViewById(R.id.tvDesc);
                price = v.findViewById(R.id.tvPrice);
                add = v.findViewById(R.id.btnAdd);
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup p, int vt) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_product, p, false));
        }

        @Override
        public void onBindViewHolder(VH h, int pos) {
            ProductListItem p = items.get(pos);
            h.name.setText(p.productName);
            h.desc.setText(p.briefDescription);

            h.price.setText(String.format(java.util.Locale.US, "%.0f VND", p.price));

            ImageRequest req = new ImageRequest.Builder(h.img.getContext())
                    .data(p.imageURL) // Sửa 8: Dùng imageURL
                    .target(h.img).build();
            Coil.imageLoader(h.img.getContext()).enqueue(req);
            h.itemView.setOnClickListener(v -> ls.onItemClick(p));
            h.add.setOnClickListener(v -> ls.onAdd(p));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private void loadProducts() {
        // 1. Lấy service

        ApiService apiService = RetrofitClient.get().create(ApiService.class);
        Call<List<ProductListItem>> call = apiService.getProducts(null, null, null, null);

        // 3. Thực thi bất đồng bộ
        call.enqueue(new Callback<List<ProductListItem>>() {
            @Override
            public void onResponse(Call<List<ProductListItem>> call, Response<List<ProductListItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductListItem> products = response.body();
                    if (productAdapter != null) {
                        productAdapter.updateData(products);
                    }
                    Log.d("ProductList", "Tải thành công: " + products.size() + " sản phẩm");
                } else {
                    // Xử lý lỗi (ví dụ: response.code() == 404)
                    Log.e("ProductList", "Lỗi khi tải: " + response.message());
                }
                // (Có thể ẩn loading indicator tại đây)
            }

            @Override
            public void onFailure(Call<List<ProductListItem>> call, Throwable t) {
                // Xử lý lỗi (ví dụ: mất mạng)
                Log.e("ProductList", "Thất bại: " + t.getMessage());
                // (Có thể ẩn loading indicator tại đây)
            }
        });
    }
}
