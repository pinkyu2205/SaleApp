package com.example.myapplicationsaleapp.Data;

public class Product {
    public String id, name, desc, imageUrl, brand, category;
    public double price, rating;

    public Product(String id, String name, double price, String desc, String imageUrl,
                   String brand, String category, double rating) {
        this.id = id; this.name = name; this.price = price; this.desc = desc;
        this.imageUrl = imageUrl; this.brand = brand; this.category = category; this.rating = rating;
    }
}
