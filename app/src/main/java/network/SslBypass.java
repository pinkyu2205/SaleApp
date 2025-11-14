package network;

import android.annotation.SuppressLint;
import android.util.Log;

import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

// (Lưu ý: Chỉ dùng cho Development/Debug. CỰC KỲ KHÔNG AN TOÀN cho Production)
public class SslBypass {

    private static final String TAG = "SslBypass";
    private static boolean isBypassApplied = false;

    @SuppressLint("TrulyRandom")
    public static void trustAllCertificates() {
        if (isBypassApplied) {
            return;
        }

        try {
            // 1. Tạo một TrustManager "ngây thơ" không kiểm tra gì cả
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                        @SuppressLint("TrustAllX509TrustManager")
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        @SuppressLint("TrustAllX509TrustManager")
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            // 2. Lấy SSLContext và khởi tạo nó với TrustManager ở trên
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // 3. Đặt nó làm SSLSocketFactory mặc định cho HttpsURLConnection
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // 4. Tạo một HostnameVerifier "ngây thơ" chấp nhận mọi tên miền
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // 5. Đặt nó làm HostnameVerifier mặc định
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            Log.i(TAG, "ĐÃ BYPASS SSL TOÀN HỆ THỐNG (CHỈ DÙNG CHO DEBUG)");
            isBypassApplied = true;

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi bypass SSL", e);
            throw new RuntimeException(e);
        }
    }
}