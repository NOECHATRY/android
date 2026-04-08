package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AnnonceAdapter adapter;
    private final List<Annonce> allAnnonces = new ArrayList<>();
    private ChipGroup chipGroup;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        if (!prefs.contains("token")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Initialisation des vues
        EditText etRecherche = findViewById(R.id.et_recherche);
        chipGroup = findViewById(R.id.chip_group_categories);
        TextView tvBienvenue = findViewById(R.id.tv_Bienvenue);
        recyclerView = findViewById(R.id.rv_voisins);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        FloatingActionButton fabPublier = findViewById(R.id.fab_publier);

        tvBienvenue.setText("Bonjour, " + prefs.getString("username", "Voisin") + " !");

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new AnnonceAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Charger les données
        chargerCategoriesAPI();
        chargerDonneesAPI();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            chargerCategoriesAPI();
            chargerDonneesAPI();
        });

        // Navigation
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
        chip.setOnClickListener(v -> filterAnnoncesByCategory(label));
        chipGroup.addView(chip);
    }

    private void chargerDonneesAPI() {
        swipeRefreshLayout.setRefreshing(true);
        RetrofitClient.getApiService().getServices().enqueue(new Callback<List<Demande>>() {
            @Override
            public void onResponse(Call<List<Demande>> call, Response<List<Demande>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    allAnnonces.clear();
                    // On garde une copie pour l'onglet demandes
                    GestionDemandes.getListeDemandes().clear();
                    GestionDemandes.getListeDemandes().addAll(response.body());

                    for (Demande d : response.body()) {
                        // Sur l'accueil on affiche les services 'public' et 'publiee'
                        if ("public".equals(d.getTypeService()) && "publiee".equals(d.getEtatService())) {
                            Annonce a = new Annonce(d.getIdDemande(), d.getTitre(), d.getDescription(), 
                                                  d.getBudget(), d.getDatePublication(), getCategoryName(d.getIdCategorie()));
                            allAnnonces.add(a);
                        }
                    }
                    adapter.setAnnonces(new ArrayList<>(allAnnonces));
                }
            }
            @Override public void onFailure(Call<List<Demande>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private String getCategoryName(int id) {
        if (id == 1) return "Informatique";
        if (id == 2) return "Bricolage";
        if (id == 3) return "Cours";
        if (id == 4) return "Jardinage";
        return "Autre";
    }

    private void filterAnnoncesByCategory(String cat) {
        List<Annonce> filtered = new ArrayList<>();
        if ("Tout".equals(cat)) filtered.addAll(allAnnonces);
        else {
            for (Annonce a : allAnnonces) {
                if (cat.equalsIgnoreCase(a.getCategorie())) filtered.add(a);
            }
        }
        adapter.setAnnonces(filtered);
    }

    private void filterAnnoncesByText(String text) {
        List<Annonce> filtered = new ArrayList<>();
        if (text.isEmpty()) filtered.addAll(allAnnonces);
        else {
            for (Annonce a : allAnnonces) {
                if (a.getTitre().toLowerCase().contains(text.toLowerCase())) filtered.add(a);
            }
        }
        adapter.setAnnonces(filtered);
    }
}
