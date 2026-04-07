package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
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

    private RecyclerView recyclerView;
    private AnnonceAdapter adapter;
    private List<Annonce> allAnnonces = new ArrayList<>();
    private TextView tvBienvenue;
    private ChipGroup chipGroup;
    private EditText etRecherche;
    private BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabPublier;
    private LinearLayout containerDemandes;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Vérifier la session
        prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        if (!prefs.contains("token")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // 2. Initialiser les vues
        etRecherche = findViewById(R.id.et_recherche);
        chipGroup = findViewById(R.id.chip_group_categories);
        tvBienvenue = findViewById(R.id.tv_Bienvenue);
        recyclerView = findViewById(R.id.rv_voisins);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        fabPublier = findViewById(R.id.fab_publier);
        containerDemandes = findViewById(R.id.container_demandes_accueil);

        // 3. Message de bienvenue
        String username = prefs.getString("username", "Voisin");
        tvBienvenue.setText("Bonjour, " + username + " !");

        // 4. Configuration de la liste
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new AnnonceAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 5. Charger les données depuis l'API
        chargerDonneesAPI();

        // 6. Configuration du Pull-to-Refresh
        swipeRefreshLayout.setOnRefreshListener(this::chargerDonneesAPI);

        // 7. Logique de filtrage reliée à la barre de recherche
        etRecherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    View view = chipGroup.getChildAt(i);
                    if (view instanceof Chip) {
                        Chip chip = (Chip) view;
                        String categoryName = chip.getText().toString().toLowerCase();
                        if (chip.getId() == R.id.chip_tout) {
                            chip.setVisibility(query.isEmpty() ? View.VISIBLE : View.GONE);
                        } else {
                            chip.setVisibility(categoryName.contains(query) ? View.VISIBLE : View.GONE);
                        }
                    }
                }
                filterAnnoncesByText(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 8. Gestion du clic sur les catégories
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                filterAnnoncesByCategory("Tout");
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chip_bricolage) filterAnnoncesByCategory("Bricolage");
                else if (checkedId == R.id.chip_jardinage) filterAnnoncesByCategory("Jardinage");
                else if (checkedId == R.id.chip_cours) filterAnnoncesByCategory("Cours");
                else filterAnnoncesByCategory("Tout");
            }
        });

        // 9. Gestion de la barre de navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_requests) {
                startActivity(new Intent(this, DemandesVoisinsActivity.class));
                return true;
            } else if (itemId == R.id.nav_premium) {
                startActivity(new Intent(this, AbonnementActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilActivity.class));
                return true;
            }
            return false;
        });

        // 10. Gestion du bouton "+"
        fabPublier.setOnClickListener(v -> startActivity(new Intent(this, PublierDemandeActivity.class)));

        // 11. Déconnexion
        Button btnConnexion = findViewById(R.id.btn_connexion);
        btnConnexion.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void chargerDonneesAPI() {
        swipeRefreshLayout.setRefreshing(true);
        RetrofitClient.getApiService().getServices().enqueue(new Callback<List<Demande>>() {
            @Override
            public void onResponse(Call<List<Demande>> call, Response<List<Demande>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    allAnnonces.clear();
                    // On convertit les 'Demande' de l'API en objets 'Annonce' pour l'adapter
                    for (Demande d : response.body()) {
                        // On ne montre que les OFFRES dans la section services populaires
                        if ("offre".equals(d.getTypeService())) {
                            Annonce a = new Annonce(d.getIdDemande(), d.getTitre(), d.getDescription(), 
                                                  d.getBudget(), d.getDatePublication(), "Service");
                            allAnnonces.add(a);
                        }
                    }
                    adapter.setAnnonces(new ArrayList<>(allAnnonces));
                    
                    // On met à jour aussi les DEMANDES dans GestionDemandes
                    GestionDemandes.getListeDemandes().clear();
                    for (Demande d : response.body()) {
                        if ("demande".equals(d.getTypeService())) {
                            GestionDemandes.ajouterDemande(d);
                        }
                    }
                    refreshDemandesVoisins();
                }
            }

            @Override
            public void onFailure(Call<List<Demande>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Erreur API : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshDemandesVoisins() {
        if (containerDemandes == null) return;
        containerDemandes.removeAllViews();
        List<Demande> liste = GestionDemandes.getListeDemandes();
        String monNom = prefs.getString("username", "Voisin");
        int monId = prefs.getInt("idMembre", -1);

        boolean hasDemandesVoisins = false;

        for (Demande d : liste) {
            if (!d.getEtatService().equals("open") || d.getIdMembre() == monId) continue;

            hasDemandesVoisins = true;
            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 16);
            card.setLayoutParams(params);
            card.setRadius(24f);
            card.setCardElevation(2f);
            card.setStrokeColor(0xFFEEEEEE);
            card.setStrokeWidth(2);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 32);

            TextView titre = new TextView(this);
            titre.setText(d.getTitre());
            titre.setTextSize(16);
            titre.setTypeface(null, android.graphics.Typeface.BOLD);
            titre.setTextColor(0xFF000000);

            TextView budget = new TextView(this);
            budget.setText("Budget : " + d.getBudget() + " crédits");
            budget.setTextColor(0xFF2E7D32);
            budget.setTextSize(12);

            Button btnRendreService = new Button(this, null, com.google.android.material.R.attr.materialButtonStyle);
            btnRendreService.setText("Rendre service");
            btnRendreService.setAllCaps(false);
            btnRendreService.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF2196F3));
            
            btnRendreService.setOnClickListener(v -> {
                Map<String, Object> data = new HashMap<>();
                data.put("message", "Je propose mon aide !");
                data.put("idService", d.getIdDemande());
                data.put("idMembre_offreur", monId);

                RetrofitClient.getApiService().addReponse(data).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Proposition envoyée !", Toast.LENGTH_SHORT).show();
                            chargerDonneesAPI();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {}
                });
            });

            layout.addView(titre);
            layout.addView(budget);
            layout.addView(btnRendreService);
            card.addView(layout);
            containerDemandes.addView(card);
        }
    }

    private void filterAnnoncesByCategory(String categorie) {
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

    private void filterAnnoncesByText(String text) {
        List<Annonce> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList.addAll(allAnnonces);
        } else {
            for (Annonce a : allAnnonces) {
                if (a.getTitre().toLowerCase().contains(text) || 
                    a.getCategorie().toLowerCase().contains(text) ||
                    a.getDescription().toLowerCase().contains(text)) {
                    filteredList.add(a);
                }
            }
        }
        adapter.setAnnonces(filteredList);
    }
}
