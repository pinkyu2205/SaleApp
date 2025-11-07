package network.models;

import com.example.myapplicationsaleapp.Data.CartItem;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Cart {
    @SerializedName("cartID")
    public int cartID;

    @SerializedName("userID")
    public Integer userID; // Dùng Integer vì C# là int? (nullable)

    @SerializedName("username")
    public String username;

    @SerializedName("totalPrice")
    public double totalPrice;

    @SerializedName("status")
    public String status;

    @SerializedName("items")
    public List<CartItem> items;
}
