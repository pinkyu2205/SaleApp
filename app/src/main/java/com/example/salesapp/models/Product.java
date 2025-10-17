package com.example.salesapp.models;

public class Product {
    private int productId;
    private String productName;
    private String briefDescription;
    private String fullDescription;
    private String technicalSpecifications;
    private double price;
    private int stockQuantity;
    private String imageURL;
    private int categoryId;
    private String categoryName; // Để hiển thị tên category
    private String createdAt;

    public Product() {
    }

    public Product(int productId, String productName, String briefDescription,
                   String fullDescription, String technicalSpecifications,
                   double price, int stockQuantity, String imageURL,
                   int categoryId, String createdAt) {
        this.productId = productId;
        this.productName = productName;
        this.briefDescription = briefDescription;
        this.fullDescription = fullDescription;
        this.technicalSpecifications = technicalSpecifications;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageURL = imageURL;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
    }

    // Getters và Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getTechnicalSpecifications() {
        return technicalSpecifications;
    }

    public void setTechnicalSpecifications(String technicalSpecifications) {
        this.technicalSpecifications = technicalSpecifications;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Phương thức format giá tiền
    public String getFormattedPrice() {
        return String.format("%,.0f đ", price);
    }
}