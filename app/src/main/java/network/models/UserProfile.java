package network.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model này dùng để hứng dữ liệu từ API /api/User/me
 * Phải khớp với UserProfileDto.cs bên backend.
 */
public class UserProfile {

    // Sử dụng @SerializedName để khớp chính xác với tên key trong JSON (nếu khác)
    // Ở đây key "userID" bên DTO là chữ hoa
    @SerializedName("userID")
    private int userID;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("address")
    private String address;

    @SerializedName("role")
    private String role;

    // Getters
    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }
}