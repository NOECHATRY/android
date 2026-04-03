package com.example.voisins_connectes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
                // Sauvegarder l'utilisateur dans les préférences
                SharedPreferences prefs = getSharedPreferences("VoisinsConnectes", MODE_PRIVATE);
                prefs.edit().putString("username", username).apply();
                
                // Rediriger vers la page d'abonnement (car c'est la première connexion)
                Intent intent = new Intent(this, AbonnementActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvLogin.setOnClickListener(v -> {
            finish(); // Retourner à la page de connexion
        });
    }
}