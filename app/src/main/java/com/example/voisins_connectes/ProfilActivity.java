package com.example.voisins_connectes;

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
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ProfilActivity extends AppCompatActivity {

    private LinearLayout containerDemandes;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // 1. Initialiser les vues
        containerDemandes = findViewById(R.id.container_mes_demandes);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_profil);
        TextView tvBienvenue = findViewById(R.id.tv_profil_bienvenue);

        // 2. Message de bienvenue
        SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        String username = prefs.getString("username", "Sophie");
        tvBienvenue.setText("Bonjour, " + username + " ! 👋");

        // 3. Charger les demandes
        refreshDemandes();

        // 4. Configurer le Pull-to-Refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                refreshDemandes();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "Profil actualisé", Toast.LENGTH_SHORT).show();
            }, 1000);
        });
    }

    private void refreshDemandes() {
        containerDemandes.removeAllViews();
        List<Demande> liste = GestionDemandes.getListeDemandes();

        if (liste.isEmpty()) {
            TextView tvVide = new TextView(this);
            tvVide.setText("Aucune demande publiée.");
            tvVide.setPadding(0, 20, 0, 20);
            containerDemandes.addView(tvVide);
            return;
        }

        for (Demande d : liste) {
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
            info.setText(d.getStatut() + " • " + d.getCategorie());
            info.setTextColor(0xFF1565C0);
            info.setTextSize(12);

            TextView reponse = new TextView(this);
            reponse.setText(d.getReponse());
            reponse.setPadding(0, 8, 0, 0);
            reponse.setTextColor(0xFF666666);
            reponse.setMaxLines(1);
            reponse.setEllipsize(android.text.TextUtils.TruncateAt.END);

            layout.addView(titre);
            layout.addView(info);
            layout.addView(reponse);
            card.addView(layout);

            // Gestion du clic pour voir le résumé détaillé
            card.setOnClickListener(v -> showDetailDemande(d));

            containerDemandes.addView(card);
        }
    }

    private void showDetailDemande(Demande d) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_detail_demande, null);

        TextView tvTitre = dialogView.findViewById(R.id.tv_detail_demande_titre);
        TextView tvCategorie = dialogView.findViewById(R.id.tv_detail_demande_categorie);
        TextView tvDescription = dialogView.findViewById(R.id.tv_detail_demande_description);
        TextView tvBudget = dialogView.findViewById(R.id.tv_detail_demande_budget);
        TextView tvStatut = dialogView.findViewById(R.id.tv_detail_demande_statut);
        TextView tvReponse = dialogView.findViewById(R.id.tv_detail_demande_reponse);

        tvTitre.setText(d.getTitre());
        tvCategorie.setText(d.getCategorie());
        tvDescription.setText(d.getDescription());
        tvBudget.setText(d.getBudget() + " crédits");
        tvStatut.setText(d.getStatut());
        tvReponse.setText(d.getReponse());

        builder.setView(dialogView)
               .setPositiveButton("Fermer", null)
               .show();
    }
}