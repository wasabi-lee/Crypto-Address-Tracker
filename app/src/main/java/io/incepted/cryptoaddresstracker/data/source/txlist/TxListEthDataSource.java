package io.incepted.cryptoaddresstracker.data.source.txlist;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.util.Log;

import java.time.Instant;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;
import io.incepted.cryptoaddresstracker.network.ApiManager;
import io.incepted.cryptoaddresstracker.network.NetworkManager;
import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.EthOperation;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TxListEthDataSource extends ItemKeyedDataSource<Long, EthOperation> {


    private NetworkService mDefaultAddressInfoService =
            NetworkManager.getDefaultNetworkService(NetworkManager.BASE_URL_ETHPLORER);

    public TxListEthDataSource() {
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<EthOperation> callback) {
        //TODO handle error
        Timber.d("Load initial");
        mDefaultAddressInfoService
                .getEthTransactionListInfo("0x5fd603027e69E628f972bA5BC3348e53ce9B45f7", ApiManager.API_KEY_ETHPLORER,
                        new Date().getTime()/ 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        callback::onResult,
                        Throwable::printStackTrace
                );
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<EthOperation> callback) {
        Timber.d("Load after: " + params.key);
        mDefaultAddressInfoService
                .getEthTransactionListInfo("0x5fd603027e69E628f972bA5BC3348e53ce9B45f7", ApiManager.API_KEY_ETHPLORER,
                        params.key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        callback::onResult,
                        Throwable::printStackTrace
                );
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<EthOperation> callback) {
        Timber.d("Load before");
    }

    @NonNull
    @Override
    public Long getKey(@NonNull EthOperation item) {
        return item.getTimestamp();
    }

//    @SuppressLint("CheckResult")
//    public void fetchEthTransactionListInfo(@NonNull String address,
//                                            @NonNull TxListCallbacks.EthTransactionListInfoListener callback) {
//        callback.onCallReady();
//        mDefaultAddressInfoService
//                .getEthTransactionListInfo(address, ApiManager.API_KEY_ETHPLORER)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(callback::onEthTransactionListInfoReady,
//                        callback::onDataNotAvailable);
//    }

}
