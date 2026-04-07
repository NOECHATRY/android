package com.example.voisins_connectes;

import java.io.Serializable;

public class Demande implements Serializable {
    private String titre;
    private String description;
    private String categorie;
    private String budget;
    private String statut; // "En attente", "Validée", etc.
    private String reponse;
    private String auteur; // Nom de la personne qui a publié
    private String aideur; // Nom de la personne qui a accepté d'aider

    public Demande(String titre, String description, String categorie, String budget, String auteur) {
        this.titre = titre;
        this.description = description;
        this.categorie = categorie;
        this.budget = budget;
        this.auteur = auteur;
        this.statut = "En attente";
        this.reponse = "Aucune réponse pour le moment.";
        this.aideur = null;
    }

    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getCategorie() { return categorie; }
    public String getBudget() { return budget; }
    public String getStatut() { return statut; }
    public String getReponse() { return reponse; }
    public String getAuteur() { return auteur; }
    public String getAideur() { return aideur; }
    
    public void setStatut(String statut) { this.statut = statut; }
    public void setReponse(String reponse) { this.reponse = reponse; }
    public void setAideur(String aideur) { this.aideur = aideur; }
}
