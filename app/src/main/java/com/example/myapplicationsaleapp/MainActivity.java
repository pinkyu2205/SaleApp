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

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private CartViewModel cartViewModel;

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
                        R.id.mapFragment
                ).build();

        // 5) Liên kết Toolbar với Navigation
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // 6) Liên kết BottomNavigationView với NavController
        bottomNav = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // 7) Ẩn/hiện BottomNavigationView dựa trên destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Ẩn BottomNavigationView ở màn hình đăng nhập và đăng ký
            if (destination.getId() == R.id.loginFragment || 
                destination.getId() == R.id.signupFragment) {
                bottomNav.setVisibility(android.view.View.GONE);
            } else {
                // Hiện BottomNavigationView ở các màn hình khác
                bottomNav.setVisibility(android.view.View.VISIBLE);
            }
        });

        // 8) Thiết lập CartViewModel để cập nhật badge
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartViewModel.state.observe(this, cartState ->
                BadgeUtils.showBadge(bottomNav, R.id.cartFragment, cartViewModel.count()));
    }

    @Override public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
