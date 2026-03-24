package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Vérifier si déjà connecté
        SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        if (prefs.contains("username")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.et_login_username);
        Button btnLogin = findViewById(R.id.btn_login_confirm);
        TextView tvRegister = findViewById(R.id.tv_go_to_register);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer un nom", Toast.LENGTH_SHORT).show();
            } else {
                // Sauvegarder le nom
                prefs.edit().putString("username", username).apply();
                
                // Aller vers l'écran principal
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        tvRegister.setOnClickListener(v -> {
            // Pour l'instant on fait la même chose que le login pour simplifier
            Toast.makeText(this, "Fonctionnalité d'inscription bientôt disponible", Toast.LENGTH_SHORT).show();
        });
    }
}