package com.example.myapplicationsaleapp.utils;

import android.view.Menu;
import android.view.View;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    
    /**
     * Xóa badge khỏi menu item
     * @param bottomNav BottomNavigationView
     * @param menuItemId ID của menu item
     */
    public static void removeBadge(@NonNull BottomNavigationView bottomNav, @IdRes int menuItemId) {
        bottomNav.removeBadge(menuItemId);
    }
}

