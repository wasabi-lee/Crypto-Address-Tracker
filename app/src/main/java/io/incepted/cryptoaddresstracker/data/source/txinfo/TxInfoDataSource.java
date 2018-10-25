package io.incepted.cryptoaddresstracker.data.source.txinfo;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import io.incepted.cryptoaddresstracker.data.source.callbacks.TxInfoCallbacks;
import io.incepted.cryptoaddresstracker.network.ApiManager;
import io.incepted.cryptoaddresstracker.network.NetworkManager;
import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TxInfoDataSource {

    private volatile static TxInfoDataSource INSTANCE = null;

    private NetworkService mDefaultAddressInfoService =
            NetworkManager.getDefaultNetworkService(NetworkManager.BASE_URL_ETHPLORER);

    private TxInfoDataSource() {
    }

    public static TxInfoDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (TxInfoDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TxInfoDataSource();
                }
            }
        }
        return INSTANCE;
    }


    @SuppressLint("CheckResult")
    public void fetchTransactionDetail(@NonNull String txHash,
                                       @NonNull TxInfoCallbacks.TransactionInfoListener callback) {
        callback.onCallReady();
        mDefaultAddressInfoService
                .getTransactionDetail(txHash, ApiManager.API_KEY_ETHPLORER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onTransactionDetailReady,
                        callback::onDataNotAvailable);
    }
}
