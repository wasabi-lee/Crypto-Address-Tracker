package io.incepted.cryptoaddresstracker.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import timber.log.Timber;

public class ConnectivityChecker {

    public static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (manager != null) {
            activeNetwork = manager.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }

}
