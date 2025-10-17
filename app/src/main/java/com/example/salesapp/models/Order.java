package com.example.salesapp.models;

public class Order {
    private int orderId;
    private int cartId;
    private int userId;
    private String paymentMethod;
    private String billingAddress;
    private String shippingAddress;
    private String orderStatus;
    private double totalAmount;
    private String orderDate;

    public Order() {
    }

    public Order(int orderId, int cartId, int userId, String paymentMethod,
                 String billingAddress, String shippingAddress, String orderStatus,
                 double totalAmount, String orderDate) {
        this.orderId = orderId;
        this.cartId = cartId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }

    // Getters và Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getFormattedTotalAmount() {
        return String.format("%,.0f đ", totalAmount);
    }
}