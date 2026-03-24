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
    private int nbAvis;
    private List<Avis> avis;
    private List<String> horairesDisponibles;
    private String horaireReserve;

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
        this.nbAvis = 0;
        this.avis = new ArrayList<>();
        this.horairesDisponibles = new ArrayList<>();
        
        // Horaires par défaut pour tous les jours de la semaine
        this.horairesDisponibles.add("Lundi 14:00");
        this.horairesDisponibles.add("Mardi 10:00");
        this.horairesDisponibles.add("Mercredi 10:00");
        this.horairesDisponibles.add("Mercredi 15:00");
        this.horairesDisponibles.add("Jeudi 18:00");
        this.horairesDisponibles.add("Vendredi 16:30");
        this.horairesDisponibles.add("Samedi 09:00");
        this.horairesDisponibles.add("Dimanche 11:00");
    }

    public Annonce(int idAnnonce, String titre, String description, double prix, String datePublication, String categorie) {
        this(idAnnonce, titre, description, prix, datePublication, categorie, "Lille", "1h", "Jean Dupont", 0.0f);
    }

    public void addAvis(Avis nouvelAvis) {
        if (this.avis == null) this.avis = new ArrayList<>();
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
    public String userName() { return userName; }
    public String getUserName() { return userName; }
    public float getUserRating() { return userRating; }
    public int getNbAvis() { return nbAvis; }
    public List<Avis> getAvis() { return avis; }
    public List<String> getHorairesDisponibles() { return horairesDisponibles; }
    public String getHoraireReserve() { return horaireReserve; }
    
    // Setters
    public void setAvis(List<Avis> avis) { this.avis = avis; }
    public void setHorairesDisponibles(List<String> horaires) { this.horairesDisponibles = horaires; }
    public void setHoraireReserve(String horaire) { this.horaireReserve = horaire; }
}
