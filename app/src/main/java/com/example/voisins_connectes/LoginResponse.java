package com.example.voisins_connectes;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("infos_membre")
    private Membre infosMembre;

    @SerializedName("error")
    private String error;

    public String getToken() { return token; }
    public Membre getInfosMembre() { return infosMembre; }
    public String getError() { return error; }
}