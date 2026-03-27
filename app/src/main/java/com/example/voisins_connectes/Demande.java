package com.example.voisins_connectes;

import java.io.Serializable;

public class Demande implements Serializable {
    private String titre;
    private String description;
    private String categorie;
    private String budget;
    private String statut; // "En attente", "Validée", etc.
    private String reponse;

    public Demande(String titre, String description, String categorie, String budget) {
        this.titre = titre;
        this.description = description;
        this.categorie = categorie;
        this.budget = budget;
        this.statut = "En attente";
        this.reponse = "Aucune réponse pour le moment.";
    }

    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getCategorie() { return categorie; }
    public String getBudget() { return budget; }
    public String getStatut() { return statut; }
    public String getReponse() { return reponse; }
    
    public void setStatut(String statut) { this.statut = statut; }
    public void setReponse(String reponse) { this.reponse = reponse; }
}
