package com.example.projethiver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

public class ProduitAdaptateur extends ArrayAdapter<Produit> {
    public ProduitAdaptateur(Context context, List<Produit> produits) {
        super(context, 0, produits);
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        Produit produit = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_produit, parent, false);
        }

        TextView tvNom = convertView.findViewById(R.id.tv_nom_produit);
        TextView tvPrix = convertView.findViewById(R.id.tv_prix_produit);
        TextView tvDate = convertView.findViewById(R.id.tv_date_produit);
        TextView tvTaxe = convertView.findViewById(R.id.tv_taxe_produit);
        TextView tvQuantite = convertView.findViewById(R.id.tv_quantite);
        Button btnMoins = convertView.findViewById(R.id.btn_moins);
        Button btnPlus = convertView.findViewById(R.id.btn_plus);
        Button btnAjouterPanier = convertView.findViewById(R.id.btn_ajouter_panier);

        tvNom.setText(produit.getNom());
        tvPrix.setText(String.format("%.2f$", produit.getPrix()));
        tvDate.setText(produit.getDateAjout());
        tvTaxe.setText(produit.isEstTaxable() ? "Taxable" : "Non taxable");
        tvQuantite.setText(String.valueOf(produit.getQuantite()));

        btnMoins.setOnClickListener(v -> {
            int qte = produit.getQuantite();
            if (qte > 1) {
                produit.setQuantite(qte - 1);
                tvQuantite.setText(String.valueOf(produit.getQuantite()));
            }
        });

        btnPlus.setOnClickListener(v -> {
            int qte = produit.getQuantite();
            produit.setQuantite(qte + 1);
            tvQuantite.setText(String.valueOf(produit.getQuantite()));
        });

        btnAjouterPanier.setOnClickListener(v -> {
            Panier.getInstance().ajouterProduit(produit);
            Panier.getInstance().savePanier(getContext());
            ((MainActivity)getContext()).updateBadge();
            Toast.makeText(getContext(), "Produit ajouté au panier", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}