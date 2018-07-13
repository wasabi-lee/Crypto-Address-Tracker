package io.incepted.cryptoaddresstracker.Network;

import android.annotation.SuppressLint;
import android.util.Log;

import io.incepted.cryptoaddresstracker.Network.NetworkModel.CurrentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.Utils.SharedPreferenceHelper;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PriceFetcher {

    private static final String TAG = PriceFetcher.class.getSimpleName();

    private static final String FSYM = "ETH";

    private static NetworkService mApiCallObservable = NetworkManager.getCurrentPrice(NetworkManager.BASE_URL_CRYPTOCOMPARE);

    public interface OnPriceLoadedListener {
        void onPriceLoaded(CurrentPrice currentPrice);

        void onError(Throwable throwable);
    }

    @SuppressLint("CheckResult")
    public static void loadCurrentPrice(String tsym, Scheduler bkgdScheduler,
                                        Scheduler mainScheduler, OnPriceLoadedListener callback) {

        mApiCallObservable.getCurrentPrice(FSYM, tsym)
                .subscribeOn(bkgdScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onPriceLoaded,
                        throwable -> {
                            throwable.printStackTrace();
                            callback.onError(throwable);
                        });
    }

    public static void setApiCallObservable(NetworkService mApiCallObservable) {
        PriceFetcher.mApiCallObservable = mApiCallObservable;
    }
}
