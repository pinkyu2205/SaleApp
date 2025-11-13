package com.example.myapplicationsaleapp.utils;

import android.view.Menu;
import android.view.View;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.myapplicationsaleapp.MainActivity; // Import channel ID
import com.example.myapplicationsaleapp.R;

public class BadgeUtils {
    
    /**
     * Hiển thị badge với số lượng trên BottomNavigationView item
     * @param bottomNav BottomNavigationView
     * @param menuItemId ID của menu item (ví dụ: R.id.cartFragment)
     * @param count Số lượng hiển thị (0 sẽ ẩn badge)
     */
    public static void showBadge(@NonNull BottomNavigationView bottomNav, @IdRes int menuItemId, int count) {
        BadgeDrawable badge = bottomNav.getOrCreateBadge(menuItemId);
        if (count > 0) {
            badge.setVisible(true);
            badge.setNumber(count);
        } else {
            badge.setVisible(false);
        }
    }
    
    /**
     * Ẩn badge trên menu item
     * @param bottomNav BottomNavigationView
     * @param menuItemId ID của menu item
     */
    public static void hideBadge(@NonNull BottomNavigationView bottomNav, @IdRes int menuItemId) {
        BadgeDrawable badge = bottomNav.getBadge(menuItemId);
        if (badge != null) {
            badge.setVisible(false);
        }
    }

    // (Giữ nguyên hàm updateCartBadgeInMenu của bạn)

    // === THÊM PHƯƠNG THỨC MỚI NÀY VÀO ===
    /**
     * Cập nhật badge trên icon của app bằng cách gửi một notification.
     * @param context Context
     * @param itemCount Số lượng item. Nếu là 0, badge sẽ bị hủy.
     */
    public static void updateAppIconBadge(Context context, int itemCount) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int NOTIFICATION_ID = 1; // ID cố định để cập nhật/hủy

        if (itemCount > 0) {
            // Xây dựng notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CART_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_cart) // Thay bằng icon của bạn
                    .setContentTitle("Giỏ hàng của bạn")
                    .setContentText("Bạn có " + itemCount + " sản phẩm trong giỏ hàng.")
                    .setNumber(itemCount) // Đây là mấu chốt để hiển thị badge
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL) // Kiểu badge
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // Hiển thị notification (và badge)
            // (Bạn cần xin quyền POST_NOTIFICATIONS ở AndroidManifest cho API 33+)
            try {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            } catch (SecurityException e) {
                // Xảy ra nếu người dùng chưa cấp quyền (Android 13+)
                e.printStackTrace();
            }

        } else {
            // Nếu giỏ hàng rỗng, hủy notification (và badge)
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }
    
    /**
     * Xóa badge khỏi menu item
     * @param bottomNav BottomNavigationView
     * @param menuItemId ID của menu item
     */
    public static void removeBadge(@NonNull BottomNavigationView bottomNav, @IdRes int menuItemId) {
        bottomNav.removeBadge(menuItemId);
    }
}

