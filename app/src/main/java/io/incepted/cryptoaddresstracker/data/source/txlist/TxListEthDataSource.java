package io.incepted.cryptoaddresstracker.data.source.txlist;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import io.incepted.cryptoaddresstracker.data.source.callbacks.TxListCallbacks;
import io.incepted.cryptoaddresstracker.network.ApiManager;
import io.incepted.cryptoaddresstracker.network.NetworkManager;
import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TxListEthDataSource {

    private volatile static TxListEthDataSource INSTANCE = null;

    private NetworkService mDefaultAddressInfoService =
            NetworkManager.getDefaultNetworkService(NetworkManager.BASE_URL_ETHPLORER);

    private TxListEthDataSource() {
    }

    public static TxListEthDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (TxListEthDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TxListEthDataSource();
                }
            }
        }
        return INSTANCE;
    }


    @SuppressLint("CheckResult")
    public void fetchEthTransactionListInfo(@NonNull String address,
                                            @NonNull TxListCallbacks.EthTransactionListInfoListener callback) {
        callback.onCallReady();
        mDefaultAddressInfoService
                .getEthTransactionListInfo(address, ApiManager.API_KEY_ETHPLORER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onEthTransactionListInfoReady,
                        callback::onDataNotAvailable);
    }

}
