package network.models;
import com.google.gson.annotations.SerializedName;
public class AddCartItemRequest {
    @SerializedName("productId")
    public int productId;

    @SerializedName("quantity")
    public int quantity;

    // Constructor để dễ dàng tạo đối tượng
    public AddCartItemRequest(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
