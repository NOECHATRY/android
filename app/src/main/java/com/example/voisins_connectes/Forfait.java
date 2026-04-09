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

    public int getFonc_parametrage() { return fonc_parametrage; }
    public void setFonc_parametrage(int fonc_parametrage) { this.fonc_parametrage = fonc_parametrage; }

    public int getFonc_perimetreIntervention() { return fonc_perimetreIntervention; }
    public void setFonc_perimetreIntervention(int fonc_perimetreIntervention) { this.fonc_perimetreIntervention = fonc_perimetreIntervention; }

    public int getFonc_notifications() { return fonc_notifications; }
    public void setFonc_notifications(int fonc_notifications) { this.fonc_notifications = fonc_notifications; }

    public int getFonc_locationMateriel() { return fonc_locationMateriel; }
    public void setFonc_locationMateriel(int fonc_locationMateriel) { this.fonc_locationMateriel = fonc_locationMateriel; }

    public int getFonc_prestationService() { return fonc_prestationService; }
    public void setFonc_prestationService(int fonc_prestationService) { this.fonc_prestationService = fonc_prestationService; }

    public int getFonc_numeroProfil() { return fonc_numeroProfil; }
    public void setFonc_numeroProfil(int fonc_numeroProfil) { this.fonc_numeroProfil = fonc_numeroProfil; }

    public int getFonc_photosProfil() { return fonc_photosProfil; }
    public void setFonc_photosProfil(int fonc_photosProfil) { this.fonc_photosProfil = fonc_photosProfil; }

    public int getFonc_accompagnementPrioritaire() { return fonc_accompagnementPrioritaire; }
    public void setFonc_accompagnementPrioritaire(int fonc_accompagnementPrioritaire) { this.fonc_accompagnementPrioritaire = fonc_accompagnementPrioritaire; }
}
