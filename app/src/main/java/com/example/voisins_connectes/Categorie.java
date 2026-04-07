package com.example.voisins_connectes;

import java.io.Serializable;

public class Categorie implements Serializable {
    private int idCategorie;
    private String libelle;
    private String theme;

    public int getIdCategorie() { return idCategorie; }
    public void setIdCategorie(int idCategorie) { this.idCategorie = idCategorie; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
}
