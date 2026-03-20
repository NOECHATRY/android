package com.example.voisins_connectes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Annonce implements Serializable {
    private int idAnnonce;
    private String titre;
    private String description;
    private double prix;
    private String datePublication;
    private String categorie;
    private String localisation;
    private String duree;
    private String userName;
    private float userRating;
    private int nbAvis; // Nombre total d'avis
    private List<Avis> avis;

    public Annonce(int idAnnonce, String titre, String description, double prix, String datePublication, String categorie, String localisation, String duree, String userName, float userRating) {
        this.idAnnonce = idAnnonce;
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.datePublication = datePublication;
        this.categorie = categorie;
        this.localisation = localisation;
        this.duree = duree;
        this.userName = userName;
        this.userRating = userRating;
        this.nbAvis = 0; // Initialisé à 0 par défaut
        this.avis = new ArrayList<>();
    }

    public Annonce(int idAnnonce, String titre, String description, double prix, String datePublication, String categorie) {
        this(idAnnonce, titre, description, prix, datePublication, categorie, "Lille", "1h", "Jean Dupont", 0.0f);
    }

    public void addAvis(Avis nouvelAvis) {
        if (this.avis == null) this.avis = new ArrayList<>();
        
        // Calcul de la nouvelle moyenne
        float totalRating = this.userRating * this.nbAvis;
        this.nbAvis++;
        this.userRating = (totalRating + nouvelAvis.getNote()) / this.nbAvis;
        
        this.avis.add(0, nouvelAvis);
    }

    // Getters
    public int getIdAnnonce() { return idAnnonce; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public double getPrix() { return prix; }
    public String getDatePublication() { return datePublication; }
    public String getCategorie() { return categorie; }
    public String getLocalisation() { return localisation; }
    public String getDuree() { return duree; }
    public String getUserName() { return userName; }
    public float getUserRating() { return userRating; }
    public int getNbAvis() { return nbAvis; }
    public List<Avis> getAvis() { return avis; }
    public void setAvis(List<Avis> avis) { this.avis = avis; }
}