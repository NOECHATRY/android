package com.example.voisins_connectes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class PublierDemandeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publier_demande);

        Toolbar toolbar = findViewById(R.id.toolbar_publier);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        EditText etTitre = findViewById(R.id.et_publier_titre);
        EditText etDescription = findViewById(R.id.et_publier_description);
        EditText etCredits = findViewById(R.id.et_publier_credits);
        ChipGroup cgCategorie = findViewById(R.id.cg_publier_categorie);
        Button btnConfirmer = findViewById(R.id.btn_publier_confirmer);

        btnConfirmer.setOnClickListener(v -> {
            String titre = etTitre.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String credits = etCredits.getText().toString().trim();
            
            int checkedId = cgCategorie.getCheckedChipId();
            String categorie = "";
            if (checkedId != View.NO_ID) {
                Chip chip = findViewById(checkedId);
                categorie = chip.getText().toString();
            }

            if (titre.isEmpty() || description.isEmpty() || credits.isEmpty() || categorie.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs et choisir une catégorie", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sauvegarder la demande dans notre gestionnaire global
            Demande nouvelleDemande = new Demande(titre, description, categorie, credits);
            GestionDemandes.ajouterDemande(nouvelleDemande);

            Toast.makeText(this, "Votre demande a été publiée !", Toast.LENGTH_LONG).show();
            finish(); // Retourner à l'accueil
        });
    }
}