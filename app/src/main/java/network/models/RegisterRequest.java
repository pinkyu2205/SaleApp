package network.models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("username")
    private String username; // or use email as username if backend expects username

    @SerializedName("email")
    private String email; // present if backend expects email

    @SerializedName("fullName")
    private String fullName; // optional depending on backend

    @SerializedName("password")
    private String password;

    public RegisterRequest(String username, String email, String fullName, String password) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPassword() { return password; }
}
