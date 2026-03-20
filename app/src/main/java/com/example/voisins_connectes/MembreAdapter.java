package com.example.voisins_connectes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MembreAdapter extends RecyclerView.Adapter<MembreAdapter.MembreViewHolder> {

    private List<Membre> membreList;

    public MembreAdapter(List<Membre> membreList) {
        this.membreList = membreList;
    }

    @NonNull
    @Override
    public MembreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_membre, parent, false);
        return new MembreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembreViewHolder holder, int position) {
        Membre membre = membreList.get(position);
        holder.tvNomPrenom.setText(membre.getNom() + " " + membre.getPrenom());
        holder.tvEmail.setText(membre.getEmail());
        holder.tvTelephone.setText(membre.getTelephone());
    }

    @Override
    public int getItemCount() {
        return membreList != null ? membreList.size() : 0;
    }

    public void setMembres(List<Membre> membres) {
        this.membreList = membres;
        notifyDataSetChanged();
    }

    static class MembreViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomPrenom, tvEmail, tvTelephone;

        public MembreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomPrenom = itemView.findViewById(R.id.tv_nom_prenom);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvTelephone = itemView.findViewById(R.id.tv_telephone);
        }
    }
}