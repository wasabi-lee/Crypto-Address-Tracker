package io.incepted.cryptoaddresstracker.di;

import io.incepted.cryptoaddresstracker.repository.TxListRepository;

public class TxListRepositoryInjection {

    public static TxListRepository provideTxListRepository() {
        return TxListRepository.getInstance();
    }

}
