package com.example.voisins_connectes;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // --- MEMBRES ---
    @POST("membres.php")
    Call<LoginResponse> login(
            @Query("action") String action,
            @Body Map<String, String> credentials
    );

    @POST("membres.php")
    Call<Void> addMembre(@Body Membre membre);

    @GET("membres.php")
    Call<List<Membre>> getMembres();

    // --- CATEGORIES ---
    @GET("categories.php")
    Call<List<Categorie>> getCategories();

    // --- FORFAITS ---
    @GET("forfaits.php")
    Call<List<Forfait>> getForfaits();

    @POST("forfaits.php")
    Call<Void> chooseForfait(
            @Query("action") String action,
            @Body Map<String, Integer> data
    );

    // --- SERVICES ---
    @GET("services.php")
    Call<List<Demande>> getServices();

    @POST("services.php")
    Call<Void> addService(@Body Demande demande);

    // --- REPONSES ---
    @POST("reponses.php")
    Call<Void> addReponse(@Body Map<String, Object> data);

    @POST("reponses.php")
    Call<Void> acceptReponse(
            @Query("action") String action,
            @Body Map<String, Integer> data
    );
}
