package com.example.projethiver;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PanierActivity extends AppCompatActivity {
    private ListView panierListView;
    private TextView tvTotalSansTaxes, tvTotalAvecTaxes, tvMontantTaxes;
    private Button btnViderPanier;
    private ProduitAdaptateur adaptateur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        panierListView = findViewById(R.id.panier_list_view);
        tvTotalSansTaxes = findViewById(R.id.tv_total_sans_taxes);
        tvTotalAvecTaxes = findViewById(R.id.tv_total_avec_taxes);
        tvMontantTaxes = findViewById(R.id.tv_montant_taxes);
        btnViderPanier = findViewById(R.id.btn_vider_panier);

        Panier.getInstance().loadPanier(this);
        ArrayList<Produit> produitsPanier = Panier.getInstance().getProduits();

        adaptateur = new ProduitAdaptateur(this, produitsPanier);
        panierListView.setAdapter(adaptateur);

        updateTotals();

        panierListView.setOnItemClickListener((parent, view, position, id) -> {
            Produit produit = produitsPanier.get(position);
            produit.setQuantite(produit.getQuantite() + 1);
            Panier.getInstance().savePanier(this);
            adaptateur.notifyDataSetChanged();
            updateTotals();
        });

        panierListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Produit produit = produitsPanier.get(position);
            if (produit.getQuantite() > 1) {
                produit.setQuantite(produit.getQuantite() - 1);
            } else {
                Panier.getInstance().supprimerProduit(produit);
            }
            Panier.getInstance().savePanier(this);
            adaptateur.notifyDataSetChanged();
            updateTotals();
            return true;
        });

        btnViderPanier.setOnClickListener(v -> {
            Panier.getInstance().viderPanier();
            Panier.getInstance().savePanier(this);
            adaptateur.notifyDataSetChanged();
            updateTotals();
        });
    }

    private void updateTotals() {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CANADA_FRENCH);

        double totalSansTaxes = Panier.getInstance().getTotalSansTaxes();
        double totalAvecTaxes = Panier.getInstance().getTotalAvecTaxes();
        double montantTaxes = totalAvecTaxes - totalSansTaxes;

        tvTotalSansTaxes.setText(format.format(totalSansTaxes));
        tvTotalAvecTaxes.setText(format.format(totalAvecTaxes));
        tvMontantTaxes.setText(format.format(montantTaxes));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Panier.getInstance().loadPanier(this);
        adaptateur.notifyDataSetChanged();
        updateTotals();
    }
}

