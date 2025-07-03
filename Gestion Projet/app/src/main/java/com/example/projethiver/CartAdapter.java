package com.example.projethiver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends ArrayAdapter<Produit> {
    public CartAdapter(Context context, List<Produit> produits) {
        super(context, 0, produits);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Produit produit = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_panier, parent, false);
        }

        TextView tvNom = convertView.findViewById(R.id.tv_nom_panier);
        TextView tvPrixUnitaire = convertView.findViewById(R.id.tv_prix_unitaire);
        TextView tvQuantite = convertView.findViewById(R.id.tv_quantite);
        TextView tvPrixTotal = convertView.findViewById(R.id.tv_prix_total);

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CANADA_FRENCH);

        tvNom.setText(produit.getNom());
        tvPrixUnitaire.setText(format.format(produit.getPrix()));
        tvQuantite.setText(String.valueOf(produit.getQuantite()));
        tvPrixTotal.setText(format.format(produit.getPrixTotal()));

        return convertView;
    }
}
