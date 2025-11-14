package network.models;

import com.google.gson.annotations.SerializedName;

public class SendChatMessageRequest {

    @SerializedName("recipientID")
    public int recipientID;

    @SerializedName("message")
    public String message;

    public SendChatMessageRequest(int recipientID, String message) {
        this.recipientID = recipientID;
        this.message = message;
    }
}