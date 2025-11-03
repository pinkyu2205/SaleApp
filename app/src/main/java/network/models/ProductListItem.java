package network.models;

import com.google.gson.annotations.SerializedName;

public class ProductListItem {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("desc")
    public String desc;

    @SerializedName("price")
    public double price;

    @SerializedName("imageUrl")
    public String imageUrl;
}
