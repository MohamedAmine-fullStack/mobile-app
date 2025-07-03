package com.example.projethiver;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Panier {
    private static Panier instance;
    private ArrayList<Produit> produits;

    private Panier() {
        produits = new ArrayList<>();
    }

    public static synchronized Panier getInstance() {
        if (instance == null) {
            instance = new Panier();
        }
        return instance;
    }

    public ArrayList<Produit> getProduits() {
        return new ArrayList<>(produits);
    }

    public void ajouterProduit(Produit produit) {
        // Vérifier si le produit existe déjà
        for (Produit p : produits) {
            if (p.getNom().equals(produit.getNom()) &&
                    p.getDateAjout().equals(produit.getDateAjout())) {
                p.setQuantite(p.getQuantite() + 1);
                return;
            }
        }
        produits.add(produit);
    }

    public void supprimerProduit(Produit produit) {
        produits.remove(produit);
    }

    public void modifierProduit(Produit produit) {
        for (int i = 0; i < produits.size(); i++) {
            if (produits.get(i).getNom().equals(produit.getNom())){
                produits.set(i, produit);
                break;
            }
        }
    }

    public double getTotalSansTaxes() {
        double total = 0;
        for (Produit p : produits) {
            total += p.getPrix() * p.getQuantite();
        }
        return total;
    }

    public double getTotalAvecTaxes() {
        double total = 0;
        for (Produit p : produits) {
            total += p.getPrixTotal();
        }
        return total;
    }

    public void viderPanier() {
        produits.clear();
    }

    public void savePanier(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("panier_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> produitsSet = new HashSet<>();
        for (Produit p : produits) {
            produitsSet.add(p.getNom() + "|" + p.getDescription() + "|" + p.getPrix() + "|" +
                    p.getCategorie() + "|" + p.getDateAjout() + "|" + p.isEstNeuf() + "|" +
                    p.isEstTaxable() + "|" + p.getTauxTaxe() + "|" + p.getQuantite());
        }

        editor.putStringSet("produits", produitsSet);
        editor.apply();
    }

    public void loadPanier(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("panier_prefs", Context.MODE_PRIVATE);
        Set<String> produitsSet = sharedPreferences.getStringSet("produits", new HashSet<>());

        produits.clear();
        for (String s : produitsSet) {
            String[] parts = s.split("\\|");
            if (parts.length == 9) {
                Produit p = new Produit(
                        parts[0], parts[1], Double.parseDouble(parts[2]), parts[3], parts[4],
                        Boolean.parseBoolean(parts[5]), Boolean.parseBoolean(parts[6]), parts[7]
                );
                p.setQuantite(Integer.parseInt(parts[8]));
                produits.add(p);
            }
        }
    }
}
