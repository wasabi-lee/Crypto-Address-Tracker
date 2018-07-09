package io.incepted.cryptoaddresstracker.Network;

import android.annotation.SuppressLint;
import android.util.Log;

import io.incepted.cryptoaddresstracker.Network.NetworkModel.CurrentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.Utils.SharedPreferenceHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PriceFetcher {

    private static final String TAG = PriceFetcher.class.getSimpleName();

    private static final String FSYM = "ETH";

    public interface OnPriceLoadedListener {
        void onPriceLoaded(CurrentPrice currentPrice);

        void onError(Throwable throwable);
    }

    @SuppressLint("CheckResult")
    public static void loadCurrentPrice(String tsym, OnPriceLoadedListener callback) {
        Log.d(TAG, "loadCurrentPrice: " + FSYM + "/" + tsym);
        NetworkManager.getCurrentPrice().getCurrentPrice(FSYM, tsym)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onPriceLoaded,
                        throwable -> {
                            throwable.printStackTrace();
                            callback.onError(throwable);
                        });
    }

}
