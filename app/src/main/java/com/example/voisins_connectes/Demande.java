package com.example.voisins_connectes;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Demande implements Serializable {
    @SerializedName("idService")
    private int idDemande;
    
    private String titre;
    private String description;
    private String datePublication;
    private String typeService; // 'offre' or 'demande'
    private String etatService; // 'open', 'validee', 'depubliee'
    private String adresseExecution;
    
    @SerializedName("prixPropose")
    private double budget;
    
    private String dateCloture;
    private int idCategorie;
    
    @SerializedName("idMembre_demandeur")
    private int idMembre;
    
    // Champs pour l'affichage local
    private String auteur; 
    private String categorie;
    private String reponse;
    private String aideur;
    private String statut; // Pour la compatibilité locale

    public Demande(String titre, String description, int idCategorie, double budget, int idMembre, String typeService) {
        this.titre = titre;
        this.description = description;
        this.idCategorie = idCategorie;
        this.budget = budget;
        this.idMembre = idMembre;
        this.typeService = typeService;
        this.etatService = "open";
        this.statut = "En attente";
    }

    // Constructeur pour les données de test (GestionDemandes)
    public Demande(String titre, String description, String categorie, String budget, String auteur) {
        this.titre = titre;
        this.description = description;
        this.categorie = categorie;
        try {
            this.budget = Double.parseDouble(budget);
        } catch (NumberFormatException e) {
            this.budget = 0.0;
        }
        this.auteur = auteur;
        this.typeService = "demande";
        this.etatService = "open";
        this.statut = "En attente";
        this.reponse = "Aucune réponse pour le moment.";
    }

    // Getters and Setters
    public int getIdDemande() { return idDemande; }
    public void setIdDemande(int idDemande) { this.idDemande = idDemande; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDatePublication() { return datePublication; }
    public void setDatePublication(String datePublication) { this.datePublication = datePublication; }

    public String getTypeService() { return typeService; }
    public void setTypeService(String typeService) { this.typeService = typeService; }

    public String getEtatService() { return etatService; }
    public void setEtatService(String etatService) { this.etatService = etatService; }

    public String getAdresseExecution() { return adresseExecution; }
    public void setAdresseExecution(String adresseExecution) { this.adresseExecution = adresseExecution; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public String getDateCloture() { return dateCloture; }
    public void setDateCloture(String dateCloture) { this.dateCloture = dateCloture; }

    public int getIdCategorie() { return idCategorie; }
    public void setIdCategorie(int idCategorie) { this.idCategorie = idCategorie; }

    public int getIdMembre() { return idMembre; }
    public void setIdMembre(int idMembre) { this.idMembre = idMembre; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }

    public String getAideur() { return aideur; }
    public void setAideur(String aideur) { this.aideur = aideur; }

    public String getStatut() { return statut != null ? statut : "En attente"; }
    public void setStatut(String statut) { this.statut = statut; }
}
