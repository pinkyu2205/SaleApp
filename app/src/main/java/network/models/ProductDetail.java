package network.models;
import com.google.gson.annotations.SerializedName;
public class ProductDetail {
    @SerializedName("productID")
    public int productID;

    @SerializedName("productName")
    public String productName;

    @SerializedName("briefDescription")
    public String briefDescription;

    @SerializedName("fullDescription")
    public String fullDescription;

    @SerializedName("technicalSpecifications")
    public String technicalSpecifications;

    @SerializedName("price")
    public double price;

    @SerializedName("imageURL")
    public String imageURL;

    @SerializedName("categoryName")
    public String categoryName;
}
