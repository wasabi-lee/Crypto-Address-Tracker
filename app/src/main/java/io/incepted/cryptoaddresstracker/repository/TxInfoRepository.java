package io.incepted.cryptoaddresstracker.repository;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import io.incepted.cryptoaddresstracker.data.source.callbacks.TxInfoCallbacks;
import io.incepted.cryptoaddresstracker.data.source.txinfo.TxInfoDataSource;
import io.incepted.cryptoaddresstracker.network.ApiManager;
import io.reactivex.Scheduler;

public class TxInfoRepository {
    private volatile static TxInfoRepository INSTANCE = null;

    private final TxInfoDataSource mTxInfoDataSource;
    public TxInfoRepository(TxInfoDataSource txInfoDataSource) {
        this.mTxInfoDataSource = txInfoDataSource;
    }

    public static TxInfoRepository getInstance(TxInfoDataSource txInfoDataSource) {
        if (INSTANCE == null) {
            synchronized (TxInfoRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TxInfoRepository(txInfoDataSource);
                }
            }
        }
        return INSTANCE;
    }

    public void fetchTransactionDetail(@NonNull String txHash,
                                       @NonNull TxInfoCallbacks.TransactionInfoListener callback) {
        mTxInfoDataSource.fetchTransactionDetail(txHash, callback);
    }

}
