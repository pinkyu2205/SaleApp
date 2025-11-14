package com.example.myapplicationsaleapp.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT; // Cần thư viện JWT để parse token
import com.example.myapplicationsaleapp.R;
import com.example.myapplicationsaleapp.ui.auth.TokenStore;

import java.util.ArrayList;
import java.util.List;

import network.ApiService;
import network.RetrofitClient;
import network.SignalRService;
import network.models.ChatMessage;
import network.models.SendChatMessageRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationFragment extends Fragment {
    private static final String TAG = "ConvFragment";

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ProgressBar progressBar;

    private ConversationAdapter adapter;
    private List<ChatMessage> messageList = new ArrayList<>();
    private SignalRService signalRService;

    private int otherUserId;
    private String otherUsername;
    private int myUserId = -1; // ID của người dùng hiện tại
    private String token;

    public ConversationFragment() {
        super(R.layout.fragment_conversation);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Lấy thông tin từ arguments (do ChatListFragment gửi qua)
        if (getArguments() != null) {
            otherUserId = getArguments().getInt("otherUserId");
            otherUsername = getArguments().getString("otherUsername");
        }

        // 2. Lấy token và MyUserID
        token = TokenStore.get(getContext());
        if (token != null) {
            try {
                // Giải mã token để lấy UserID
                JWT jwt = new JWT(token);
                String userIdStr = jwt.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier").asString();
                if (userIdStr != null) {
                    myUserId = Integer.parseInt(userIdStr);
                }
            } catch (Exception e) {
                Log.e(TAG, "Lỗi giải mã JWT", e);
                Toast.makeText(getContext(), "Token không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        }

        // 3. Khởi tạo SignalR Service
        signalRService = SignalRService.getInstance(getContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set tiêu đề cho Toolbar
        if (getActivity() instanceof AppCompatActivity && otherUsername != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chat với " + otherUsername);
        }

        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        progressBar = view.findViewById(R.id.progressBar);

        setupRecyclerView();

        btnSend.setOnClickListener(v -> sendMessage());

        loadConversationHistory();

        // 4. Bắt đầu kết nối SignalR (nếu chưa kết nối)
        signalRService.startConnection();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 5. Đăng ký lắng nghe tin nhắn mới khi Fragment hiển thị
        signalRService.setOnMessageReceivedListener(this::onMessageReceived);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 6. Gỡ lắng nghe khi Fragment bị che
        signalRService.removeOnMessageReceivedListener();
    }

    // (Tùy chọn: Ngắt kết nối SignalR khi thoát app hoàn toàn, có thể làm ở MainActivity)
    // @Override
    // public void onDestroy() {
    //     super.onDestroy();
    //     signalRService.stopConnection();
    // }

    private void setupRecyclerView() {
        adapter = new ConversationAdapter(messageList, myUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Luôn cuộn xuống dưới cùng
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);
    }

    private void loadConversationHistory() {
        if (token == null || otherUserId == 0) return;

        progressBar.setVisibility(View.VISIBLE);
        ApiService api = RetrofitClient.get().create(ApiService.class);

        api.getConversationHistory("Bearer " + token, otherUserId).enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    messageList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    rvMessages.scrollToPosition(messageList.size() - 1); // Cuộn xuống cuối
                } else {
                    Log.e(TAG, "Lỗi tải lịch sử chat: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Lỗi mạng (lịch sử chat): ", t);
            }
        });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty() || token == null || otherUserId == 0) {
            return;
        }

        // Tạm thời vô hiệu hóa nút gửi
        btnSend.setEnabled(false);
        etMessage.setEnabled(false);

        SendChatMessageRequest request = new SendChatMessageRequest(otherUserId, messageText);
        ApiService api = RetrofitClient.get().create(ApiService.class);

        // Gửi tin nhắn qua API REST
        // (Backend sẽ tự động đẩy tin này qua SignalR cho cả 2 client)
        api.sendMessage("Bearer " + token, request).enqueue(new Callback<ChatMessage>() {
            @Override
            public void onResponse(Call<ChatMessage> call, Response<ChatMessage> response) {
                // Kích hoạt lại nút gửi
                btnSend.setEnabled(true);
                etMessage.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    // Xóa text trong ô nhập liệu
                    etMessage.setText("");

                    // Tin nhắn đã được gửi (và sẽ được nhận lại qua SignalR)
                    Log.d(TAG, "Gửi tin nhắn qua API thành công.");
                    // (Chúng ta không cần add vào list ở đây, vì sẽ chờ Hub trả về)
                } else {
                    Log.e(TAG, "Lỗi gửi tin nhắn (API): " + response.message());
                    Toast.makeText(getContext(), "Gửi thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatMessage> call, Throwable t) {
                btnSend.setEnabled(true);
                etMessage.setEnabled(true);
                Log.e(TAG, "Lỗi mạng (gửi tin nhắn): ", t);
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 7. Hàm được gọi khi SignalR đẩy tin nhắn mới về
    private void onMessageReceived(ChatMessage message) {
        // Đảm bảo chúng ta đang ở UI thread
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            // Chỉ thêm tin nhắn nếu nó thuộc cuộc trò chuyện này
            if ((message.senderID == myUserId && message.recipientID == otherUserId) ||
                    (message.senderID == otherUserId && message.recipientID == myUserId))
            {
                messageList.add(message);
                adapter.notifyItemInserted(messageList.size() - 1);
                rvMessages.scrollToPosition(messageList.size() - 1); // Cuộn xuống cuối
            }
        });
    }
}