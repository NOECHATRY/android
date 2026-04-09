package com.example.voisins_connectes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublierDemandeActivity extends AppCompatActivity {

    private EditText etTitre, etDescription, etCredits;
    private ChipGroup cgCategorie;
    private Button btnConfirmer;
    private Demande demandeAModifier;
    private boolean isEditMode = false;

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

        etTitre = findViewById(R.id.et_publier_titre);
        etDescription = findViewById(R.id.et_publier_description);
        etCredits = findViewById(R.id.et_publier_credits);
        cgCategorie = findViewById(R.id.cg_publier_categorie);
        btnConfirmer = findViewById(R.id.btn_publier_confirmer);

        // Vérifier si on est en mode modification
        demandeAModifier = (Demande) getIntent().getSerializableExtra("demande");
        if (demandeAModifier != null) {
            isEditMode = true;
            remplirChamps();
            btnConfirmer.setText("Modifier la demande");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Modifier ma demande");
            }
        }

        btnConfirmer.setOnClickListener(v -> {
            if (isEditMode) {
                validerEtModifier();
            } else {
                validerEtAjouter();
            }
        });
    }

    private void remplirChamps() {
        etTitre.setText(demandeAModifier.getTitre());
        etDescription.setText(demandeAModifier.getDescription());
        etCredits.setText(String.valueOf((int)demandeAModifier.getBudget()));
        
        // On essaye de cocher le bon chip par libellé si possible
        for (int i = 0; i < cgCategorie.getChildCount(); i++) {
            View child = cgCategorie.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (getCategoryId(chip.getText().toString()) == demandeAModifier.getIdCategorie()) {
                    chip.setChecked(true);
                    break;
                }
            }
        }
    }

    private void validerEtAjouter() {
        String titre = etTitre.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String creditsStr = etCredits.getText().toString().trim();
        
        int checkedId = cgCategorie.getCheckedChipId();
        if (checkedId == View.NO_ID || titre.isEmpty() || description.isEmpty() || creditsStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        double budget = Double.parseDouble(creditsStr);
        Chip selectedChip = findViewById(checkedId);
        int idCategorie = getCategoryId(selectedChip.getText().toString());

        SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        int idMembre = prefs.getInt("idMembre", -1);

        Demande nouvelleDemande = new Demande(titre, description, idCategorie, budget, idMembre, "public");
        nouvelleDemande.setEtatService("publiee");
        nouvelleDemande.setAdresseExecution("Non spécifiée");

        RetrofitClient.getApiService().addService(nouvelleDemande).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PublierDemandeActivity.this, "Demande publiée !", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PublierDemandeActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validerEtModifier() {
        String titre = etTitre.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String creditsStr = etCredits.getText().toString().trim();
        
        int checkedId = cgCategorie.getCheckedChipId();
        if (checkedId == View.NO_ID || titre.isEmpty() || description.isEmpty() || creditsStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        demandeAModifier.setTitre(titre);
        demandeAModifier.setDescription(description);
        demandeAModifier.setBudget(Double.parseDouble(creditsStr));
        Chip selectedChip = findViewById(checkedId);
        demandeAModifier.setIdCategorie(getCategoryId(selectedChip.getText().toString()));

        RetrofitClient.getApiService().updateService(demandeAModifier).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PublierDemandeActivity.this, "Demande modifiée !", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PublierDemandeActivity.this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PublierDemandeActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getCategoryId(String label) {
        String l = label.toLowerCase();
        if (l.contains("informatique")) return 1;
        if (l.contains("bricolage")) return 2;
        if (l.contains("cours")) return 3;
        if (l.contains("jardinage")) return 4;
        return 1;
    }
}
