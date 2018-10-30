package io.incepted.cryptoaddresstracker.data.source.txlist;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import io.incepted.cryptoaddresstracker.data.source.callbacks.TxListCallbacks;
import io.incepted.cryptoaddresstracker.network.ApiManager;
import io.incepted.cryptoaddresstracker.network.NetworkManager;
import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TxListContractDataSource {
    private volatile static TxListContractDataSource INSTANCE = null;

    private NetworkService mDefaultAddressInfoService =
            NetworkManager.getDefaultNetworkService(NetworkManager.BASE_URL_ETHPLORER);

    private TxListContractDataSource() {
    }

    public static TxListContractDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (TxListContractDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TxListContractDataSource();
                }
            }
        }
        return INSTANCE;
    }

    @SuppressLint("CheckResult")
    public void fetchContractTokenTransactionListInfo(@NonNull String address,
                                                      @NonNull TxListCallbacks.TransactionListInfoListener callback) {

    }

}
