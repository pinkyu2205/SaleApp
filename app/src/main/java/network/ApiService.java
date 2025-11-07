package network;

import java.util.List;

import network.models.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT; // Thêm PUT
import retrofit2.http.DELETE; // Thêm DELETE
import retrofit2.http.Path; // Thêm Path
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/Auth/login")
    Call<LoginResponse> login(@Body LoginRequest body);

    @POST("api/Auth/register")
    Call<Void> register(@Body RegisterRequest body);

    @GET("api/Products")
    Call<List<ProductListItem>> getProducts(
            @Query("categoryId") Integer categoryId,
            @Query("minPrice") Double minPrice
    );

    @GET("api/User/me")
    Call<UserProfile> getMyProfile(@Header("Authorization") String bearerToken);

    // === Product ===
    @GET("api/products")
    Call<List<ProductListItem>> getProducts(
            @Query("categoryId") Integer categoryId,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice, // Sửa đổi
            @Query("sortBy") String sortBy      // Sửa đổi
    );

    @GET("api/products/{id}")
    Call<ProductDetail> getProductDetail(@Path("id") int productId); // Mới


    // === Cart ===
    // (Lưu ý: Các API này yêu cầu Header Authorization)

    @POST("api/cart/items")
    Call<Cart> addItemToCart(
            @Header("Authorization") String bearerToken,
            @Body AddCartItemRequest body
    ); // Mới

    @PUT("api/cart/items/{id}")
    Call<Cart> updateCartItem(
            @Header("Authorization") String bearerToken,
            @Path("id") int cartItemId,
            @Body UpdateCartItemRequest body
    ); // Mới

    @DELETE("api/cart/items/{id}")
    Call<Cart> removeCartItem(
            @Header("Authorization") String bearerToken,
            @Path("id") int cartItemId
    ); // Mới
}
