package network;

import java.util.List;

import network.models.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/Auth/login")
    Call<LoginResponse> login(@Body LoginRequest body);

    @POST("api/Auth/register")
    Call<Void> register(@Body RegisterRequest body);

    @GET("api/products")
    Call<List<ProductListItem>> getProducts(
            @Query("categoryId") Integer categoryId,
            @Query("minPrice") Double minPrice
    );

    @GET("api/user/me")
    Call<UserProfile> getMyProfile(@Header("Authorization") String bearerToken);
}
