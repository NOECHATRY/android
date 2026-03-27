package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voisins_connectes.api.ApiService;
import com.example.voisins_connectes.models.Membre;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔐 Vérifie si déjà connecté
        SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
        if (prefs.contains("username")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // 🔌 Initialisation API
        apiService = RetrofitClient.getApiService();

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login_confirm);
        TextView tvRegister = findViewById(R.id.tv_go_to_register);

        // 🔘 Bouton login
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        // 🔘 Aller vers inscription
        tvRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Fonctionnalité d'inscription bientôt disponible", Toast.LENGTH_SHORT).show();
        });
    }

    private void loginUser(String email, String password) {

        btnLogin.setEnabled(false);
        btnLogin.setText("Connexion...");

        Call<Membre> call = apiService.login("login", email, password);

        call.enqueue(new Callback<Membre>() {

            @Override
            public void onResponse(Call<Membre> call, Response<Membre> response) {

                btnLogin.setEnabled(true);
                btnLogin.setText("Se connecter");

                if (response.isSuccessful() && response.body() != null) {

                    Membre membre = response.body();

                    // 💾 Sauvegarde session
                    SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("username", membre.prenom + " " + membre.nom);
                    editor.putString("email", membre.email);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Bienvenue " + membre.prenom, Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Membre> call, Throwable t) {

                btnLogin.setEnabled(true);
                btnLogin.setText("Se connecter");

                Toast.makeText(LoginActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
}