package com.example.salesapp.models;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String email;
    private String phoneNumber;
    private String address;
    private String role;
    private String createdAt;

    // Constructor rỗng
    public User() {
    }

    // Constructor đầy đủ
    public User(int userId, String username, String passwordHash, String email,
                String phoneNumber, String address, String role, String createdAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Constructor cho đăng ký
    public User(String username, String password, String email, String phoneNumber, String address) {
        this.username = username;
        this.passwordHash = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = "Customer";
    }

    // Getters và Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}