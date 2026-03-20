package com.example.voisins_connectes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.chip.Chip;
import java.util.List;

public class AnnonceDetailActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_AVIS = 100;
    private LinearLayout containerAvis;
    private Button btnVoirPlusAvis;
    private Button btnAjouterAvis;
    private Annonce currentAnnonce;
    private boolean isAvisExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        containerAvis = findViewById(R.id.container_avis);
        btnVoirPlusAvis = findViewById(R.id.btn_voir_plus_avis);
        btnAjouterAvis = findViewById(R.id.btn_ajouter_avis);

        // Récupération de l'annonce passée en paramètre
        currentAnnonce = (Annonce) getIntent().getSerializableExtra("annonce");

        if (currentAnnonce != null) {
            populateDetails(currentAnnonce);
            setupAvis(currentAnnonce.getAvis());
        }

        btnAjouterAvis.setOnClickListener(v -> {
            Intent intent = new Intent(this, AjouterAvisActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_AVIS);
        });
    }

    private void populateDetails(Annonce annonce) {
        TextView tvTitre = findViewById(R.id.tv_detail_titre);
        TextView tvDescription = findViewById(R.id.tv_detail_description);
        TextView tvPrix = findViewById(R.id.tv_detail_prix);
        TextView tvLocation = findViewById(R.id.tv_detail_location);
        TextView tvDuree = findViewById(R.id.tv_detail_duree);
        Chip chipCategorie = findViewById(R.id.chip_detail_categorie);
        TextView tvRating = findViewById(R.id.tv_detail_rating);
        TextView tvUserName = findViewById(R.id.tv_user_name);
        TextView tvUserStats = findViewById(R.id.tv_user_stats);
        ImageView ivHeader = findViewById(R.id.iv_annonce_header);

        tvTitre.setText(annonce.getTitre());
        tvDescription.setText(annonce.getDescription());
        tvPrix.setText(String.format("%.2f €", annonce.getPrix()));
        tvLocation.setText("📍 " + annonce.getLocalisation());
        tvDuree.setText("🕒 " + annonce.getDuree());
        chipCategorie.setText(annonce.getCategorie());
        tvRating.setText(String.format("★ %.1f", annonce.getUserRating()));
        tvUserName.setText(annonce.getUserName());
        tvUserStats.setText(String.format("★ %.1f (%d avis)", annonce.getUserRating(), annonce.getNbAvis()));

        // Définir l'image selon la catégorie
        if ("Jardinage".equalsIgnoreCase(annonce.getCategorie())) {
            ivHeader.setImageResource(R.drawable.jardinage);
        } else if ("Bricolage".equalsIgnoreCase(annonce.getCategorie())) {
            ivHeader.setImageResource(R.drawable.bricolage);
        } else if ("Cours".equalsIgnoreCase(annonce.getCategorie())) {
            ivHeader.setImageResource(R.drawable.cours);
        }
    }

    private void setupAvis(List<Avis> listAvis) {
        containerAvis.removeAllViews();
        if (listAvis == null || listAvis.isEmpty()) {
            btnVoirPlusAvis.setVisibility(View.GONE);
            return;
        }

        int maxInitial = 5;
        for (int i = 0; i < listAvis.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_avis, containerAvis, false);
            Avis avis = listAvis.get(i);
            
            ((TextView) view.findViewById(R.id.tv_avis_user)).setText(avis.getUserName());
            ((RatingBar) view.findViewById(R.id.rb_avis_stars)).setRating(avis.getNote());
            ((TextView) view.findViewById(R.id.tv_avis_date)).setText(avis.getDate());
            ((TextView) view.findViewById(R.id.tv_avis_commentaire)).setText(avis.getCommentaire());

            if (i >= maxInitial && !isAvisExpanded) {
                view.setVisibility(View.GONE);
            }
            containerAvis.addView(view);
        }

        if (listAvis.size() > maxInitial) {
            btnVoirPlusAvis.setVisibility(View.VISIBLE);
            btnVoirPlusAvis.setText(isAvisExpanded ? "Voir moins" : "Voir plus");
            btnVoirPlusAvis.setOnClickListener(v -> {
                isAvisExpanded = !isAvisExpanded;
                btnVoirPlusAvis.setText(isAvisExpanded ? "Voir moins" : "Voir plus");
                for (int i = maxInitial; i < containerAvis.getChildCount(); i++) {
                    containerAvis.getChildAt(i).setVisibility(isAvisExpanded ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            btnVoirPlusAvis.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_AVIS && resultCode == RESULT_OK && data != null) {
            Avis nouvelAvis = (Avis) data.getSerializableExtra("nouvelAvis");
            if (nouvelAvis != null && currentAnnonce != null) {
                currentAnnonce.addAvis(nouvelAvis);
                populateDetails(currentAnnonce);
                setupAvis(currentAnnonce.getAvis());
            }
        }
    }
}