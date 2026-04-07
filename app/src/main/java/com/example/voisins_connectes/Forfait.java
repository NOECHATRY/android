package com.example.voisins_connectes;

import java.io.Serializable;

public class Forfait implements Serializable {
    private int idForfait;
    private String nomForfait;
    private String description;
    private double prix;
    private int duree;
    
    // Fonctionnalités
    private int fonc_parametrage;
    private int fonc_perimetreIntervention;
    private int fonc_notifications;
    private int fonc_locationMateriel;
    private int fonc_prestationService;
    private int fonc_numeroProfil;
    private int fonc_photosProfil;
    private int fonc_accompagnementPrioritaire;

    // Getters and Setters
    public int getIdForfait() { return idForfait; }
    public void setIdForfait(int idForfait) { this.idForfait = idForfait; }
    public String getNomForfait() { return nomForfait; }
    public void setNomForfait(String nomForfait) { this.nomForfait = nomForfait; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }
}
