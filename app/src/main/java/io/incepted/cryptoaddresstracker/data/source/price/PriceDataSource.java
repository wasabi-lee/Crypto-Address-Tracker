package io.incepted.cryptoaddresstracker.data.source.price;

import android.annotation.SuppressLint;

import io.incepted.cryptoaddresstracker.data.source.address.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.data.source.address.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.network.NetworkManager;
import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.incepted.cryptoaddresstracker.network.deserializer.CurrentPriceDeserializer;
import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.repository.PriceRepository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PriceDataSource {

    private volatile static PriceDataSource INSTANCE = null;

    private static final String FSYM = "ETH";

    private static NetworkService mApiCallObservable =
            NetworkManager.getCustomNetworkService(NetworkManager.BASE_URL_CRYPTOCOMPARE,
                    CurrentPrice.class, new CurrentPriceDeserializer());

    private PriceDataSource() {
    }

    public static PriceDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (PriceDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PriceDataSource();
                }
            }
        }
        return INSTANCE;
    }


    @SuppressLint("CheckResult")
    public void loadCurrentPrice(String tsym,  PriceRepository.OnPriceLoadedListener callback) {
        mApiCallObservable.getCurrentPrice(FSYM, tsym)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onPriceLoaded,
                        throwable -> {
                            throwable.printStackTrace();
                            callback.onError(throwable);
                        });
    }

}
