package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class DemandesVoisinsActivity extends AppCompatActivity {

    private LinearLayout containerDemandes;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demandes_voisins);

        prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        containerDemandes = findViewById(R.id.container_toutes_demandes);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_demandes);
        bottomNavigationView = findViewById(R.id.bottom_navigation_demandes);

        // Configurer la navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_requests);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_requests) {
                return true;
            } else if (itemId == R.id.nav_messages) {
                Toast.makeText(this, "Messagerie bientôt disponible", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilActivity.class));
                finish();
                return true;
            }
            return false;
        });

        refreshListeDemandes();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                refreshListeDemandes();
                swipeRefreshLayout.setRefreshing(false);
            }, 1000);
        });
    }

    private void refreshListeDemandes() {
        containerDemandes.removeAllViews();
        List<Demande> liste = GestionDemandes.getListeDemandes();

        boolean hasDemandes = false;
        for (Demande d : liste) {
            if (!d.getStatut().equals("En attente")) continue;
            hasDemandes = true;

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
            titre.setTextSize(18);
            titre.setTypeface(null, android.graphics.Typeface.BOLD);
            titre.setTextColor(0xFF000000);

            TextView info = new TextView(this);
            info.setText("Catégorie : " + d.getCategorie());
            info.setTextColor(0xFF1565C0);
            info.setTextSize(12);

            TextView desc = new TextView(this);
            desc.setText(d.getDescription());
            desc.setPadding(0, 8, 0, 8);
            desc.setTextColor(0xFF444444);

            TextView budget = new TextView(this);
            budget.setText("Récompense : " + d.getBudget() + " crédits");
            budget.setTextColor(0xFF2E7D32);
            budget.setPadding(0, 0, 0, 16);
            budget.setTextSize(14);
            budget.setTypeface(null, android.graphics.Typeface.BOLD);

            Button btnAider = new Button(this, null, com.google.android.material.R.attr.materialButtonStyle);
            btnAider.setText("Accepter et aider");
            btnAider.setAllCaps(false);
            btnAider.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
            
            btnAider.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                    .setTitle("Confirmer l'aide")
                    .setMessage("Voulez-vous vraiment aider ce voisin pour " + d.getTitre() + " ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        d.setStatut("Validée");
                        String monNom = prefs.getString("username", "Un voisin");
                        d.setReponse("Réponse de " + monNom + " : J'ai accepté votre demande !");
                        Toast.makeText(this, "Super ! Vous avez accepté d'aider.", Toast.LENGTH_LONG).show();
                        refreshListeDemandes();
                    })
                    .setNegativeButton("Non", null)
                    .show();
            });

            layout.addView(titre);
            layout.addView(info);
            layout.addView(desc);
            layout.addView(budget);
            layout.addView(btnAider);
            card.addView(layout);

            containerDemandes.addView(card);
        }

        if (!hasDemandes) {
            TextView tvVide = new TextView(this);
            tvVide.setText("Aucune demande disponible pour le moment.");
            tvVide.setGravity(android.view.Gravity.CENTER);
            tvVide.setPadding(0, 100, 0, 0);
            containerDemandes.addView(tvVide);
        }
    }
}
