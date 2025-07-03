package com.example.projethiver;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddProduitActivity extends AppCompatActivity {
    private EditText etNom, etDescription, etPrix;
    private Spinner spinnerCategorie, spinnerTaxe;
    private RadioGroup rgEtat, rgTaxable;
    private Button btnSauvegarder, btnAnnuler;
    private EditText etDateAjout;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_produit);

        etNom = findViewById(R.id.et_nom_produit);
        etDescription = findViewById(R.id.et_description_produit);
        etPrix = findViewById(R.id.et_prix_produit);
        spinnerCategorie = findViewById(R.id.spinner_categorie);
        spinnerTaxe = findViewById(R.id.spinner_taxe);
        rgEtat = findViewById(R.id.rg_etat);
        rgTaxable = findViewById(R.id.rg_taxable);
        btnSauvegarder = findViewById(R.id.btn_sauvegarder);
        btnAnnuler = findViewById(R.id.btn_annuler);
        etDateAjout = findViewById(R.id.et_date_ajout);

        // Configurer les Spinners
        ArrayAdapter<CharSequence> categorieAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        categorieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorie.setAdapter(categorieAdapter);

        ArrayAdapter<CharSequence> taxeAdapter = ArrayAdapter.createFromResource(this,
                R.array.taxes_array, android.R.layout.simple_spinner_item);
        taxeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaxe.setAdapter(taxeAdapter);

        // Gestion de l'état du produit (neuf/usagé)
        rgEtat.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_usage) {
                rgTaxable.setVisibility(View.GONE);
                spinnerTaxe.setVisibility(View.GONE);
            } else {
                rgTaxable.setVisibility(View.VISIBLE);
                spinnerTaxe.setVisibility(View.VISIBLE);
            }
        });

        // Gestion de la date
        etDateAjout.setOnClickListener(v -> showDatePickerDialog());

        // Récupérer les données si modification
        Intent intent = getIntent();
        if (intent.hasExtra("position")) {
            position = intent.getIntExtra("position", -1);
            etNom.setText(intent.getStringExtra("nom"));
            etDescription.setText(intent.getStringExtra("description"));
            etPrix.setText(String.valueOf(intent.getDoubleExtra("prix", 0)));

            // Sélectionner la catégorie
            String categorie = intent.getStringExtra("categorie");
            for (int i = 0; i < spinnerCategorie.getCount(); i++) {
                if (spinnerCategorie.getItemAtPosition(i).toString().equals(categorie)) {
                    spinnerCategorie.setSelection(i);
                    break;
                }
            }

            etDateAjout.setText(intent.getStringExtra("date_ajout"));

            boolean estNeuf = intent.getBooleanExtra("est_neuf", true);
            if (estNeuf) {
                rgEtat.check(R.id.rb_neuf);
            } else {
                rgEtat.check(R.id.rb_usage);
            }

            boolean estTaxable = intent.getBooleanExtra("est_taxable", true);
            if (estTaxable) {
                rgTaxable.check(R.id.rb_taxable);
            } else {
                rgTaxable.check(R.id.rb_non_taxable);
            }

            String tauxTaxe = intent.getStringExtra("taux_taxe");
            for (int i = 0; i < spinnerTaxe.getCount(); i++) {
                if (spinnerTaxe.getItemAtPosition(i).toString().equals(tauxTaxe)) {
                    spinnerTaxe.setSelection(i);
                    break;
                }
            }
        }

        btnSauvegarder.setOnClickListener(v -> sauvegarderProduit());
        btnAnnuler.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    etDateAjout.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void sauvegarderProduit() {
        String nom = etNom.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String prixString = etPrix.getText().toString().trim();
        String categorie = spinnerCategorie.getSelectedItem().toString();
        String dateAjout = etDateAjout.getText().toString().trim();

        if (nom.isEmpty() || description.isEmpty() || prixString.isEmpty() || dateAjout.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        double prix;
        try {
            prix = Double.parseDouble(prixString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Prix invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean estNeuf = rgEtat.getCheckedRadioButtonId() == R.id.rb_neuf;
        boolean estTaxable = estNeuf && rgTaxable.getCheckedRadioButtonId() == R.id.rb_taxable;
        String tauxTaxe = estTaxable ? spinnerTaxe.getSelectedItem().toString() : "Non taxable";

        Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.putExtra("nom", nom);
        intent.putExtra("description", description);
        intent.putExtra("prix", prix);
        intent.putExtra("categorie", categorie);
        intent.putExtra("date_ajout", dateAjout);
        intent.putExtra("est_neuf", estNeuf);
        intent.putExtra("est_taxable", estTaxable);
        intent.putExtra("taux_taxe", tauxTaxe);

        setResult(RESULT_OK, intent);
        finish();
    }
}
