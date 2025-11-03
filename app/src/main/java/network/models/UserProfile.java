package network.models;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    @SerializedName("username")
    public String username;

    @SerializedName("email")
    public String email;
}
