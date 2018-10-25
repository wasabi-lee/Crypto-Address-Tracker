package io.incepted.cryptoaddresstracker.repository;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import io.incepted.cryptoaddresstracker.data.source.txlist.TxListContractDataSource;
import io.incepted.cryptoaddresstracker.data.source.txlist.TxListEthDataSource;
import io.incepted.cryptoaddresstracker.data.source.txlist.TxListTokenDataSource;
import io.incepted.cryptoaddresstracker.data.source.callbacks.TxListCallbacks;

public class TxListRepository {

    private volatile static TxListRepository INSTANCE = null;

    private final TxListEthDataSource mEthDataSource;
    private final TxListTokenDataSource mTokenDataSource;
    private final TxListContractDataSource mContractDataSource;

    public TxListRepository(TxListEthDataSource ethDataSource,
                            TxListTokenDataSource tokenDataSource,
                            TxListContractDataSource contractDataSource) {
        this.mEthDataSource = ethDataSource;
        this.mTokenDataSource = tokenDataSource;
        this.mContractDataSource = contractDataSource;
    }

    public static TxListRepository getInstance(TxListEthDataSource ethDataSource,
                                               TxListTokenDataSource tokenDataSource,
                                               TxListContractDataSource contractDataSource) {
        if (INSTANCE == null) {
            synchronized (TxListRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TxListRepository(ethDataSource, tokenDataSource, contractDataSource);
                }
            }
        }
        return INSTANCE;
    }


    @SuppressLint("CheckResult")
    public void fetchEthTransactionListInfo(@NonNull String address,
                                            @NonNull TxListCallbacks.EthTransactionListInfoListener callback) {
        mEthDataSource.fetchEthTransactionListInfo(address, callback);
    }

    @SuppressLint("CheckResult")
    public void fetchContractTokenTransactionListInfo(@NonNull String address,
                                                      @NonNull TxListCallbacks.TransactionListInfoListener callback) {
        mContractDataSource.fetchContractTokenTransactionListInfo(address, callback);
    }

    @SuppressLint("CheckResult")
    public void fetchTokenTransactionListInfo(@NonNull String address, @NonNull String tokenAddress,
                                              @NonNull TxListCallbacks.TransactionListInfoListener callback) {
        mTokenDataSource.fetchTokenTransactionListInfo(address, tokenAddress, callback);
    }

}
