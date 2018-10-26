package io.incepted.cryptoaddresstracker;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;
import timber.log.Timber;

import static android.util.Log.INFO;

public class CryptoAddressTracker extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

}
