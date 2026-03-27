package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voisins_connectes.api.ApiService;
import com.example.voisins_connectes.models.Service;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AnnonceAdapter adapter;
    private List<Annonce> allAnnonces;
    private TextView tvBienvenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔐 Vérifier si l'utilisateur est connecté
        SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        if (!prefs.contains("username")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // 👋 Message de bienvenue
        String username = prefs.getString("username", "Voisin");
        tvBienvenue = findViewById(R.id.tv_Bienvenue);
        tvBienvenue.setText("Bonjour, " + username + " !");

        // 📦 RecyclerView
        recyclerView = findViewById(R.id.rv_voisins);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapter = new AnnonceAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 🌐 Charger les services depuis API
        loadServicesFromApi();

        // 🏷 Gestion des catégories
        ChipGroup chipGroup = findViewById(R.id.chip_group_categories);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                filterAnnonces("Tout");
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chip_tout) filterAnnonces("Tout");
                else if (checkedId == R.id.chip_bricolage) filterAnnonces("Bricolage");
                else if (checkedId == R.id.chip_jardinage) filterAnnonces("Jardinage");
                else if (checkedId == R.id.chip_cours) filterAnnonces("Cours");
            }
        });

        // 🔓 Déconnexion
        Button btnConnexion = findViewById(R.id.btn_connexion);
        btnConnexion.setText("Déconnexion");
        btnConnexion.setOnClickListener(v -> {
            prefs.edit().remove("username").apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    // 🌐 APPEL API
    private void loadServicesFromApi() {

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getServices().enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<Service> services = response.body();

                    allAnnonces = new ArrayList<>();

                    for (Service s : services) {

                        // 🔥 Conversion Service → Annonce
                        Annonce a = new Annonce(
                                s.idService,
                                s.titre,
                                s.description,
                                s.prixPropose,
                                s.datePublication,
                                "API"
                        );

                        allAnnonces.add(a);
                    }

                    adapter.setAnnonces(allAnnonces);
                } else {
                    Log.e("API_ERROR", "Réponse vide ou erreur");
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    // 🔎 FILTRE
    private void filterAnnonces(String categorie) {
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
}