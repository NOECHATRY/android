package com.example.voisins_connectes;

import com.example.voisins_connectes.models.Membre;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiMembre {
    // MEMBRES
    @GET("membres/list")
    Call<List<Membre>> getMembres();

    @POST("membres/add")
    Call<ResponseBody> addMembre(@Body Membre membre);

    @FormUrlEncoded
    @POST("membres/login")
    Call<Membre> login(
            @Query("action") String action,
            @Field("email") String email,
            @Field("motDePasse") String motDePasse
    );
}
