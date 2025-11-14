package com.example.myapplicationsaleapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.myapplicationsaleapp.shared.CartViewModel;
import com.example.myapplicationsaleapp.utils.BadgeUtils;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationBarView;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private CartViewModel cartViewModel;
    public static final String CART_CHANNEL_ID = "CART_CHANNEL";
    private static final int ADMIN_USER_ID = 9;
    private static final String ADMIN_USERNAME = "Nhân viên Hỗ trợ";
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1) Gắn đúng layout chứa nav_host_fragment
        setContentView(R.layout.activity_main);

        // 2) Toolbar (nếu có trong activity_main)
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 3) Lấy NavController qua NavHostFragment (an toàn)
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found. Check activity_main.xml");
        }
        NavController navController = navHostFragment.getNavController();

        // 4) Cấu hình top-level destinations cho BottomNavigationView
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(
                        R.id.productListFragment,
                        R.id.cartFragment,
                        R.id.mapFragment,
                        R.id.chatListFragment
                ).build();

        // 5) Liên kết Toolbar với Navigation
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // 6) Liên kết BottomNavigationView với NavController
        bottomNav = findViewById(R.id.bottomNavigationView);
//        NavigationUI.setupWithNavController(bottomNav, navController);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.chatListFragment) {
                    // KHI BẤM CHAT: Điều hướng thủ công đến ConversationFragment
                    Bundle args = new Bundle();
                    args.putInt("otherUserId", ADMIN_USER_ID);
                    args.putString("otherUsername", ADMIN_USERNAME);

                    // Đảm bảo không điều hướng nếu đã ở trang chat
                    if(navController.getCurrentDestination().getId() != R.id.conversationFragment){
                        navController.navigate(R.id.conversationFragment, args);
                    }
                    return true;
                }

                // Các nút khác (Products, Cart, Map) dùng logic tự động của NavigationUI
                return NavigationUI.onNavDestinationSelected(item, navController);
            }
        });

        // 7) Ẩn/hiện BottomNavigationView dựa trên destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFragment ||
                    destination.getId() == R.id.signupFragment) {
                bottomNav.setVisibility(View.GONE);
            } else {
                bottomNav.setVisibility(View.VISIBLE);
            }

            // THÊM MỚI: Khi điều hướng, tự động chọn item trên taskbar
            if (destination.getId() == R.id.productListFragment ||
                    destination.getId() == R.id.cartFragment ||
                    destination.getId() == R.id.mapFragment) {
                bottomNav.getMenu().findItem(destination.getId()).setChecked(true);
            }
            // Nếu vào trang chat, cũng tự chọn tab chat
            if (destination.getId() == R.id.conversationFragment) {
                bottomNav.getMenu().findItem(R.id.chatListFragment).setChecked(true);
            }
        });
        createNotificationChannel();
        // 8) Thiết lập CartViewModel để cập nhật badge
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartViewModel.state.observe(this, cartState ->
                BadgeUtils.showBadge(bottomNav, R.id.cartFragment, cartViewModel.count()));
        cartViewModel.state.observe(this, cartState -> {
            int totalItems = cartViewModel.count();

            BadgeUtils.showBadge(bottomNav, R.id.cartFragment, totalItems);

            BadgeUtils.updateAppIconBadge(this, totalItems);
        });
    }
    private void createNotificationChannel() {
        // Chỉ tạo channel trên API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Giỏ hàng"; // Tên channel
            String description = "Thông báo về giỏ hàng"; // Mô tả
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CART_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Bật tính năng badge cho channel này
            channel.setShowBadge(true);

            // Đăng ký channel với hệ thống
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
