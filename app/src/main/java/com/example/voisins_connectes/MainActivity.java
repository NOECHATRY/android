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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AnnonceAdapter adapter;
    private List<Annonce> allAnnonces;
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
        if (!prefs.contains("username")) {
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

        // 4. Initialisation des données et de la liste
        initData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new AnnonceAdapter(new ArrayList<>(allAnnonces));
        recyclerView.setAdapter(adapter);

        // 5. Charger les demandes des voisins
        refreshDemandesVoisins();

        // 6. Configuration du Pull-to-Refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                initData();
                adapter.setAnnonces(new ArrayList<>(allAnnonces));
                refreshDemandesVoisins();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Page actualisée", Toast.LENGTH_SHORT).show();
            }, 1500);
        });

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
            } else if (itemId == R.id.nav_messages) {
                Toast.makeText(this, "Messagerie bientôt disponible", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilActivity.class));
                return true;
            }
            return false;
        });

        // 10. Gestion du bouton "+" (Publication d'une demande)
        fabPublier.setOnClickListener(v -> {
            startActivity(new Intent(this, PublierDemandeActivity.class));
        });

        // 11. Déconnexion
        Button btnConnexion = findViewById(R.id.btn_connexion);
        btnConnexion.setOnClickListener(v -> {
            prefs.edit().remove("username").apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDemandesVoisins(); // Rafraîchir quand on revient sur l'écran
    }

    private void refreshDemandesVoisins() {
        if (containerDemandes == null) return;
        containerDemandes.removeAllViews();
        List<Demande> liste = GestionDemandes.getListeDemandes();

        if (liste.isEmpty()) {
            TextView tvVide = new TextView(this);
            tvVide.setText("Aucune demande de voisin pour le moment.");
            tvVide.setPadding(0, 20, 0, 20);
            containerDemandes.addView(tvVide);
            return;
        }

        for (Demande d : liste) {
            // On ne montre que les demandes "En attente" pour les autres voisins
            if (!d.getStatut().equals("En attente")) continue;

            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 
                    LinearLayout.LayoutParams.WRAP_CONTENT);
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

            TextView desc = new TextView(this);
            desc.setText(d.getDescription());
            desc.setTextColor(0xFF666666);
            desc.setPadding(0, 8, 0, 8);

            TextView budget = new TextView(this);
            budget.setText("Budget : " + d.getBudget() + " crédits");
            budget.setTextColor(0xFF2E7D32);
            budget.setTextSize(12);
            budget.setPadding(0, 0, 0, 16);

            Button btnRendreService = new Button(this, null, com.google.android.material.R.attr.materialButtonStyle);
            btnRendreService.setText("Rendre service");
            btnRendreService.setAllCaps(false);
            btnRendreService.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF2196F3));
            
            btnRendreService.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                    .setTitle("Rendre service")
                    .setMessage("Voulez-vous accepter cette demande et aider ce voisin ?")
                    .setPositiveButton("Oui, j'aide", (dialog, which) -> {
                        d.setStatut("Validée");
                        String monNom = prefs.getString("username", "Un voisin");
                        d.setReponse("Réponse de " + monNom + " : J'accepte de vous aider !");
                        Toast.makeText(this, "Merci ! Le voisin a été prévenu.", Toast.LENGTH_LONG).show();
                        refreshDemandesVoisins();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
            });

            layout.addView(titre);
            layout.addView(desc);
            layout.addView(budget);
            layout.addView(btnRendreService);
            card.addView(layout);

            containerDemandes.addView(card);
        }
    }

    private void initData() {
        allAnnonces = new ArrayList<>();
        allAnnonces.add(new Annonce(1, "Tonte de pelouse", "Je propose mes services pour tondre votre pelouse.", 15.0, "22/05/2024", "Jardinage"));
        allAnnonces.add(new Annonce(2, "Cours de Maths", "Soutien scolaire pour niveau collège.", 20.0, "21/05/2024", "Cours"));
        allAnnonces.add(new Annonce(3, "Aide déménagement", "Besoin de bras pour porter des cartons.", 10.0, "20/05/2024", "Bricolage"));
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
