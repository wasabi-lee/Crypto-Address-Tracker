package io.incepted.cryptoaddresstracker.repository;

import io.incepted.cryptoaddresstracker.data.source.price.PriceDataSource;
import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;

public class PriceRepository {

    private volatile static PriceRepository INSTANCE = null;

    private final PriceDataSource mPriceDataSource;
    private PriceRepository(PriceDataSource PriceDataSource) {
        this.mPriceDataSource = PriceDataSource;
    }

    public static PriceRepository getInstance(PriceDataSource PriceDataSource) {
        if (INSTANCE == null) {
            synchronized (PriceRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PriceRepository(PriceDataSource);
                }
            }
        }
        return INSTANCE;
    }

    public interface OnPriceLoadedListener {
        void onPriceLoaded(CurrentPrice currentPrice);

        void onError(Throwable throwable);
    }

    public void loadCurrentPrice(String tsym, OnPriceLoadedListener callback) {
        mPriceDataSource.loadCurrentPrice(tsym, callback);
    }

}
