package io.incepted.cryptoaddresstracker;

import android.app.Application;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;
import io.incepted.cryptoaddresstracker.network.NetworkLiveData;
import timber.log.Timber;

import static android.util.Log.INFO;

public class CryptoAddressTracker extends MultiDexApplication {

    private NetworkLiveData networkLiveData;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        networkLiveData = new NetworkLiveData(this);
    }

    public NetworkLiveData getNetworkLiveData() {
        return networkLiveData;
    }
}
