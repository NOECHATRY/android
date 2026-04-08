package com.example.voisins_connectes;

import java.util.ArrayList;
import java.util.List;

public class GestionDemandes {
    private static List<Demande> listeDemandes = new ArrayList<>();

    public static void ajouterDemande(Demande d) {
        listeDemandes.add(0, d); // Ajouter en haut de liste
    }

    public static List<Demande> getListeDemandes() {
        return listeDemandes;
    }
}
