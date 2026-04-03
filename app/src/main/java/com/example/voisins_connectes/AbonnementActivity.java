package com.example.voisins_connectes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AbonnementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abonnement);

        Button btnPremium = findViewById(R.id.btn_choisir_premium);
        Button btnGratuit = findViewById(R.id.btn_continuer_gratuit);

        btnPremium.setOnClickListener(v -> {
            Toast.makeText(this, "Félicitations ! Vous êtes maintenant Premium.", Toast.LENGTH_LONG).show();
            allerAAccueil();
        });

        btnGratuit.setOnClickListener(v -> {
            allerAAccueil();
        });
    }

    private void allerAAccueil() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}