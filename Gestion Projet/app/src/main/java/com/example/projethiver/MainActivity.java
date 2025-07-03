    package com.example.projethiver;

    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.FrameLayout;
    import android.widget.ListView;
    import android.widget.RadioButton;
    import android.widget.RadioGroup;
    import android.widget.RelativeLayout;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;

    import com.google.android.material.floatingactionbutton.FloatingActionButton;

    import java.text.NumberFormat;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.Comparator;
    import java.util.Locale;

    public class MainActivity extends AppCompatActivity {
        private ListView produitListView;
        private FloatingActionButton fab;
        private ArrayList<Produit> produits;
        private ProduitAdaptateur adaptateur;
        private boolean sortByName = true;
        private boolean sortAscending = true;
        private TextView tvBadge;

        private final ActivityResultLauncher<Intent> addProduitActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        int position = data.getIntExtra("position", -1);
                        String nom = data.getStringExtra("nom");
                        String description = data.getStringExtra("description");
                        double prix = data.getDoubleExtra("prix", 0);
                        String categorie = data.getStringExtra("categorie");
                        String dateAjout = data.getStringExtra("date_ajout");
                        boolean estNeuf = data.getBooleanExtra("est_neuf", true);
                        boolean estTaxable = data.getBooleanExtra("est_taxable", true);
                        String tauxTaxe = data.getStringExtra("taux_taxe");

                        if (position == -1) {
                            Produit nouveauProduit = new Produit(nom, description, prix, categorie,
                                    dateAjout, estNeuf, estTaxable, tauxTaxe);
                            produits.add(nouveauProduit);
                        } else {
                            Produit produit = produits.get(position);
                            produit.setNom(nom);
                            produit.setDescription(description);
                            produit.setPrix(prix);
                            produit.setCategorie(categorie);
                            produit.setDateAjout(dateAjout);
                            produit.setEstNeuf(estNeuf);
                            produit.setEstTaxable(estTaxable);
                            produit.setTauxTaxe(tauxTaxe);

                            Panier.getInstance().modifierProduit(produit);
                            Panier.getInstance().savePanier(this);
                        }

                        sortProduits();
                        saveData();
                        updateBadge();
                    }
                }
        );

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // Initialisation des vues
            produitListView = findViewById(R.id.produit_list_view);
            fab = findViewById(R.id.fab);
            tvBadge = findViewById(R.id.tv_badge);

            // Configuration de la liste et de l'adaptateur
            produits = new ArrayList<>();
            adaptateur = new ProduitAdaptateur(this, produits);
            produitListView.setAdapter(adaptateur);

            // Bouton d'ajout de produit
            fab.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, AddProduitActivity.class);
                addProduitActivityResultLauncher.launch(intent);
            });

            // Clic sur un produit pour modification
            produitListView.setOnItemClickListener((parent, view, position, id) -> {
                Produit produit = produits.get(position);
                Intent intent = new Intent(MainActivity.this, AddProduitActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("nom", produit.getNom());
                intent.putExtra("description", produit.getDescription());
                intent.putExtra("prix", produit.getPrix());
                intent.putExtra("categorie", produit.getCategorie());
                intent.putExtra("date_ajout", produit.getDateAjout());
                intent.putExtra("est_neuf", produit.isEstNeuf());
                intent.putExtra("est_taxable", produit.isEstTaxable());
                intent.putExtra("taux_taxe", produit.getTauxTaxe());
                addProduitActivityResultLauncher.launch(intent);
            });

            // Clic long pour suppression
            produitListView.setOnItemLongClickListener((parent, view, position, id) -> {
                Produit produit = produits.get(position);
                showDeleteConfirmationDialog(produit, position);
                return true;
            });

            // Configuration du tri
            setupSortingControls();

            // Chargement des données
            loadData();
            Panier.getInstance().loadPanier(this);
            updateBadge();
        }

        private void setupSortingControls() {
            Spinner spinnerFilter = findViewById(R.id.spinner_filter);
            spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sortByName = position == 0;
                    sortProduits();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            RadioGroup radioGroupOrder = findViewById(R.id.radio_group_order);
            radioGroupOrder.setOnCheckedChangeListener((group, checkedId) -> {
                sortAscending = checkedId == R.id.radio_asc;
                sortProduits();
            });

            FrameLayout cartLayout = findViewById(R.id.cart_badge_layout);
            cartLayout.setOnClickListener(view -> {
                startActivity(new Intent(this, PanierActivity.class));
            });

            // Valeurs par défaut
            spinnerFilter.setSelection(0);
            RadioButton radioAsc = findViewById(R.id.radio_asc);
            radioAsc.setChecked(true);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            } else if (id == R.id.action_devis) {
                genererDevis();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        private void genererDevis() {
            if (Panier.getInstance().getProduits().isEmpty()) {
                Toast.makeText(this, "Le panier est vide", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Devis client");
            shareIntent.putExtra(Intent.EXTRA_TEXT, creerTexteDevis());
            startActivity(Intent.createChooser(shareIntent, "Partager le devis"));
        }

        private String creerTexteDevis() {
            StringBuilder sb = new StringBuilder();
            sb.append("DEVIS CLIENT\n\n");
            sb.append("Date: ").append(java.text.DateFormat.getDateTimeInstance().format(new java.util.Date())).append("\n\n");
            sb.append("Articles:\n");

            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CANADA_FRENCH);

            for (Produit p : Panier.getInstance().getProduits()) {
                sb.append("- ").append(p.getNom())
                        .append(" (").append(p.getQuantite()).append("x)")
                        .append(": ").append(format.format(p.getPrixTotal()))
                        .append("\n");
            }

            sb.append("\nSous-total: ").append(format.format(Panier.getInstance().getTotalSansTaxes()));
            sb.append("\nTaxes (Québec): ").append(format.format(Panier.getInstance().getTotalAvecTaxes() - Panier.getInstance().getTotalSansTaxes()));
            sb.append("\nTOTAL: ").append(format.format(Panier.getInstance().getTotalAvecTaxes()));

            return sb.toString();
        }

        private void sortProduits() {
            Comparator<Produit> comparator = sortByName ?
                    Comparator.comparing(Produit::getNom) :
                    Comparator.comparingDouble(Produit::getPrix);

            if (!sortAscending) {
                comparator = comparator.reversed();
            }

            Collections.sort(produits, comparator);
            adaptateur.notifyDataSetChanged();
        }

        private void showDeleteConfirmationDialog(Produit produit, int position) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation de suppression")
                    .setMessage("Êtes-vous sûr de vouloir supprimer ce produit ?")
                    .setPositiveButton("Oui", (dialog, which) -> supprimerProduit(produit, position))
                    .setNegativeButton("Non", null)
                    .show();
        }

        private void supprimerProduit(Produit produit, int position) {
            produits.remove(position);
            adaptateur.notifyDataSetChanged();
            saveData();

            Panier.getInstance().supprimerProduit(produit);
            Panier.getInstance().savePanier(this);
            updateBadge();
        }

        public void updateBadge() {
            int count = Panier.getInstance().getProduits().size();
            if (count > 0) {
                tvBadge.setText(String.valueOf(count));
                tvBadge.setVisibility(View.VISIBLE);
            } else {
                tvBadge.setVisibility(View.GONE);
            }
        }

        private void saveData() {
            SharedPreferences sharedPreferences = getSharedPreferences("produits_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("produit_count", produits.size());


            for (int i = 0; i < produits.size(); i++) {
                Produit produit = produits.get(i);
                editor.putString("produit_" + i + "_nom", produit.getNom());
                editor.putString("produit_" + i + "_description", produit.getDescription());
                editor.putFloat("produit_" + i + "_prix", (float) produit.getPrix());
                editor.putString("produit_" + i + "_categorie", produit.getCategorie());
                editor.putString("produit_" + i + "_dateAjout", produit.getDateAjout());
                editor.putBoolean("produit_" + i + "_estNeuf", produit.isEstNeuf());
                editor.putBoolean("produit_" + i + "_estTaxable", produit.isEstTaxable());
                editor.putString("produit_" + i + "_tauxTaxe", produit.getTauxTaxe());
                editor.putInt("produit_" + i + "_quantite", produit.getQuantite());
            }
            editor.apply();
        }

        private void loadData() {
            SharedPreferences sharedPreferences = getSharedPreferences("produits_prefs", MODE_PRIVATE);
            int produitCount = sharedPreferences.getInt("produit_count", 0);
            produits = new ArrayList<>();

            for (int i = 0; i < produitCount; i++) {
                int quantite = sharedPreferences.getInt("produit_" + i + "_quantite", 1);
                String nom = sharedPreferences.getString("produit_" + i + "_nom", "");
                String description = sharedPreferences.getString("produit_" + i + "_description", "");
                float prix = sharedPreferences.getFloat("produit_" + i + "_prix", 0);
                String categorie = sharedPreferences.getString("produit_" + i + "_categorie", "");
                String dateAjout = sharedPreferences.getString("produit_" + i + "_dateAjout", "");
                boolean estNeuf = sharedPreferences.getBoolean("produit_" + i + "_estNeuf", true);
                boolean estTaxable = sharedPreferences.getBoolean("produit_" + i + "_estTaxable", true);
                String tauxTaxe = sharedPreferences.getString("produit_" + i + "_tauxTaxe", "TPS+TVQ (14.975%)");


                Produit produit = new Produit(nom, description, prix, categorie,
                        dateAjout, estNeuf, estTaxable, tauxTaxe);
                produit.setQuantite(quantite);
                produits.add(produit);
            }

            adaptateur = new ProduitAdaptateur(this, produits);
            produitListView.setAdapter(adaptateur);
            updateBadge();
        }

        @Override
        protected void onResume() {
            super.onResume();
            Panier.getInstance().loadPanier(this);
            updateBadge();
        }
    }