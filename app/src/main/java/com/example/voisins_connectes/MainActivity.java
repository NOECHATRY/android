package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ChipGroup chipGroup;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout containerMesDemandes;
    private SharedPreferences prefs;
    private int monId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        if (!prefs.contains("token")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        monId = prefs.getInt("idMembre", -1);

        setContentView(R.layout.activity_main);

        chipGroup = findViewById(R.id.chip_group_categories);
        TextView tvBienvenue = findViewById(R.id.tv_Bienvenue);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        containerMesDemandes = findViewById(R.id.container_mes_demandes_accueil);
        FloatingActionButton fabPublier = findViewById(R.id.fab_publier);

        tvBienvenue.setText("Bonjour, " + prefs.getString("username", "Voisin") + " !");

        chargerCategoriesAPI();
        chargerDonneesAPI();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            chargerCategoriesAPI();
            chargerDonneesAPI();
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true;
            if (itemId == R.id.nav_requests) {
                startActivity(new Intent(this, DemandesVoisinsActivity.class));
                return true;
            }
            if (itemId == R.id.nav_premium) {
                startActivity(new Intent(this, AbonnementActivity.class));
                return true;
            }
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilActivity.class));
                return true;
            }
            return false;
        });

        fabPublier.setOnClickListener(v -> startActivity(new Intent(this, PublierDemandeActivity.class)));

        findViewById(R.id.btn_connexion).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void chargerCategoriesAPI() {
        RetrofitClient.getApiService().getCategories().enqueue(new Callback<List<Categorie>>() {
            @Override
            public void onResponse(Call<List<Categorie>> call, Response<List<Categorie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chipGroup.removeAllViews();
                    ajouterChip("Tout", true);
                    for (Categorie cat : response.body()) {
                        ajouterChip(cat.getLibelle(), false);
                    }
                }
            }
            @Override public void onFailure(Call<List<Categorie>> call, Throwable t) {}
        });
    }

    private void ajouterChip(String label, boolean isDefault) {
        Chip chip = new Chip(this, null, com.google.android.material.R.attr.chipStyle);
        chip.setText(label);
        chip.setCheckable(true);
        if (isDefault) chip.setChecked(true);
        chipGroup.addView(chip);
    }

    private void chargerDonneesAPI() {
        swipeRefreshLayout.setRefreshing(true);
        RetrofitClient.getApiService().getServices().enqueue(new Callback<List<Demande>>() {
            @Override
            public void onResponse(Call<List<Demande>> call, Response<List<Demande>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GestionDemandes.getListeDemandes().clear();
                    GestionDemandes.getListeDemandes().addAll(response.body());
                    refreshMesDemandes();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
            @Override public void onFailure(Call<List<Demande>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void refreshMesDemandes() {
        containerMesDemandes.removeAllViews();
        List<Demande> liste = GestionDemandes.getListeDemandes();
        boolean hasDemandes = false;

        for (Demande d : liste) {
            if (d.getIdMembre() == monId) {
                hasDemandes = true;
                ajouterCardMesDemandes(d);
            }
        }

        if (!hasDemandes) {
            TextView tv = new TextView(this);
            tv.setText("Vous n'avez pas encore publié de demande.");
            tv.setPadding(0, 20, 0, 20);
            containerMesDemandes.addView(tv);
        }
    }

    private void ajouterCardMesDemandes(Demande d) {
        View cardView = getLayoutInflater().inflate(R.layout.item_demande_card, containerMesDemandes, false);
        TextView titre = cardView.findViewById(R.id.tv_demande_titre);
        TextView budget = cardView.findViewById(R.id.tv_demande_budget);
        ImageView iv = cardView.findViewById(R.id.iv_demande_image);

        titre.setText(d.getTitre());
        budget.setText(d.getBudget() + " crédits • Statut : " + d.getEtatService());
        
        if (d.getIdCategorie() == 1) iv.setImageResource(R.drawable.informatique);
        else if (d.getIdCategorie() == 2) iv.setImageResource(R.drawable.bricolage);
        else if (d.getIdCategorie() == 3) iv.setImageResource(R.drawable.cours);
        else if (d.getIdCategorie() == 4) iv.setImageResource(R.drawable.jardinage);

        cardView.setOnClickListener(v -> showOptionsDemande(d));
        containerMesDemandes.addView(cardView);
    }

    private void showOptionsDemande(Demande d) {
        String[] options = {"Modifier", "Supprimer", "Fermer"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(d.getTitre());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent intent = new Intent(this, PublierDemandeActivity.class);
                intent.putExtra("demande", d);
                startActivity(intent);
            } else if (which == 1) {
                confirmerSuppression(d);
            }
        });
        builder.show();
    }

    private void confirmerSuppression(Demande d) {
        new AlertDialog.Builder(this)
            .setTitle("Supprimer la demande")
            .setMessage("Voulez-vous vraiment supprimer définitivement cette demande ?")
            .setPositiveButton("Supprimer", (dialog, which) -> {
                Map<String, Integer> data = new HashMap<>();
                data.put("id", d.getIdDemande());
                
                RetrofitClient.getApiService().deleteService(data).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Demande supprimée", Toast.LENGTH_SHORT).show();
                            chargerDonneesAPI();
                        } else {
                            Toast.makeText(MainActivity.this, "Erreur suppression : " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Annuler", null).show();
    }
}
