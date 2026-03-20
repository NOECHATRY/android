package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AnnonceAdapter adapter;
    private List<Annonce> allAnnonces;
    private TextView tvBienvenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vérifier si l'utilisateur est connecté
        SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        if (!prefs.contains("username")) {
            // Pas de session -> redirection vers l'écran de login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Récupérer le nom de l'utilisateur
        String username = prefs.getString("username", "Voisin");

        // Mettre à jour le message de bienvenue
        tvBienvenue = findViewById(R.id.tv_Bienvenue);
        tvBienvenue.setText("Bonjour, " + username + " !");

        // Initialisation des données et de la vue
        initData();
        recyclerView = findViewById(R.id.rv_voisins);
        
        // Modification pour l'alignement de gauche à droite (Horizontal)
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        adapter = new AnnonceAdapter(new ArrayList<>(allAnnonces));
        recyclerView.setAdapter(adapter);

        // Gestion des catégories
        ChipGroup chipGroup = findViewById(R.id.chip_group_categories);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                filterAnnonces("Tout");
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chip_tout) filterAnnonces("Tout");
                else if (checkedId == R.id.chip_bricolage) filterAnnonces("Bricolage");
                else if (checkedId == R.id.chip_jardinage) filterAnnonces("Jardinage");
                else if (checkedId == R.id.chip_cours) filterAnnonces("Cours");
            }
        });

        // Gestion de la déconnexion
        Button btnConnexion = findViewById(R.id.btn_connexion);
        btnConnexion.setText("Déconnexion");
        btnConnexion.setOnClickListener(v -> {
            prefs.edit().remove("username").apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void initData() {
        allAnnonces = new ArrayList<>();
        allAnnonces.add(new Annonce(1, "Tonte de pelouse", "Je propose mes services pour tondre votre pelouse.", 15.0, "22/05/2024", "Jardinage"));
        allAnnonces.add(new Annonce(2, "Cours de Maths", "Soutien scolaire pour niveau collège.", 20.0, "21/05/2024", "Cours"));
        allAnnonces.add(new Annonce(3, "Aide déménagement", "Besoin de bras pour porter des cartons.", 10.0, "20/05/2024", "Bricolage"));
    }

    private void filterAnnonces(String categorie) {
        List<Annonce> filteredList = new ArrayList<>();
        if (categorie.equals("Tout")) {
            filteredList.addAll(allAnnonces);
        } else {
            for (Annonce a : allAnnonces) {
                if (a.getCategorie().equalsIgnoreCase(categorie)) {
                    filteredList.add(a);
                }
            }
        }
        adapter.setAnnonces(filteredList);
    }
}