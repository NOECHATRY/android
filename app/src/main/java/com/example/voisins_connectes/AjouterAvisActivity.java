package com.example.voisins_connectes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AjouterAvisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_avis);

        Toolbar toolbar = findViewById(R.id.toolbar_ajouter_avis);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        RatingBar rb = findViewById(R.id.rb_ajouter_avis);
        EditText etCommentaire = findViewById(R.id.et_ajouter_avis_commentaire);
        Button btnPublier = findViewById(R.id.btn_publier_avis);

        btnPublier.setOnClickListener(v -> {
            float note = rb.getRating();
            String commentaire = etCommentaire.getText().toString();

            if (commentaire.isEmpty()) {
                Toast.makeText(this, "Veuillez écrire un commentaire", Toast.LENGTH_SHORT).show();
                return;
            }

            if (note == 0) {
                Toast.makeText(this, "Veuillez donner une note", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            Avis nouvelAvis = new Avis("Utilisateur", commentaire, note, date);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("nouvelAvis", nouvelAvis);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}