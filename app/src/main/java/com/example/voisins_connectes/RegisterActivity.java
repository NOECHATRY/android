package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etUsername = findViewById(R.id.et_register_username);
        EditText etEmail = findViewById(R.id.et_register_email);
        EditText etPassword = findViewById(R.id.et_register_password);
        Button btnRegister = findViewById(R.id.btn_register_confirm);
        TextView tvLogin = findViewById(R.id.tv_back_to_login);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                // On crée un membre complet avec les champs attendus par membres.php
                Membre nouveauMembre = new Membre();
                nouveauMembre.setNom(username); 
                nouveauMembre.setPrenom(username); // On met le nom dans prenom aussi pour éviter le champ vide
                nouveauMembre.setEmail(email);
                nouveauMembre.setMotDePasse(password);
                nouveauMembre.setRole("user");
                nouveauMembre.setTelephone("Non renseigné"); // Valeur par défaut non nulle
                nouveauMembre.setAdresse("Non renseignée"); // Valeur par défaut non nulle

                RetrofitClient.getApiService().addMembre(nouveauMembre).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                            SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
                            prefs.edit().putString("username", username).apply();
                            
                            Intent intent = new Intent(RegisterActivity.this, AbonnementActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // On affiche le code erreur pour aider au diagnostic
                            Toast.makeText(RegisterActivity.this, "Erreur API : " + response.code() + ". Vérifiez si l'email existe déjà.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }
}
