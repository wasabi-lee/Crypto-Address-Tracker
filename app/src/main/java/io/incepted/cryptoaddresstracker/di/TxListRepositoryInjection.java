package io.incepted.cryptoaddresstracker.di;

import io.incepted.cryptoaddresstracker.data.source.txlist.TxListContractDataSource;
import io.incepted.cryptoaddresstracker.data.source.txlist.TxListDataSource;
import io.incepted.cryptoaddresstracker.data.source.txlist.TxListTokenDataSource;
import io.incepted.cryptoaddresstracker.repository.TxListRepository;

public class TxListRepositoryInjection {

    public static TxListRepository provideTxListRepository() {
        return TxListRepository.getInstance(provideTxListEthDataSource(),
                provideTxListTokenDataSource(),
                provideTxListContractDataSource());
    }

    private static TxListDataSource provideTxListEthDataSource() {
        return new TxListDataSource();
    }

    private static TxListTokenDataSource provideTxListTokenDataSource() {
        return TxListTokenDataSource.getInstance();
    }

    private static TxListContractDataSource provideTxListContractDataSource() {
        return TxListContractDataSource.getInstance();
    }
}
