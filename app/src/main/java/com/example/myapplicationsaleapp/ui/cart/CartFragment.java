package com.example.myapplicationsaleapp.ui.cart;

import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.*;
import com.example.myapplicationsaleapp.R;
import com.example.myapplicationsaleapp.Data.CartItem;
import com.example.myapplicationsaleapp.shared.CartState;
import com.example.myapplicationsaleapp.shared.CartViewModel;
import com.example.myapplicationsaleapp.ui.auth.TokenStore;

import java.util.List;

import network.ApiService;
import network.RetrofitClient;
import network.models.Cart;
import network.models.UpdateCartItemRequest;
import android.util.Log; // Sửa lỗi 'cannot find symbol: variable Log'
import retrofit2.Call; // Sửa lỗi 'cannot find symbol: class Call'
import retrofit2.Callback; // Sửa lỗi 'cannot find symbol: class Callback'
import retrofit2.Response; // Sửa lỗi 'cannot find symbol: class Response'

public class CartFragment extends Fragment {
    private CartViewModel cartVM;

    public CartFragment(){ super(R.layout.fragment_cart); }

    @Override public void onViewCreated(View v, @Nullable Bundle s){
        cartVM = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        RecyclerView rv = v.findViewById(R.id.rvCart);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        CartAdapter adapter = new CartAdapter(
                (id, q)-> cartVM.setQty(id,q),
                id -> cartVM.remove(id)
        );
        rv.setAdapter(adapter);

        TextView tvTotal = v.findViewById(R.id.tvTotal);

        v.<Button>findViewById(R.id.btnClear).setOnClickListener(b -> cartVM.clear());
        v.<Button>findViewById(R.id.btnCheckout).setOnClickListener(b ->
                Navigation.findNavController(v).navigate(R.id.action_cart_to_billing)
        );

        cartVM.state.observe(getViewLifecycleOwner(), (CartState st) -> {
            adapter.submit(st.items);
            tvTotal.setText("Total: $" + String.format("%.2f", st.total));
        });
    }

    // ---- Adapter đơn giản ----
    static class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {
        interface Qty { void set(String id, int q); }
        interface Remove { void rm(String id); }
        private List<CartItem> data = java.util.Collections.emptyList();
        private final Qty onQty; private final Remove onRemove;
        CartAdapter(Qty q, Remove r){ onQty=q; onRemove=r; }

        static class VH extends RecyclerView.ViewHolder {
            TextView t1,t2;
            VH(View v){ super(v); t1=v.findViewById(android.R.id.text1); t2=v.findViewById(android.R.id.text2); }
        }
        void submit(List<CartItem> list){ this.data = list; notifyDataSetChanged(); }
        @Override public VH onCreateViewHolder(ViewGroup p, int vt){
            return new VH(LayoutInflater.from(p.getContext()).inflate(android.R.layout.simple_list_item_2,p,false));
        }
        @Override public void onBindViewHolder(VH h, int pos){
            CartItem ci = data.get(pos);
            h.t1.setText(ci.product.name + "  x" + ci.quantity);
            h.t2.setText("$"+ci.product.price + " | Subtotal: $" + String.format("%.2f", ci.product.price*ci.quantity));
            h.itemView.setOnClickListener(v -> onQty.set(ci.product.id, ci.quantity+1));
            h.itemView.setOnLongClickListener(v -> { onRemove.rm(ci.product.id); return true; });
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    // --- HÀM CẬP NHẬT SỐ LƯỢNG ---
    public void updateItemQuantity(Context context, int cartItemId, int newQuantity) {
        String bearerToken = "Bearer " + TokenStore.get(context);
        UpdateCartItemRequest requestBody = new UpdateCartItemRequest(newQuantity);

        ApiService apiService = RetrofitClient.get().create(ApiService.class);
        Call<Cart> call = apiService.updateCartItem(bearerToken, cartItemId, requestBody);

        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Thành công! API trả về giỏ hàng mới
                    // Bạn cần cập nhật lại danh sách của adapter VÀ tổng tiền
                    // adapter.submitList(response.body().items);
                    // fragment.updateTotalPrice(response.body().totalPrice);
                    Log.d("Cart", "Cập nhật thành công");
                } else {
                    Log.e("Cart", "Lỗi cập nhật: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e("Cart", "Lỗi mạng: " + t.getMessage());
            }
        });
    }

    // --- HÀM XÓA SẢN PHẨM ---
    public void removeItemFromCart(Context context, int cartItemId) {
        String bearerToken = "Bearer " + TokenStore.get(context);


        ApiService apiService = RetrofitClient.get().create(ApiService.class);
        Call<Cart> call = apiService.removeCartItem(bearerToken, cartItemId);

        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Thành công! API trả về giỏ hàng mới
                    // Cập nhật lại danh sách của adapter VÀ tổng tiền
                    // adapter.submitList(response.body().items);
                    // fragment.updateTotalPrice(response.body().totalPrice);
                    Log.d("Cart", "Xóa thành công");
                } else {
                    Log.e("Cart", "Lỗi xóa: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e("Cart", "Lỗi mạng: " + t.getMessage());
            }
        });
    }
}

