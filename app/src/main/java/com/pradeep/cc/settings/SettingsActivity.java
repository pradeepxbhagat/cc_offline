package com.pradeep.cc.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.Toast;

import com.pradeep.cc.R;

public class SettingsActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            EditTextPreference txtBillingCycle = (EditTextPreference) findPreference(getString(R.string.key_billing_cycle_date));
            EditTextPreference txtBudgetAmount = (EditTextPreference) findPreference(getString(R.string.key_budget_amount));

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int billingCycleDay = Integer.parseInt(prefs.getString(getString(R.string.key_billing_cycle_date), "1"));
            setBillingCycleSummary(txtBillingCycle, billingCycleDay);

            float budgetAmount = Float.parseFloat(prefs.getString(getString(R.string.key_budget_amount), "0"));
            setBudgetSummary(txtBudgetAmount, budgetAmount);
            txtBillingCycle.setOnPreferenceChangeListener(onPreferenceChangeListener);
            txtBudgetAmount.setOnPreferenceChangeListener(onPreferenceChangeListener);
        }

        private void setBillingCycleSummary(EditTextPreference preference, int billingCycleDay) {
            preference.setSummary(getString(R.string.default_billing_cycle_summary_1) +
                    " " + billingCycleDay + getString(R.string.default_billing_cycle_summary_2));
        }

        private void setBudgetSummary(EditTextPreference preference, float budget) {
            preference.setSummary("Rs. " + budget + " is set as your budget");
        }

        private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference instanceof EditTextPreference) {
                    if (preference.getKey().equals(getString(R.string.key_billing_cycle_date))) {
                        int day = Integer.parseInt(newValue.toString());
                        if (day <= 0 || day > 31) {
                            Toast.makeText(getActivity(), "Date should be between 1 and 31", Toast.LENGTH_LONG).show();
                            return false;
                        }
                        setBillingCycleSummary((EditTextPreference) preference, day);
                    } else if (preference.getKey().equals(getString(R.string.key_budget_amount))) {
                        Float amount = Float.parseFloat(newValue.toString());
                        setBudgetSummary((EditTextPreference) preference,amount);
                    }

                    return true;
                }
                return true;
            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
