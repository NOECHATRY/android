package com.example.voisins_connectes;

import com.example.voisins_connectes.models.Forfait;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiForfait {
    // FORFAITS
    @GET("forfaits/list")
    Call<List<Forfait>> getForfaits();
}
