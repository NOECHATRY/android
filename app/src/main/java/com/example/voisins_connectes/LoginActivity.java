package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        if (prefs.contains("token")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        EditText etEmail = findViewById(R.id.et_login_username); // Note: id should ideally be et_login_email
        Button btnLogin = findViewById(R.id.btn_login_confirm);
        TextView tvRegister = findViewById(R.id.tv_go_to_register);

        // On va réutiliser le champ existant pour l'email pour ne pas casser le layout
        etEmail.setHint("Adresse e-mail");

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            // Pour l'instant, on demande juste l'email pour simplifier, 
            // mais l'API attend aussi un mot de passe.
            // On va ajouter un dialogue ou utiliser un mot de passe par défaut pour le test
            // ou mieux : modifier l'UI pour ajouter le champ mot de passe.
            
            if (email.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer votre e-mail", Toast.LENGTH_SHORT).show();
                return;
            }

            performLogin(email, "1234"); // Mot de passe fictif pour l'instant
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    // Dans LoginActivity.java, remplacez performLogin par :
    private void performLogin(String email, String password) {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("motDePasse", password);

        RetrofitClient.getApiService().login("login", credentials).enqueue(new Callback<LoginResponse>() {
            // ... reste du code
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginRes = response.body();
                    if (loginRes.getError() != null) {
                        Toast.makeText(LoginActivity.this, loginRes.getError(), Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
                        prefs.edit()
                            .putString("token", loginRes.getToken())
                            .putInt("idMembre", loginRes.getInfosMembre().getIdMembre())
                            .putString("username", loginRes.getInfosMembre().getPrenom())
                            .apply();
                        
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
