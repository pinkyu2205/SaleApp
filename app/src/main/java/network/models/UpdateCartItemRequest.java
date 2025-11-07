package network.models;

import com.google.gson.annotations.SerializedName;
public class UpdateCartItemRequest {
    @SerializedName("quantity")
    public int quantity;

    // Constructor để dễ dàng tạo đối tượng
    public UpdateCartItemRequest(int quantity) {
        this.quantity = quantity;
    }

}
