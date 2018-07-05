package io.incepted.cryptoaddresstracker.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import io.incepted.cryptoaddresstracker.Activities.SettingsActivity;
import io.incepted.cryptoaddresstracker.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.app_preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String darkThemeKey = getResources().getString(R.string.pref_key_dark_theme);

        boolean darkTheme = sharedPreferences.getBoolean(darkThemeKey, false);
    }
}
