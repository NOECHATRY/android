package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DemandesVoisinsActivity extends AppCompatActivity {

    private LinearLayout containerMesDemandes;
    private LinearLayout containerToutesDemandes;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences prefs;
    private String monNom;
    private int monId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demandes_voisins);

        prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        monNom = prefs.getString("username", "Sophie");
        monId = prefs.getInt("idMembre", -1);

        containerMesDemandes = findViewById(R.id.container_mes_demandes_suivi);
        containerToutesDemandes = findViewById(R.id.container_toutes_demandes);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_demandes);
        bottomNavigationView = findViewById(R.id.bottom_navigation_demandes);

        bottomNavigationView.setSelectedItemId(R.id.nav_requests);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_requests) {
                return true;
            } else if (itemId == R.id.nav_premium) {
                startActivity(new Intent(this, AbonnementActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilActivity.class));
                finish();
                return true;
            }
            return false;
        });

        loadDemandesFromAPI();
        swipeRefreshLayout.setOnRefreshListener(this::loadDemandesFromAPI);
    }

    private void loadDemandesFromAPI() {
        swipeRefreshLayout.setRefreshing(true);
        RetrofitClient.getApiService().getServices().enqueue(new Callback<List<Demande>>() {
            @Override
            public void onResponse(Call<List<Demande>> call, Response<List<Demande>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    GestionDemandes.getListeDemandes().clear();
                    GestionDemandes.getListeDemandes().addAll(response.body());
                    refreshListes();
                }
            }
            @Override
            public void onFailure(Call<List<Demande>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(DemandesVoisinsActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshListes() {
        containerMesDemandes.removeAllViews();
        containerToutesDemandes.removeAllViews();
        List<Demande> liste = GestionDemandes.getListeDemandes();

        for (Demande d : liste) {
            if (d.getIdMembre() == monId) {
                ajouterDemandeSuivi(d);
            } else if ("publiee".equals(d.getEtatService())) {
                ajouterDemandeVoisin(d);
            }
        }
    }

    private void ajouterDemandeSuivi(Demande d) {
        View cardView = getLayoutInflater().inflate(R.layout.item_demande_card, containerMesDemandes, false);
        TextView titre = cardView.findViewById(R.id.tv_demande_titre);
        TextView budget = cardView.findViewById(R.id.tv_demande_budget);
        ImageView iv = cardView.findViewById(R.id.iv_demande_image);

        titre.setText(d.getTitre());
        budget.setText("Statut : " + d.getEtatService());
        iv.setVisibility(View.GONE);

        cardView.setOnClickListener(v -> showDetailDemande(d));
        containerMesDemandes.addView(cardView);
    }

    private void ajouterDemandeVoisin(Demande d) {
        View cardView = getLayoutInflater().inflate(R.layout.item_demande_card, containerToutesDemandes, false);
        TextView titre = cardView.findViewById(R.id.tv_demande_titre);
        TextView budget = cardView.findViewById(R.id.tv_demande_budget);
        ImageView iv = cardView.findViewById(R.id.iv_demande_image);

        titre.setText(d.getTitre());
        budget.setText(d.getBudget() + " crédits");
        setCategoryImage(iv, d.getIdCategorie());

        cardView.setOnClickListener(v -> showDetailDemande(d));
        containerToutesDemandes.addView(cardView);
    }

    private void setCategoryImage(ImageView iv, int idCat) {
        if (idCat == 1) iv.setImageResource(R.drawable.informatique);
        else if (idCat == 2) iv.setImageResource(R.drawable.bricolage);
        else if (idCat == 3) iv.setImageResource(R.drawable.cours);
        else if (idCat == 4) iv.setImageResource(R.drawable.jardinage);
        else iv.setImageResource(android.R.drawable.ic_menu_gallery);
    }

    private void showDetailDemande(Demande d) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_detail_demande, null);

        ImageView ivDetail = dialogView.findViewById(R.id.iv_detail_demande_image);
        setCategoryImage(ivDetail, d.getIdCategorie());

        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_titre)).setText(d.getTitre());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_description)).setText(d.getDescription());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_budget)).setText(d.getBudget() + " crédits");
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_statut)).setText(d.getEtatService());
        
        TextView tvReponse = dialogView.findViewById(R.id.tv_detail_demande_reponse);
        tvReponse.setText(d.getReponse() != null ? d.getReponse() : "Aucune réponse pour le moment.");

        builder.setView(dialogView);
        builder.setPositiveButton("Fermer", null);

        Button btnAccepter = dialogView.findViewById(R.id.btn_dialog_accepter);
        if (d.getIdMembre() != monId && "publiee".equals(d.getEtatService())) {
            btnAccepter.setVisibility(View.VISIBLE);
            btnAccepter.setOnClickListener(v -> accepterMission(d));
        } else {
            btnAccepter.setVisibility(View.GONE);
        }

        builder.show();
    }

    private void accepterMission(Demande d) {
        // 1. Ajouter une réponse dans la table 'reponse'
        Map<String, Object> reponseData = new HashMap<>();
        reponseData.put("message", "J'accepte votre demande !");
        reponseData.put("idMembre_offreur", monId);
        reponseData.put("idService", d.getIdDemande());
        reponseData.put("statutReponse", "acceptee");

        RetrofitClient.getApiService().addReponse(reponseData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 2. Mettre à jour l'état du service vers 'validee'
                    Map<String, Integer> validateData = new HashMap<>();
                    validateData.put("idService", d.getIdDemande());
                    
                    RetrofitClient.getApiService().validateService("validate", validateData).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(DemandesVoisinsActivity.this, "Mission acceptée ! Retrouvez-la dans votre profil.", Toast.LENGTH_SHORT).show();
                            loadDemandesFromAPI(); // Refresh UI
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            loadDemandesFromAPI();
                        }
                    });
                } else {
                    Toast.makeText(DemandesVoisinsActivity.this, "Erreur lors de l'acceptation", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DemandesVoisinsActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
