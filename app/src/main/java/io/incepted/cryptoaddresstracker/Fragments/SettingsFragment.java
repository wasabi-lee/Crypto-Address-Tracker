package io.incepted.cryptoaddresstracker.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import io.incepted.cryptoaddresstracker.Activities.SettingsActivity;
import io.incepted.cryptoaddresstracker.Listeners.OnThemeChangedListener;
import io.incepted.cryptoaddresstracker.R;

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
        mCallback.onThemeChanged();
    }
}
