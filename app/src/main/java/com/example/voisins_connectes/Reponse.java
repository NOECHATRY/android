package com.example.voisins_connectes;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Reponse implements Serializable {
    private int idReponse;
    private String message;
    private String dateReponse;
    private String statutReponse;
    @SerializedName("idMembre_offreur")
    private int idMembreOffreur;
    private int idService;

    public int getIdReponse() { return idReponse; }
    public String getMessage() { return message; }
    public String getStatutReponse() { return statutReponse; }
    public int getIdMembreOffreur() { return idMembreOffreur; }
    public int getIdService() { return idService; }
}
