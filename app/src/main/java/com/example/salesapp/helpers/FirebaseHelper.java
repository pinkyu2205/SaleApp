package com.example.salesapp.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private static FirebaseHelper instance;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    private FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();

        // THAY ĐỔI URL THEO LOG LỖI
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://salesapp-6c159-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseRef = database.getReference();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseRef;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public void signOut() {
        mAuth.signOut();
    }

    // Database references
    public DatabaseReference getProductsRef() {
        return databaseRef.child("products");
    }

    public DatabaseReference getCategoriesRef() {
        return databaseRef.child("categories");
    }

    public DatabaseReference getCartsRef() {
        return databaseRef.child("carts");
    }

    public DatabaseReference getOrdersRef() {
        return databaseRef.child("orders");
    }

    public DatabaseReference getChatMessagesRef() {
        return databaseRef.child("chatMessages");
    }

    public DatabaseReference getStoreLocationsRef() {
        return databaseRef.child("storeLocations");
    }

    public DatabaseReference getUsersRef() {
        return databaseRef.child("users");
    }
}