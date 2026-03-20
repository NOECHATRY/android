package com.example.voisins_connectes;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AnnonceAdapter adapter;
    private List<Annonce> allAnnonces; // Liste complète pour le filtrage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialisation des données
        initData();

        // 2. Initialisation de la RecyclerView
        recyclerView = findViewById(R.id.rv_voisins);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnnonceAdapter(new ArrayList<>(allAnnonces));
        recyclerView.setAdapter(adapter);

        // 3. Gestion des catégories (Chips)
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

        // 4. Autres boutons
        Button btnInscription = findViewById(R.id.btn_inscription);
        Button btnConnexion = findViewById(R.id.btn_connexion);

        btnInscription.setOnClickListener(v -> { /* Action */ });
        btnConnexion.setOnClickListener(v -> { /* Action */ });
    }

    private void initData() {
        allAnnonces = new ArrayList<>();
        allAnnonces.add(new Annonce(1, "Tonte de pelouse", "Je propose mes services pour tondre votre pelouse.", 15.0, "22/05/2024", "Jardinage"));
        allAnnonces.add(new Annonce(2, "Cours de Maths", "Soutien scolaire pour niveau collège.", 20.0, "21/05/2024", "Cours"));
        allAnnonces.add(new Annonce(3, "Aide déménagement", "Besoin de bras pour porter des cartons.", 10.0, "20/05/2024", "Bricolage"));
        allAnnonces.add(new Annonce(4, "Peinture salon", "Aide pour repeindre un mur de 10m2.", 30.0, "19/05/2024", "Bricolage"));
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