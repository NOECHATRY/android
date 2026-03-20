package com.example.voisins_connectes;

import java.io.Serializable;

public class Avis implements Serializable {
    private String userName;
    private String commentaire;
    private float note;
    private String date;

    public Avis(String userName, String commentaire, float note, String date) {
        this.userName = userName;
        this.commentaire = commentaire;
        this.note = note;
        this.date = date;
    }

    public String getUserName() { return userName; }
    public String getCommentaire() { return commentaire; }
    public float getNote() { return note; }
    public String getDate() { return date; }
}