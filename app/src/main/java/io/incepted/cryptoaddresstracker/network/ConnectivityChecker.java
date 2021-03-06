package io.incepted.cryptoaddresstracker.network;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.incepted.cryptoaddresstracker.CryptoAddressTracker;
import timber.log.Timber;

public class ConnectivityChecker {

    public static boolean checkConnectionManually(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (manager != null) {
            activeNetwork = manager.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static boolean isConnected(Application application) {
        NetworkLiveData networkLiveData = ((CryptoAddressTracker) application).getNetworkLiveData();
        if (networkLiveData.getValue() != null && networkLiveData.hasActiveObservers()) {
            return networkLiveData.getValue();
        } else {
            return checkConnectionManually(application);
        }
    }
}

