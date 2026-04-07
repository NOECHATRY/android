package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class DemandesVoisinsActivity extends AppCompatActivity {

    private LinearLayout containerMesDemandes;
    private LinearLayout containerToutesDemandes;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences prefs;
    private String monNom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demandes_voisins);

        prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        monNom = prefs.getString("username", "Sophie");

        containerMesDemandes = findViewById(R.id.container_mes_demandes_suivi);
        containerToutesDemandes = findViewById(R.id.container_toutes_demandes);
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

        refreshListes();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                refreshListes();
                swipeRefreshLayout.setRefreshing(false);
            }, 1000);
        });
    }

    private void refreshListes() {
        containerMesDemandes.removeAllViews();
        containerToutesDemandes.removeAllViews();
        
        List<Demande> liste = GestionDemandes.getListeDemandes();

        boolean hasMesDemandes = false;
        boolean hasAutresDemandes = false;

        for (Demande d : liste) {
            if (d.getAuteur().equals(monNom)) {
                hasMesDemandes = true;
                ajouterDemandeSuivi(d);
            } else if (d.getStatut().equals("En attente")) {
                hasAutresDemandes = true;
                ajouterDemandeVoisin(d);
            }
        }

        if (!hasMesDemandes) {
            TextView tv = new TextView(this);
            tv.setText("Vous n'avez publié aucune demande.");
            tv.setPadding(0, 20, 0, 20);
            containerMesDemandes.addView(tv);
        }

        if (!hasAutresDemandes) {
            TextView tv = new TextView(this);
            tv.setText("Aucune demande de voisin disponible.");
            tv.setPadding(0, 20, 0, 20);
            containerToutesDemandes.addView(tv);
        }
    }

    private void ajouterDemandeSuivi(Demande d) {
        MaterialCardView card = createBaseCard();
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        TextView titre = new TextView(this);
        titre.setText(d.getTitre());
        titre.setTextSize(16);
        titre.setTypeface(null, android.graphics.Typeface.BOLD);
        titre.setTextColor(0xFF000000);

        TextView info = new TextView(this);
        info.setText("Statut : " + d.getStatut());
        info.setTextColor(d.getStatut().equals("Validée") ? 0xFF2E7D32 : 0xFF1565C0);
        info.setTextSize(12);

        TextView reponse = new TextView(this);
        reponse.setText(d.getReponse());
        reponse.setPadding(0, 8, 0, 0);
        reponse.setTextColor(0xFF666666);

        layout.addView(titre);
        layout.addView(info);
        layout.addView(reponse);
        card.addView(layout);
        
        card.setOnClickListener(v -> showDetailDemande(d));
        containerMesDemandes.addView(card);
    }

    private void ajouterDemandeVoisin(Demande d) {
        // Design type accueil avec image
        MaterialCardView card = createBaseCard();
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        // Image comme sur l'accueil
        ImageView iv = new ImageView(this);
        iv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 400));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        if ("Jardinage".equalsIgnoreCase(d.getCategorie())) iv.setImageResource(R.drawable.jardinage);
        else if ("Bricolage".equalsIgnoreCase(d.getCategorie())) iv.setImageResource(R.drawable.bricolage);
        else if ("Cours".equalsIgnoreCase(d.getCategorie())) iv.setImageResource(R.drawable.cours);
        else iv.setImageResource(android.R.drawable.ic_menu_gallery);

        mainLayout.addView(iv);

        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        infoLayout.setPadding(32, 32, 32, 32);

        TextView titre = new TextView(this);
        titre.setText(d.getTitre());
        titre.setTextSize(18);
        titre.setTypeface(null, android.graphics.Typeface.BOLD);
        titre.setTextColor(0xFF000000);

        TextView auteur = new TextView(this);
        auteur.setText("Voisin : " + d.getAuteur() + " • " + d.getCategorie());
        auteur.setTextSize(12);
        auteur.setTextColor(0xFF1565C0);

        TextView budget = new TextView(this);
        budget.setText(d.getBudget() + " crédits");
        budget.setTextColor(0xFF666666);
        budget.setTextSize(13);
        budget.setPadding(0, 4, 0, 16);

        Button btnRendreService = new Button(this, null, com.google.android.material.R.attr.materialButtonStyle);
        btnRendreService.setText("Rendre service");
        btnRendreService.setAllCaps(false);
        btnRendreService.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF2196F3));
        
        btnRendreService.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Aider un voisin")
                .setMessage("Acceptez-vous d'aider " + d.getAuteur() + " pour sa demande : " + d.getTitre() + " ?")
                .setPositiveButton("Oui, j'aide", (dialog, which) -> {
                    accepterMission(d);
                })
                .setNegativeButton("Annuler", null).show();
        });

        infoLayout.addView(titre);
        infoLayout.addView(auteur);
        infoLayout.addView(budget);
        infoLayout.addView(btnRendreService);
        mainLayout.addView(infoLayout);
        
        card.addView(mainLayout);
        card.setOnClickListener(v -> showDetailDemande(d));
        containerToutesDemandes.addView(card);
    }

    private void accepterMission(Demande d) {
        d.setStatut("Validée");
        d.setAideur(monNom);
        d.setReponse("Réponse de " + monNom + " : J'ai accepté votre demande !");
        Toast.makeText(this, "Mission acceptée ! Retrouvez-la dans votre profil.", Toast.LENGTH_SHORT).show();
        refreshListes();
    }

    private void showDetailDemande(Demande d) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_detail_demande, null);

        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_titre)).setText(d.getTitre());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_categorie)).setText(d.getCategorie());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_description)).setText(d.getDescription());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_budget)).setText(d.getBudget() + " crédits");
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_statut)).setText(d.getStatut());
        ((TextView) dialogView.findViewById(R.id.tv_detail_demande_reponse)).setText(d.getReponse());

        builder.setView(dialogView);
        builder.setPositiveButton("Fermer", null);

        // Ajout du bouton "Accepter" si c'est une demande d'un voisin et qu'elle est en attente
        if (!d.getAuteur().equals(monNom) && d.getStatut().equals("En attente")) {
            builder.setNeutralButton("Accepter la mission", (dialog, which) -> {
                accepterMission(d);
            });
        }

        builder.show();
    }

    private MaterialCardView createBaseCard() {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        card.setLayoutParams(params);
        card.setRadius(32f);
        card.setCardElevation(4f);
        card.setStrokeColor(0xFFEEEEEE);
        card.setStrokeWidth(2);
        card.setClickable(true);
        card.setFocusable(true);
        return card;
    }
}
