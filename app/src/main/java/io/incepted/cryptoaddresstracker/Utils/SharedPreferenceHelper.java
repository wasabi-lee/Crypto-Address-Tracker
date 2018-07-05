package io.incepted.cryptoaddresstracker.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import io.incepted.cryptoaddresstracker.R;

public class SharedPreferenceHelper {

    public static SharedPreferences getSharedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean getThemeFlag(Context context) {
        return getSharedPref(context)
                .getBoolean(context.getString(R.string.pref_key_dark_theme), false);
    }

}
