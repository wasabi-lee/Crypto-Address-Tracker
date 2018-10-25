package io.incepted.cryptoaddresstracker.data.source.callbacks;

import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.TransactionInfo;

public interface TxInfoCallbacks {
    interface TransactionInfoListener {
        void onCallReady();

        void onTransactionDetailReady(TransactionInfo transactionDetail);

        void onDataNotAvailable(Throwable throwable);
    }
}
