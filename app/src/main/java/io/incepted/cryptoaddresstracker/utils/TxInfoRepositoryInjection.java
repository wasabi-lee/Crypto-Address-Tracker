package io.incepted.cryptoaddresstracker.utils;

import io.incepted.cryptoaddresstracker.data.source.txinfo.TxInfoDataSource;
import io.incepted.cryptoaddresstracker.repository.TxInfoRepository;

public class TxInfoRepositoryInjection {
    public static TxInfoRepository provideTxListRepository() {
        return TxInfoRepository.getInstance(provideTxInfoDataSource());
    }

    private static TxInfoDataSource provideTxInfoDataSource() {
        return TxInfoDataSource.getInstance();
    }
}
