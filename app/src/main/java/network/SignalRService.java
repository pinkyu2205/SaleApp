package network;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.myapplicationsaleapp.ui.auth.TokenStore;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import java.util.function.Consumer;
import network.models.ChatMessage;

// XÓA TẤT CẢ IMPORT LIÊN QUAN TỚI OkHttpClient hoặc HttpConnectionOptions

public class SignalRService {
    private static final String TAG = "SignalRService";
    private static volatile SignalRService instance;
    private HubConnection hubConnection;
    private Context context;

    private static final String HUB_URL = "https://10.0.2.2:7126/chathub";

    private SignalRService(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized SignalRService getInstance(Context context) {
        if (instance == null) {
            instance = new SignalRService(context);
        }
        return instance;
    }

    // ==== SỬA LẠI PHƯƠNG THỨC NÀY VỀ DẠNG GỐC ====
    public void startConnection() {
        String token = TokenStore.get(context);
        if (token == null) {
            Log.e(TAG, "Không thể kết nối SignalR: Chưa đăng nhập (token == null)");
            return;
        }

        if (hubConnection == null || hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            try {
                // Chạy trên một thread riêng (giữ nguyên)
                io.reactivex.rxjava3.schedulers.Schedulers.io().scheduleDirect(() -> {
                    try {

                        // Xây dựng HubConnection CƠ BẢN NHẤT
                        // SSL Bypass đã được xử lý toàn cục ở Bước 2
                        hubConnection = HubConnectionBuilder.create(HUB_URL)
                                .withAccessTokenProvider(io.reactivex.rxjava3.core.Single.defer(() -> {
                                    return io.reactivex.rxjava3.core.Single.just(TokenStore.get(context));
                                }))
                                .withHandshakeResponseTimeout(30 * 1000)
                                .build();

                        registerHubEvents();

                        hubConnection.start().blockingAwait();
                        Log.i(TAG, "Kết nối SignalR THÀNH CÔNG (đã bypass SSL)!");

                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi kết nối SignalR: " + e.getMessage());
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi tạo HubConnection: " + e.getMessage());
            }
        } else {
            Log.d(TAG, "Kết nối SignalR đã tồn tại, bỏ qua...");
        }
    }
    // ==== KẾT THÚC SỬA ĐỔI ====


    public void stopConnection() {
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.stop();
            hubConnection = null;
            Log.i(TAG, "Đã ngắt kết nối SignalR.");
        }
    }

    private void registerHubEvents() {
        if (hubConnection == null) return;

        hubConnection.onClosed(exception -> {
            Log.w(TAG, "Kết nối SignalR đã đóng: " + (exception != null ? exception.getMessage() : "Bình thường"));
        });
    }

    public void setOnMessageReceivedListener(@NonNull Consumer<ChatMessage> listener) {
        if (hubConnection == null) {
            Log.e(TAG, "HubConnection null, không thể đăng ký listener");
            return;
        }

        Log.d(TAG, "Đăng ký listener cho 'ReceiveMessage'");

        hubConnection.on("ReceiveMessage", (message) -> {
            Log.i(TAG, "Nhận được tin nhắn từ Hub: " + message.message);
            listener.accept(message);
        }, ChatMessage.class);
    }

    public void removeOnMessageReceivedListener() {
        if (hubConnection != null) {
            Log.d(TAG, "Gỡ đăng ký listener 'ReceiveMessage'");
            hubConnection.remove("ReceiveMessage");
        }
    }
}