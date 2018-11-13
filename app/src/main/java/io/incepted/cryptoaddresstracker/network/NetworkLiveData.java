package io.incepted.cryptoaddresstracker.network;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.lifecycle.LiveData;

public class NetworkLiveData extends LiveData<Boolean> {

    private ConnectivityManager connManager;

    public NetworkLiveData(Application application) {
        connManager = (ConnectivityManager) application
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private ConnectivityManager.NetworkCallback networkCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            postValue(true);
        }

        @Override
        public void onLost(Network network) {
            postValue(false);
        }
    };


    @Override
    protected void onActive() {
        super.onActive();
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        postValue(activeNetwork.isConnectedOrConnecting());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connManager.registerNetworkCallback(builder.build(), networkCallback);
        }
    }


    @Override
    protected void onInactive() {
        super.onInactive();
        connManager.unregisterNetworkCallback(networkCallback);
    }
}
