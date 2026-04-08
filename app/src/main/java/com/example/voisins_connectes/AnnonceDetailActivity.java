package com.example.voisins_connectes;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnonceDetailActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_AVIS = 100;
    private LinearLayout containerAvis;
    private LinearLayout containerHoraires;
    private Button btnVoirPlusAvis;
    private Button btnAjouterAvis;
    private Button btnReserver;
    private TextView tvHoraireSelectionne;
    private Annonce currentAnnonce;
    private boolean isAvisExpanded = false;
    private String selectedHoraire = null;

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
        containerHoraires = findViewById(R.id.container_horaires);
        btnVoirPlusAvis = findViewById(R.id.btn_voir_plus_avis);
        btnAjouterAvis = findViewById(R.id.btn_ajouter_avis);
        btnReserver = findViewById(R.id.btn_reserver);
        tvHoraireSelectionne = findViewById(R.id.tv_horaire_selectionne);

        // Récupération de l'annonce passée en paramètre
        currentAnnonce = (Annonce) getIntent().getSerializableExtra("annonce");

        if (currentAnnonce != null) {
            populateDetails(currentAnnonce);
            setupAvis(currentAnnonce.getAvis());
            setupTableauEmploiDuTemps(currentAnnonce);
        }

        btnAjouterAvis.setOnClickListener(v -> {
            Intent intent = new Intent(this, AjouterAvisActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_AVIS);
        });

        btnReserver.setOnClickListener(v -> {
            if (selectedHoraire == null) {
                Toast.makeText(this, "Veuillez sélectionner un horaire d'abord", Toast.LENGTH_SHORT).show();
            } else {
                currentAnnonce.getHorairesDisponibles().remove(selectedHoraire);
                currentAnnonce.setHoraireReserve(selectedHoraire);
                Toast.makeText(this, "Réservation confirmée pour : " + selectedHoraire, Toast.LENGTH_LONG).show();
                
                setupTableauEmploiDuTemps(currentAnnonce);
                selectedHoraire = null;
                tvHoraireSelectionne.setText("Aucun horaire sélectionné");
                tvHoraireSelectionne.setTextColor(Color.GRAY);
            }
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

        // Mise à jour des images selon la catégorie pour le résumé
        String cat = annonce.getCategorie();
        if ("Jardinage".equalsIgnoreCase(cat)) {
            ivHeader.setImageResource(R.drawable.jardinage);
        } else if ("Bricolage".equalsIgnoreCase(cat)) {
            ivHeader.setImageResource(R.drawable.bricolage);
        } else if ("Cours".equalsIgnoreCase(cat) || "Cours particuliers".equalsIgnoreCase(cat)) {
            ivHeader.setImageResource(R.drawable.cours);
        } else if ("Informatique".equalsIgnoreCase(cat)) {
            ivHeader.setImageResource(R.drawable.informatique);
        } else {
            ivHeader.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void setupTableauEmploiDuTemps(Annonce annonce) {
        containerHoraires.removeAllViews();
        
        TableLayout table = new TableLayout(this);
        table.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        table.setStretchAllColumns(true);
        table.setBackgroundColor(Color.parseColor("#4DB6AC"));

        String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        
        TableRow headerRow = new TableRow(this);
        headerRow.setPadding(2, 2, 2, 2);
        
        TextView tvHeureLabel = createTableCell("Heure", true);
        headerRow.addView(tvHeureLabel);
        
        for (String jour : jours) {
            headerRow.addView(createTableCell(jour.toUpperCase(), true));
        }
        table.addView(headerRow);

        Map<String, List<String>> planning = new HashMap<>();
        for (String jour : jours) planning.put(jour, new ArrayList<>());
        
        for (String h : annonce.getHorairesDisponibles()) {
            String[] parts = h.split(" ");
            if (parts.length == 2) {
                planning.get(parts[0]).add(parts[1]);
            }
        }

        int maxSlots = 1;
        for (List<String> slots : planning.values()) {
            if (slots.size() > maxSlots) maxSlots = slots.size();
        }

        for (int i = 0; i < maxSlots; i++) {
            TableRow row = new TableRow(this);
            row.setPadding(2, 2, 2, 2);
            
            row.addView(createTableCell("-", false));
            
            for (String jour : jours) {
                List<String> slots = planning.get(jour);
                if (i < slots.size()) {
                    String currentH = slots.get(i);
                    TextView cell = createTableCell(currentH, false);
                    cell.setOnClickListener(v -> {
                        selectedHoraire = jour + " " + currentH;
                        tvHoraireSelectionne.setText("Sélectionné : " + selectedHoraire);
                        tvHoraireSelectionne.setTextColor(Color.parseColor("#00796B"));
                        resetTableSelection(table);
                        cell.setBackgroundColor(Color.parseColor("#B2DFDB"));
                    });
                    row.addView(cell);
                } else {
                    row.addView(createTableCell("Indisponible", false));
                }
            }
            table.addView(row);
        }

        containerHoraires.addView(table);
    }

    private TextView createTableCell(String text, boolean isHeader) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 24, 8, 24);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(Color.WHITE);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
        params.setMargins(2, 2, 2, 2);
        tv.setLayoutParams(params);
        
        if (isHeader) {
            tv.setTextColor(Color.parseColor("#004D40"));
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextSize(12);
        } else {
            tv.setTextColor(text.equals("Indisponible") ? Color.LTGRAY : Color.BLACK);
            tv.setTextSize(11);
        }
        return tv;
    }

    private void resetTableSelection(TableLayout table) {
        for (int i = 1; i < table.getChildCount(); i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            for (int j = 1; j < row.getChildCount(); j++) {
                View cell = row.getChildAt(j);
                if (cell instanceof TextView) {
                    String text = ((TextView) cell).getText().toString();
                    if (!text.equals("Indisponible")) {
                        cell.setBackgroundColor(Color.WHITE);
                    }
                }
            }
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
