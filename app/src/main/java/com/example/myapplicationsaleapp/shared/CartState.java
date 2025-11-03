package com.example.myapplicationsaleapp.shared;

import com.example.myapplicationsaleapp.Data.CartItem;
import java.util.*;

public class CartState {
    public final List<CartItem> items;
    public final double total;
    public CartState(List<CartItem> items, double total){ this.items = items; this.total = total; }
}
