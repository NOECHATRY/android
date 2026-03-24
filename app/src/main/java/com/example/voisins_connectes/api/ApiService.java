package com.example.voisins_connectes.api;

import com.example.voisins_connectes.models.*;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // SERVICES
    @GET("services/list")
    Call<List<Service>> getServices();

    @POST("services/add")
    Call<ResponseBody> addService(@Body Service service);

    @PUT("services/update/{id}")
    Call<ResponseBody> updateService(@Path("id") int id, @Body Service service);

    @DELETE("services/delete/{id}")
    Call<ResponseBody> deleteService(@Path("id") int id);

    // MEMBRES
    @GET("membres/list")
    Call<List<Membre>> getMembres();

    @POST("membres/add")
    Call<ResponseBody> addMembre(@Body Membre membre);

    // FORFAITS
    @GET("forfaits/list")
    Call<List<Forfait>> getForfaits();
}