package io.incepted.cryptoaddresstracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import io.incepted.cryptoaddresstracker.R;

public class SharedPreferenceHelper {

    public static SharedPreferences getSharedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean getThemeFlag(Context context) {
        return getSharedPref(context)
                .getBoolean(context.getString(R.string.pref_key_dark_theme), false);
    }

    public static int getBaseCurrencyPrefValue(Context context) {

        String baseCurrencySetting = getSharedPref(context)
                .getString(context.getString(R.string.pref_key_base_currency), "0");

        return Integer.valueOf(baseCurrencySetting);
    }

    public static boolean getIsLastShownCurrencyEth(Context context) {
        return getSharedPref(context)
                .getBoolean(context.getString(R.string.pref_key_last_currency_setting_show_eth), true);
    }

    public static void writeIsLastShownCurrencyEth(Context context, boolean isLastShownCurrencyEth) {
        SharedPreferences sharedPreferences = SharedPreferenceHelper.getSharedPref(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.pref_key_last_currency_setting_show_eth), isLastShownCurrencyEth);
        editor.apply();
    }

}
