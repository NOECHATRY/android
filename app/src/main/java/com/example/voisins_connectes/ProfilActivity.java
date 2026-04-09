package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilActivity extends AppCompatActivity {

    private LinearLayout containerDemandes;
    private LinearLayout containerMissions;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView bottomNavigationView;
    private String monNom;
    private int monId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // 1. Initialiser les vues
        containerDemandes = findViewById(R.id.container_mes_demandes);
        containerMissions = findViewById(R.id.container_missions_acceptees);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_profil);
        TextView tvBienvenue = findViewById(R.id.tv_profil_bienvenue);
        bottomNavigationView = findViewById(R.id.bottom_navigation_profil);

        // 2. Récupération de la session
        SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        monNom = prefs.getString("username", "Sophie");
        monId = prefs.getInt("idMembre", -1);
        
        tvBienvenue.setText("Bonjour, " + monNom + " ! 👋");

        // 3. Configurer la navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_requests) {
                startActivity(new Intent(this, DemandesVoisinsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_premium) {
                startActivity(new Intent(this, AbonnementActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        // 4. Charger les données
        refreshAll();

        // 5. Configurer le Pull-to-Refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshAll();
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
        });
    }

    private void refreshAll() {
        // On recharge d'abord les services de l'API pour être à jour
        RetrofitClient.getApiService().getServices().enqueue(new Callback<List<Demande>>() {
            @Override
            public void onResponse(Call<List<Demande>> call, Response<List<Demande>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GestionDemandes.getListeDemandes().clear();
                    GestionDemandes.getListeDemandes().addAll(response.body());
                    
                    // Ensuite on charge les réponses pour savoir quelles missions sont acceptées
                    chargerMissionsAcceptees();
                }
            }
            @Override
            public void onFailure(Call<List<Demande>> call, Throwable t) {
                Toast.makeText(ProfilActivity.this, "Erreur de synchronisation", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chargerMissionsAcceptees() {
        RetrofitClient.getApiService().getReponses().enqueue(new Callback<List<Reponse>>() {
            @Override
            public void onResponse(Call<List<Reponse>> call, Response<List<Reponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reponse> mesReponses = response.body();
                    
                    // On nettoie les vues avant de remplir
                    containerDemandes.removeAllViews();
                    containerMissions.removeAllViews();
                    
                    List<Demande> toutesLesDemandes = GestionDemandes.getListeDemandes();
                    boolean hasDemandes = false;
                    boolean hasMissions = false;

                    for (Demande d : toutesLesDemandes) {
                        // 1. Mes propres demandes
                        if (d.getIdMembre() == monId) {
                            hasDemandes = true;
                            containerDemandes.addView(createDemandeCard(d));
                        }
                        
                        // 2. Mes missions acceptées (via la table 'reponse')
                        for (Reponse r : mesReponses) {
                            if (r.getIdService() == d.getIdDemande() && r.getIdMembreOffreur() == monId && "acceptee".equals(r.getStatutReponse())) {
                                hasMissions = true;
                                containerMissions.addView(createDemandeCard(d));
                                break;
                            }
                        }
                    }

                    if (!hasDemandes) {
                        TextView tv = new TextView(ProfilActivity.this);
                        tv.setText("Aucune demande publiée.");
                        tv.setPadding(0, 20, 0, 20);
                        containerDemandes.addView(tv);
                    }
                    if (!hasMissions) {
                        TextView tv = new TextView(ProfilActivity.this);
                        tv.setText("Vous n'avez pas encore accepté de missions.");
                        tv.setPadding(0, 20, 0, 20);
                        containerMissions.addView(tv);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Reponse>> call, Throwable t) {}
        });
    }

    private MaterialCardView createDemandeCard(Demande d) {
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
        card.setClickable(true);
        card.setFocusable(true);
        card.setCardBackgroundColor(0xFFFFFFFF);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        TextView titre = new TextView(this);
        titre.setText(d.getTitre() != null ? d.getTitre() : "Sans titre");
        titre.setTextSize(16);
        titre.setTypeface(null, android.graphics.Typeface.BOLD);
        titre.setTextColor(0xFF2C3E2D);

        TextView info = new TextView(this);
        info.setText("Statut : " + d.getEtatService());
        info.setTextColor(0xFF467832);
        info.setTextSize(12);

        layout.addView(titre);
        layout.addView(info);
        card.addView(layout);

        card.setOnClickListener(v -> showDetailDemande(d));
        return card;
    }

    private void showDetailDemande(Demande d) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_detail_demande, null);

        ImageView ivDetail = dialogView.findViewById(R.id.iv_detail_demande_image);
        setCategoryImage(ivDetail, d.getIdCategorie());

        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_titre)).setText(d.getTitre());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_description)).setText(d.getDescription());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_budget)).setText(d.getBudget() + " crédits");
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_statut)).setText(d.getEtatService());
        
        TextView tvReponse = dialogView.findViewById(R.id.tv_detail_demande_reponse);
        tvReponse.setText(d.getReponse() != null ? d.getReponse() : "Aucune réponse pour le moment.");

        builder.setView(dialogView).setPositiveButton("Fermer", null).show();
    }

    private void setCategoryImage(ImageView iv, int idCat) {
        if (idCat == 1) iv.setImageResource(R.drawable.informatique);
        else if (idCat == 2) iv.setImageResource(R.drawable.bricolage);
        else if (idCat == 3) iv.setImageResource(R.drawable.cours);
        else if (idCat == 4) iv.setImageResource(R.drawable.jardinage);
        else iv.setImageResource(android.R.drawable.ic_menu_gallery);
    }
}
