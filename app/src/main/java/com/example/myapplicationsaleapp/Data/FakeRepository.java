package com.example.myapplicationsaleapp.Data;

import java.util.*;

public class FakeRepository {
    private static final List<Product> products = Arrays.asList(
            new Product("1","Coffee Beans",3.5,"Arabica premium","https://picsum.photos/300?1","Coffee","Drink",4.6),
            new Product("2","Green Tea",2.2,"Organic sencha","https://picsum.photos/300?2","Tea","Drink",4.4),
            new Product("3","Espresso Machine",129.0,"Compact 15-bar","https://picsum.photos/300?3","BrandX","Machine",4.8)
    );
    public static List<Product> getProducts(){ return products; }
    public static Product getById(String id){
        for (Product p: products) if (p.id.equals(id)) return p;
        throw new NoSuchElementException("Product not found");
    }
}
