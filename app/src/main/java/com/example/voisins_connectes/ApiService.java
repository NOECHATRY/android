package com.example.voisins_connectes;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {
    @GET("membres/list") // On utilise le chemin fourni par l'utilisateur
    Call<List<Membre>> getMembres();

    @GET("membres/list")
    Call<Membre> getMembre(@Query("id") int id);

    @POST("membres/list")
    Call<Void> addMembre(@Body Membre membre);

    @PUT("membres/list")
    Call<Void> updateMembre(@Body Membre membre);

    @DELETE("membres/list")
    Call<Void> deleteMembre(@Query("id") int id);
}