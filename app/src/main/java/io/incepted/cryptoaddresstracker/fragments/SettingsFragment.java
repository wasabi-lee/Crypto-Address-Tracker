package io.incepted.cryptoaddresstracker.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.activities.SettingsActivity;
import io.incepted.cryptoaddresstracker.listeners.OnThemeChangedListener;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private OnThemeChangedListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnThemeChangedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getPackageName() + " must implement OnThemeChangedListener");
        }
    }

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
        if (key.equals(getString(R.string.pref_key_dark_theme))) {
            mCallback.onThemeChanged(sharedPreferences.getBoolean(key, false));
        }
    }
}
