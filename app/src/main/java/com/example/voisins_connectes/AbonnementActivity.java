package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbonnementActivity extends AppCompatActivity {

    private LinearLayout containerForfaits;
    private int idMembre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abonnement);

        containerForfaits = findViewById(R.id.container_forfaits);
        
        SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        idMembre = prefs.getInt("idMembre", -1);

        loadForfaits();
    }

    private void loadForfaits() {
        RetrofitClient.getApiService().getForfaits().enqueue(new Callback<List<Forfait>>() {
            @Override
            public void onResponse(Call<List<Forfait>> call, Response<List<Forfait>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayForfaits(response.body());
                } else {
                    Toast.makeText(AbonnementActivity.this, "Erreur chargement forfaits", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Forfait>> call, Throwable t) {
                Toast.makeText(AbonnementActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayForfaits(List<Forfait> forfaits) {
        containerForfaits.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Forfait f : forfaits) {
            View cardView = inflater.inflate(R.layout.item_forfait_card, containerForfaits, false);

            TextView tvNom = cardView.findViewById(R.id.tv_forfait_nom);
            TextView tvPrix = cardView.findViewById(R.id.tv_forfait_prix);
            TextView tvType = cardView.findViewById(R.id.tv_forfait_type);
            TextView tvDuree = cardView.findViewById(R.id.tv_forfait_duree);
            LinearLayout header = cardView.findViewById(R.id.ll_forfait_header);
            Button btnChoisir = cardView.findViewById(R.id.btn_forfait_choisir);

            tvNom.setText(f.getNomForfait());
            tvPrix.setText(String.format(Locale.FRANCE, "%.2f €", f.getPrix()));
            tvDuree.setText("⏱ Durée : " + f.getDuree() + " jour(s)");

            if (f.getPrix() <= 0) {
                tvType.setText("GRATUIT");
                header.setBackgroundResource(R.color.vert_light);
                tvType.setTextColor(getResources().getColor(R.color.vert_dark));
                tvNom.setTextColor(getResources().getColor(R.color.texte_main));
                tvPrix.setTextColor(getResources().getColor(R.color.vert_dark));
            } else {
                tvType.setText("PAYANT");
                header.setBackgroundResource(R.color.vert_dark);
                tvType.setTextColor(Color.WHITE);
                tvNom.setTextColor(Color.WHITE);
                tvPrix.setTextColor(Color.WHITE);
            }

            updateFeature(cardView.findViewById(R.id.tv_fonc_parametrage), f.getFonc_parametrage());
            updateFeature(cardView.findViewById(R.id.tv_fonc_perimetre), f.getFonc_perimetreIntervention());
            updateFeature(cardView.findViewById(R.id.tv_fonc_notifications), f.getFonc_notifications());
            updateFeature(cardView.findViewById(R.id.tv_fonc_location), f.getFonc_locationMateriel());
            updateFeature(cardView.findViewById(R.id.tv_fonc_prestation), f.getFonc_prestationService());
            updateFeature(cardView.findViewById(R.id.tv_fonc_numero), f.getFonc_numeroProfil());
            updateFeature(cardView.findViewById(R.id.tv_fonc_photos), f.getFonc_photosProfil());
            updateFeature(cardView.findViewById(R.id.tv_fonc_accompagnement), f.getFonc_accompagnementPrioritaire());

            btnChoisir.setOnClickListener(v -> souscrireForfait(f));

            containerForfaits.addView(cardView);
        }
    }

    private void updateFeature(TextView tv, int value) {
        if (value == 1) {
            tv.setText("✓ " + tv.getText().toString().replaceAll("^[✓X] ", ""));
            tv.setTextColor(getResources().getColor(R.color.texte_main));
        } else {
            tv.setText("X " + tv.getText().toString().replaceAll("^[✓X] ", ""));
            tv.setTextColor(getResources().getColor(R.color.rouge_main));
        }
    }

    private void souscrireForfait(Forfait f) {
        if (idMembre == -1) {
            Toast.makeText(this, "Erreur : Utilisateur non identifié", Toast.LENGTH_SHORT).show();
            return;
        }

        // Préparation des dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String dateDebut = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, f.getDuree());
        String dateFin = sdf.format(cal.getTime());

        Map<String, Object> data = new HashMap<>();
        data.put("idMembre", idMembre);
        data.put("idForfait", f.getIdForfait());
        data.put("dateDebut", dateDebut);
        data.put("dateFin", dateFin);
        data.put("statut", "en_attente");

        // Appel à l'API adhesions.php
        RetrofitClient.getApiService().addAdhesion(data).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AbonnementActivity.this, "Demande envoyée ! Statut : En attente", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AbonnementActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AbonnementActivity.this, "Erreur lors de la demande d'adhésion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AbonnementActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
