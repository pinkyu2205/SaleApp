package com.example.myapplicationsaleapp.shared;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.myapplicationsaleapp.Data.CartItem;
import com.example.myapplicationsaleapp.Data.Product;
import java.util.*;

public class CartViewModel extends ViewModel {
    private final List<CartItem> items = new ArrayList<>();
    public final MutableLiveData<CartState> state = new MutableLiveData<>(new CartState(new ArrayList<>(), 0));

    public void add(Product p, int qty){
        CartItem hit = null;
        for (CartItem ci: items) if (ci.product.id.equals(p.id)) { hit = ci; break; }
        if (hit == null) items.add(new CartItem(p, qty)); else hit.quantity += qty;
        publish();
    }
    public void setQty(String id, int qty){
        for (CartItem ci: items) if (ci.product.id.equals(id)) { ci.quantity = Math.max(1, qty); break; }
        publish();
    }
    public void remove(String id){ items.removeIf(ci -> ci.product.id.equals(id)); publish(); }
    public void clear(){ items.clear(); publish(); }
    public int count(){ int c=0; for (CartItem ci: items) c+=ci.quantity; return c; }
    private void publish(){
        double total = 0;
        for (CartItem ci: items) total += ci.product.price * ci.quantity;
        state.postValue(new CartState(new ArrayList<>(items), total));
    }
}
