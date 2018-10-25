package io.incepted.cryptoaddresstracker.utils;

import io.incepted.cryptoaddresstracker.data.source.TxListContractDataSource;
import io.incepted.cryptoaddresstracker.data.source.TxListEthDataSource;
import io.incepted.cryptoaddresstracker.data.source.TxListTokenDataSource;
import io.incepted.cryptoaddresstracker.repository.TxListRepository;

public class TxListRepositoryInjection {

    public static TxListRepository provideTxListRepository() {
        return TxListRepository.getInstance(provideTxListEthDataSource(),
                provideTxListTokenDataSource(),
                provideTxListContractDataSource());
    }

    private static TxListEthDataSource provideTxListEthDataSource() {
        return TxListEthDataSource.getInstance();
    }

    private static TxListTokenDataSource provideTxListTokenDataSource() {
        return TxListTokenDataSource.getInstance();
    }

    private static TxListContractDataSource provideTxListContractDataSource() {
        return TxListContractDataSource.getInstance();
    }
}
