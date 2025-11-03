package com.example.myapplicationsaleapp.ui.product;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
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

public class ProductDetailFragment extends Fragment {
    private CartViewModel cartVM; private Product product; private int qty = 1;

    public ProductDetailFragment(){ super(R.layout.fragment_product_detail); }

    @Override public void onViewCreated(View v, @Nullable Bundle s){
        cartVM = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        String id = requireArguments().getString("id");
        product = FakeRepository.getById(id);

        ImageView img = v.findViewById(R.id.img);
        ((TextView)v.findViewById(R.id.tvName)).setText(product.name);
        ((TextView)v.findViewById(R.id.tvDesc)).setText(product.desc);
        Coil.imageLoader(requireContext()).enqueue(new ImageRequest.Builder(requireContext())
                .data(product.imageUrl).target(img).build());

        TextView tvQty = v.findViewById(R.id.tvQty);
        v.findViewById(R.id.btnPlus).setOnClickListener(b -> { qty++; tvQty.setText(String.valueOf(qty)); });
        v.findViewById(R.id.btnMinus).setOnClickListener(b -> { if(qty>1) qty--; tvQty.setText(String.valueOf(qty)); });
        v.findViewById(R.id.btnAdd).setOnClickListener(b -> {
            cartVM.add(product, qty);
            Navigation.findNavController(v).popBackStack();
        });
    }
}

