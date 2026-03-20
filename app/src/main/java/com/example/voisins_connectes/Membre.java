package com.example.voisins_connectes;

import com.google.gson.annotations.SerializedName;

public class Membre {
    private int idMembre;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String adresse;
    private String dateInscription;
    private String role;

    // Constructeur vide nécessaire pour Gson
    public Membre() {}

    // Getters et Setters
    public int getIdMembre() { return idMembre; }
    public void setIdMembre(int idMembre) { this.idMembre = idMembre; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getDateInscription() { return dateInscription; }
    public void setDateInscription(String dateInscription) { this.dateInscription = dateInscription; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}