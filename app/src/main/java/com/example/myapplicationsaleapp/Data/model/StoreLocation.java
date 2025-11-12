package com.example.myapplicationsaleapp.Data.model;

public class StoreLocation {
    private String name;
    private String address;
    private String openingHours;
    private String phone;
    private double latitude;
    private double longitude;

    public StoreLocation(String name, String address, String openingHours, String phone, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getPhone() {
        return phone;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

