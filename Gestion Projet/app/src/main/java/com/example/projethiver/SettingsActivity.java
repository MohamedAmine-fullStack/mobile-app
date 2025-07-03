package com.example.projethiver;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AppSettings";
    private static final String PREF_TAX_MODE = "TaxMode";
    private static final String PREF_DEFAULT_CATEGORY = "DefaultCategory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RadioGroup rgTaxMode = findViewById(R.id.rg_tax_mode);
        RadioGroup rgDefaultCategory = findViewById(R.id.rg_default_category);

        // Charger les préférences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int taxMode = prefs.getInt(PREF_TAX_MODE, 2); // Par défaut: TPS+TVQ
        int defaultCategory = prefs.getInt(PREF_DEFAULT_CATEGORY, 0); // Par défaut: Électronique

        rgTaxMode.check(getTaxModeId(taxMode));
        rgDefaultCategory.check(getDefaultCategoryId(defaultCategory));

        // Sauvegarder les changements
        rgTaxMode.setOnCheckedChangeListener((group, checkedId) -> {
            int mode = getTaxModeFromId(checkedId);
            prefs.edit().putInt(PREF_TAX_MODE, mode).apply();
        });

        rgDefaultCategory.setOnCheckedChangeListener((group, checkedId) -> {
            int category = getDefaultCategoryFromId(checkedId);
            prefs.edit().putInt(PREF_DEFAULT_CATEGORY, category).apply();
        });
    }

    private int getTaxModeId(int mode) {
        switch (mode) {
            case 0: return R.id.rb_tps_only;
            case 1: return R.id.rb_tvq_only;
            case 2: return R.id.rb_both_taxes;
            default: return R.id.rb_both_taxes;
        }
    }

    private int getTaxModeFromId(int id) {
        if (id == R.id.rb_tps_only) return 0;
        if (id == R.id.rb_tvq_only) return 1;
        return 2; // both
    }

    private int getDefaultCategoryId(int category) {
        switch (category) {
            case 0: return R.id.rb_elec;
            case 1: return R.id.rb_food;
            case 2: return R.id.rb_clothes;
            case 3: return R.id.rb_furniture;
            default: return R.id.rb_elec;
        }
    }

    private int getDefaultCategoryFromId(int id) {
        if (id == R.id.rb_elec) return 0;
        if (id == R.id.rb_food) return 1;
        if (id == R.id.rb_clothes) return 2;
        if (id == R.id.rb_furniture) return 3;
        return 0;
    }
}
