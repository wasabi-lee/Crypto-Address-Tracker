package io.incepted.cryptoaddresstracker.data.source.txlist;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import io.incepted.cryptoaddresstracker.data.source.callbacks.TxListCallbacks;
import io.incepted.cryptoaddresstracker.network.ApiManager;
import io.incepted.cryptoaddresstracker.network.NetworkManager;
import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TxListTokenDataSource {
    private volatile static TxListTokenDataSource INSTANCE = null;

    private NetworkService mDefaultAddressInfoService =
            NetworkManager.getDefaultNetworkService(NetworkManager.BASE_URL_ETHPLORER);

    private TxListTokenDataSource() {
    }

    public static TxListTokenDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (TxListTokenDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TxListTokenDataSource();
                }
            }
        }
        return INSTANCE;
    }

    @SuppressLint("CheckResult")
    public void fetchTokenTransactionListInfo(@NonNull String address, @NonNull String tokenAddress,
                                              @NonNull TxListCallbacks.TransactionListInfoListener callback) {
        callback.onCallReady();
        mDefaultAddressInfoService
                .getTokenTransactionListInfo(address, ApiManager.API_KEY_ETHPLORER, tokenAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onTransactionListInfoLoaded,
                        callback::onDataNotAvailable);
    }

}
