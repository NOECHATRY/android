package com.example.voisins_connectes;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import java.util.List;

public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceAdapter.AnnonceViewHolder> {

    private List<Annonce> annonceList;

    public AnnonceAdapter(List<Annonce> annonceList) {
        this.annonceList = annonceList;
    }

    @NonNull
    @Override
    public AnnonceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_annonce, parent, false);
        return new AnnonceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnonceViewHolder holder, int position) {
        Annonce annonce = annonceList.get(position);
        holder.tvTitre.setText(annonce.getTitre());
        holder.tvPrix.setText(String.format("%.0f crédits", annonce.getPrix()));
        holder.tvDescription.setText(annonce.getDescription());
        holder.chipCategorie.setText(annonce.getCategorie());

        // Gestion de l'image selon la catégorie (Uniformisée avec le site)
        String cat = annonce.getCategorie();
        if ("Jardinage".equalsIgnoreCase(cat)) {
            holder.ivAnnonce.setImageResource(R.drawable.jardinage);
        } else if ("Bricolage".equalsIgnoreCase(cat)) {
            holder.ivAnnonce.setImageResource(R.drawable.bricolage);
        } else if ("Cours".equalsIgnoreCase(cat) || "Cours particuliers".equalsIgnoreCase(cat)) {
            holder.ivAnnonce.setImageResource(R.drawable.cours);
        } else if ("Informatique".equalsIgnoreCase(cat)) {
            holder.ivAnnonce.setImageResource(R.drawable.informatique);
        } else {
            holder.ivAnnonce.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        if (annonce.getHoraireReserve() != null) {
            holder.tvHoraires.setVisibility(View.VISIBLE);
            holder.tvHoraires.setText("✅ Réservé : " + annonce.getHoraireReserve());
        } else {
            holder.tvHoraires.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AnnonceDetailActivity.class);
            intent.putExtra("annonce", annonce);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return annonceList != null ? annonceList.size() : 0;
    }

    public void setAnnonces(List<Annonce> annonces) {
        this.annonceList = annonces;
        notifyDataSetChanged();
    }

    static class AnnonceViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre, tvPrix, tvDescription, tvHoraires;
        ImageView ivAnnonce;
        Chip chipCategorie;

        public AnnonceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitre = itemView.findViewById(R.id.tv_titre_annonce);
            tvPrix = itemView.findViewById(R.id.tv_prix_annonce);
            tvDescription = itemView.findViewById(R.id.tv_description_annonce);
            tvHoraires = itemView.findViewById(R.id.tv_horaires_resume);
            ivAnnonce = itemView.findViewById(R.id.iv_annonce_image);
            chipCategorie = itemView.findViewById(R.id.chip_categorie);
        }
    }
}
