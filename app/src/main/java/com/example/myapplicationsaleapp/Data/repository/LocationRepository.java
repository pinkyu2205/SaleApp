package com.example.myapplicationsaleapp.Data.repository;

import com.example.myapplicationsaleapp.Data.model.StoreLocation;
import java.util.ArrayList;
import java.util.List;

public class LocationRepository {

    public interface LocationCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    /**
     * Lấy danh sách tất cả cửa hàng
     * TODO: Thay thế bằng API call thực tế
     */
    public void getAllStoreLocations(LocationCallback<List<StoreLocation>> callback) {
        // Simulate API call delay
        new Thread(() -> {
            try {
                Thread.sleep(500); // Simulate network delay
                
                // Dữ liệu mẫu - các cửa hàng ở HCM
                List<StoreLocation> stores = new ArrayList<>();
                stores.add(new StoreLocation(
                    "Cửa hàng Quận 1",
                    "123 Nguyễn Huệ, Quận 1, TP.HCM",
                    "8:00 - 22:00",
                    "028-1234-5678",
                    10.7769, 106.7009
                ));
                stores.add(new StoreLocation(
                    "Cửa hàng Quận 3",
                    "456 Võ Văn Tần, Quận 3, TP.HCM",
                    "8:00 - 21:00",
                    "028-2345-6789",
                    10.7833, 106.6900
                ));
                stores.add(new StoreLocation(
                    "Cửa hàng Quận 7",
                    "789 Nguyễn Thị Thập, Quận 7, TP.HCM",
                    "9:00 - 22:00",
                    "028-3456-7890",
                    10.7300, 106.7200
                ));

                // Return on main thread
                android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
                handler.post(() -> callback.onSuccess(stores));
            } catch (InterruptedException e) {
                android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
                handler.post(() -> callback.onError("Lỗi khi tải dữ liệu: " + e.getMessage()));
            }
        }).start();
    }
}

