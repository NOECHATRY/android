package com.example.voisins_connectes;

import java.util.ArrayList;
import java.util.List;

public class GestionDemandes {
    private static List<Demande> listeDemandes = new ArrayList<>();

    static {
        // Ajout de demandes de test pour peupler l'application
        listeDemandes.add(new Demande("Besoin d'aide pour monter un meuble", "J'ai acheté une armoire IKEA mais je n'arrive pas à la monter seul.", "Bricolage", "10", "Marc Durand"));
        listeDemandes.add(new Demande("Cherche professeur de piano", "Je souhaite débuter le piano, niveau grand débutant.", "Cours", "20", "Julie Moreau"));
        listeDemandes.add(new Demande("Taille de haie", "Ma haie de jardin a besoin d'un bon rafraîchissement.", "Jardinage", "15", "Thomas Bernard"));
        listeDemandes.add(new Demande("Réparation vélo", "Mon pneu est crevé et mes freins grincent.", "Bricolage", "5", "Antoine Petit"));
        listeDemandes.add(new Demande("Soutien scolaire Anglais", "Besoin d'aide pour mon fils en classe de 4ème.", "Cours", "12", "Emma Lefebvre"));
        listeDemandes.add(new Demande("Aide potager", "Je voudrais planter des tomates et des salades.", "Jardinage", "8", "Sophie Martin"));
        listeDemandes.add(new Demande("Peinture volets", "J'ai 4 paires de volets à repeindre en blanc.", "Bricolage", "25", "Lucas Roux"));
    }

    public static void ajouterDemande(Demande d) {
        listeDemandes.add(0, d); // Ajouter en haut de liste
    }

    public static List<Demande> getListeDemandes() {
        return listeDemandes;
    }
}
