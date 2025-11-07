package network.models;

import com.google.gson.annotations.SerializedName;
public class CartItem {
    @SerializedName("cartItemID")
    public int cartItemID;

    @SerializedName("productID")
    public Integer productID; // Dùng Integer vì C# là int? (nullable)

    @SerializedName("productName")
    public String productName;

    @SerializedName("quantity")
    public int quantity;

    @SerializedName("price")
    public double price;
}
