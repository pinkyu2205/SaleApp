package com.example.myapplicationsaleapp.ui.checkout;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.myapplicationsaleapp.R;

public class BillingFragment extends Fragment {
    public BillingFragment(){ super(R.layout.fragment_billing); }

    @Override public void onViewCreated(View v, @Nullable Bundle s){
        Spinner sp = v.findViewById(R.id.spPayment);
        sp.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                new String[]{"VNPay","ZaloPay","PayPal"}));
        v.<Button>findViewById(R.id.btnPay).setOnClickListener(btn ->
                // TODO: tích hợp cổng thanh toán thật rồi điều hướng theo kết quả
                Navigation.findNavController(v).navigate(R.id.action_billing_to_success)
        );
    }
}
