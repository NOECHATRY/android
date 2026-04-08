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

        EditText etIdentifiant = findViewById(R.id.et_register_username);
        EditText etEmail = findViewById(R.id.et_register_email);
        EditText etPassword = findViewById(R.id.et_register_password);
        Button btnRegister = findViewById(R.id.btn_register_confirm);
        TextView tvLogin = findViewById(R.id.tv_back_to_login);

        btnRegister.setOnClickListener(v -> {
            String identifiant = etIdentifiant.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (identifiant.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                // On enregistre l'Identifiant dans le champ 'email' car c'est lui qui sert au login
                Membre nouveauMembre = new Membre();
                nouveauMembre.setEmail(identifiant); 
                nouveauMembre.setNom(email); // On stocke l'email réel dans le nom
                nouveauMembre.setPrenom("");
                nouveauMembre.setMotDePasse(password);
                nouveauMembre.setRole("demandeur");
                nouveauMembre.setTelephone("Non renseigné");
                nouveauMembre.setAdresse("Non renseignée");

                RetrofitClient.getApiService().addMembre(nouveauMembre).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Compte créé ! Connectez-vous avec votre identifiant.", Toast.LENGTH_LONG).show();
                            finish(); // Retour à la page de login
                        } else {
                            Toast.makeText(RegisterActivity.this, "Erreur : cet identifiant est peut-être déjà pris.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}
