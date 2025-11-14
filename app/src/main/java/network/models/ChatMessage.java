package network.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date; // Dùng Date cho Z-time

public class ChatMessage {

    @SerializedName("chatMessageID")
    public int chatMessageID;

    @SerializedName("senderID")
    public int senderID;

    @SerializedName("senderUsername")
    public String senderUsername;

    @SerializedName("recipientID")
    public int recipientID;

    @SerializedName("recipientUsername")
    public String recipientUsername;

    @SerializedName("message")
    public String message;

    @SerializedName("sentAt")
    public Date sentAt; // Gson sẽ tự parse Z-time (UTC)

    // Thêm trường này để check xem tin nhắn này có phải của tôi không
    // (sẽ được set thủ công ở client)
    public boolean isSentByMe = false;
}