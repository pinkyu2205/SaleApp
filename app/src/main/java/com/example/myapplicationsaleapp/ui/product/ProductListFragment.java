package com.example.myapplicationsaleapp.ui.product;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
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
import java.util.List;

public class ProductListFragment extends Fragment {
    private CartViewModel cartVM;

    public ProductListFragment(){ super(R.layout.fragment_product_list); }

    @Override public void onViewCreated(View v, @Nullable Bundle s){
        cartVM = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        RecyclerView rv = v.findViewById(R.id.rvProducts);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<Product> data = FakeRepository.getProducts();
        rv.setAdapter(new ProductAdapter(data, new ProductAdapter.Listener() {
            @Override public void onItemClick(Product p) {
                Bundle b = new Bundle(); b.putString("id", p.id);
                Navigation.findNavController(v).navigate(R.id.action_list_to_detail, b);
            }
            @Override public void onAdd(Product p) {
                cartVM.add(p,1);
                requireActivity().setTitle("Cart(" + cartVM.count() + ")");
            }
        }));

        v.<Button>findViewById(R.id.btnCart).setOnClickListener(btn ->
                Navigation.findNavController(v).navigate(R.id.action_list_to_cart)
        );

        Spinner spSort = v.findViewById(R.id.spSort);
        Spinner spFilter = v.findViewById(R.id.spFilter);
        spSort.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                new String[]{"Default","Price ↑","Price ↓","Rating"}));
        spFilter.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                new String[]{"All","Drink","Machine"}));
    }

    // --- Adapter ---
    static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {
        interface Listener { void onItemClick(Product p); void onAdd(Product p); }
        private final List<Product> items; private final Listener ls;
        ProductAdapter(List<Product> items, Listener ls){ this.items = items; this.ls = ls; }

        static class VH extends RecyclerView.ViewHolder {
            ImageView img; TextView name, desc, price; Button add;
            VH(View v){ super(v);
                img=v.findViewById(R.id.img); name=v.findViewById(R.id.tvName);
                desc=v.findViewById(R.id.tvDesc); price=v.findViewById(R.id.tvPrice);
                add=v.findViewById(R.id.btnAdd);
            }
        }
        @Override public VH onCreateViewHolder(ViewGroup p, int vt){
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_product, p, false));
        }
        @Override public void onBindViewHolder(VH h, int pos){
            Product p = items.get(pos);
            h.name.setText(p.name); h.desc.setText(p.desc); h.price.setText("$"+p.price);
            ImageRequest req = new ImageRequest.Builder(h.img.getContext())
                    .data(p.imageUrl).target(h.img).build();
            Coil.imageLoader(h.img.getContext()).enqueue(req);
            h.itemView.setOnClickListener(v -> ls.onItemClick(p));
            h.add.setOnClickListener(v -> ls.onAdd(p));
        }
        @Override public int getItemCount(){ return items.size(); }
    }
}

