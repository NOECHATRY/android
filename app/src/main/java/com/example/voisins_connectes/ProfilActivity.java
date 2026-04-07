package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ProfilActivity extends AppCompatActivity {

    private LinearLayout containerDemandes;
    private LinearLayout containerMissions;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView bottomNavigationView;
    private String monNom;

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

        // 2. Message de bienvenue
        SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        monNom = prefs.getString("username", "Sophie");
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
            new Handler().postDelayed(() -> {
                refreshAll();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "Profil actualisé", Toast.LENGTH_SHORT).show();
            }, 1000);
        });
    }

    private void refreshAll() {
        refreshDemandes();
        refreshMissions();
    }

    private void refreshDemandes() {
        containerDemandes.removeAllViews();
        List<Demande> liste = GestionDemandes.getListeDemandes();
        boolean hasDemandes = false;

        for (Demande d : liste) {
            if (d.getAuteur().equals(monNom)) {
                hasDemandes = true;
                containerDemandes.addView(createDemandeCard(d));
            }
        }

        if (!hasDemandes) {
            TextView tvVide = new TextView(this);
            tvVide.setText("Aucune demande publiée.");
            tvVide.setPadding(0, 20, 0, 20);
            containerDemandes.addView(tvVide);
        }
    }

    private void refreshMissions() {
        containerMissions.removeAllViews();
        List<Demande> liste = GestionDemandes.getListeDemandes();
        boolean hasMissions = false;

        for (Demande d : liste) {
            if (monNom.equals(d.getAideur())) {
                hasMissions = true;
                containerMissions.addView(createDemandeCard(d));
            }
        }

        if (!hasMissions) {
            TextView tvVide = new TextView(this);
            tvVide.setText("Vous n'avez pas encore accepté de missions.");
            tvVide.setPadding(0, 20, 0, 20);
            containerMissions.addView(tvVide);
        }
    }

    private MaterialCardView createDemandeCard(Demande d) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);
        card.setRadius(24f);
        card.setCardElevation(0f);
        card.setStrokeColor(0xFFEEEEEE);
        card.setStrokeWidth(2);
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        TextView titre = new TextView(this);
        titre.setText(d.getTitre());
        titre.setTextSize(16);
        titre.setTypeface(null, android.graphics.Typeface.BOLD);
        titre.setTextColor(0xFF000000);

        TextView info = new TextView(this);
        info.setText(d.getStatut() + " • " + d.getCategorie() + " • Par " + d.getAuteur());
        info.setTextColor(0xFF1565C0);
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

        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_titre)).setText(d.getTitre());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_categorie)).setText(d.getCategorie());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_description)).setText(d.getDescription());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_budget)).setText(d.getBudget() + " crédits");
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_statut)).setText(d.getStatut());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_reponse)).setText(d.getReponse());

        builder.setView(dialogView).setPositiveButton("Fermer", null).show();
    }
}
