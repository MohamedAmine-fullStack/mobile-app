package com.example.projethiver;

import java.io.Serializable;

public class Produit implements Serializable {
    private String nom;
    private String description;
    private double prix;
    private String categorie;
    private String dateAjout;
    private boolean estNeuf;
    private boolean estTaxable;
    private String tauxTaxe;
    private int quantite;

    public Produit(String nom, String description, double prix, String categorie, String dateAjout,
                   boolean estNeuf, boolean estTaxable, String tauxTaxe) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.categorie = categorie;
        this.dateAjout = dateAjout;
        this.estNeuf = estNeuf;
        this.estTaxable = estTaxable;
        this.tauxTaxe = tauxTaxe;
        this.quantite = 1;
    }


    // Getters et setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getDateAjout() { return dateAjout; }
    public void setDateAjout(String dateAjout) { this.dateAjout = dateAjout; }

    public boolean isEstNeuf() { return estNeuf; }
    public void setEstNeuf(boolean estNeuf) { this.estNeuf = estNeuf; }

    public boolean isEstTaxable() { return estTaxable; }
    public void setEstTaxable(boolean estTaxable) { this.estTaxable = estTaxable; }

    public String getTauxTaxe() { return tauxTaxe; }
    public void setTauxTaxe(String tauxTaxe) { this.tauxTaxe = tauxTaxe; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    // Calcul du prix total avec taxes pour le Québec
    public double getPrixTotal() {
        double prixUnitaire = prix;
        if (estTaxable) {
            double taux = 0.0;
            if (tauxTaxe.equals("TPS (5%)")) {
                taux = 0.05;
            } else if (tauxTaxe.equals("TVQ (9.975%)")) {
                taux = 0.09975;
            } else if (tauxTaxe.equals("TPS+TVQ (14.975%)")) {
                taux = 0.14975;
            }
            prixUnitaire *= (1 + taux);
        }
        return prixUnitaire * quantite;
    }
}