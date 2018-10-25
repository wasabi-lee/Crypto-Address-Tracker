package io.incepted.cryptoaddresstracker.di;

import io.incepted.cryptoaddresstracker.data.source.price.PriceDataSource;
import io.incepted.cryptoaddresstracker.repository.PriceRepository;

public class PriceRepositoryInjection {

    public static PriceRepository providePriceRepository() {
        return PriceRepository.getInstance(providePriceDataSource());
    }

    private static PriceDataSource providePriceDataSource() {
        return PriceDataSource.getInstance();
    }

}
