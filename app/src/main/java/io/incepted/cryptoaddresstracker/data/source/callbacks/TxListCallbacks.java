package io.incepted.cryptoaddresstracker.data.source.callbacks;

import java.util.List;

import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.TransactionListInfo;

public interface TxListCallbacks {
    interface TransactionListInfoListener {
        void onCallReady();

        void onTransactionListInfoLoaded(TransactionListInfo transactionListInfo);

        void onDataNotAvailable(Throwable throwable);
    }

    interface EthTransactionListInfoListener {
        void onCallReady();

        void onEthTransactionListInfoReady(List<EthOperation> ethOperationList);

        void onDataNotAvailable(Throwable throwable);
    }




}
