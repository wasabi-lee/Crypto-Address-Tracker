package io.incepted.cryptoaddresstracker.data.source.txlist;

import android.annotation.SuppressLint;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ItemKeyedDataSource;
import io.incepted.cryptoaddresstracker.network.ApiManager;
import io.incepted.cryptoaddresstracker.network.NetworkManager;
import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItemDeserializer;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.SimpleTxItemResult;
import io.incepted.cryptoaddresstracker.repository.TxListRepository;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TxListDataSource extends ItemKeyedDataSource<Long, SimpleTxItem> {

    public static final int PAGE_SIZE = 50;

    private NetworkService mNetworkService =
            NetworkManager.getCustomNetworkService(NetworkManager.BASE_URL_ETHPLORER,
                    SimpleTxItemResult.class, new SimpleTxItemDeserializer());

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> itemExists = new MutableLiveData<>();

    private TxListRepository.Type type;
    private String address;
    private String tokenAddress;

    public TxListDataSource() {
    }

    public TxListDataSource(TxListRepository.Type type, String address, String tokenAddress) {
        this.type = type;
        this.address = address;
        this.tokenAddress = tokenAddress;
    }


    @SuppressLint("CheckResult")
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<SimpleTxItem> callback) {
        getNetworkServiceSingle(type, address, tokenAddress, getCurrentTimestamp())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isLoading.postValue(true))
                .subscribe(simpleTxItemResult -> {
                            setItemAvailability(simpleTxItemResult.getItems());
                            callback.onResult(simpleTxItemResult.getItems());
                            isLoading.setValue(false);
                        },
                        throwable -> {
                            Timber.e(throwable);
                            errorMessage.postValue(throwable.getMessage());
                            isLoading.setValue(false);
                        });
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<SimpleTxItem> callback) {
        getNetworkServiceSingle(type, address, tokenAddress, params.key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isLoading.postValue(true))
                .subscribe(simpleTxItemResult -> {
                            setItemAvailability(simpleTxItemResult.getItems());
                            callback.onResult(simpleTxItemResult.getItems());
                            isLoading.setValue(false);
                        },
                        throwable -> {
                            Timber.e(throwable);
                            errorMessage.postValue(throwable.getMessage());
                            isLoading.setValue(false);
                        });
    }


    private static long getCurrentTimestamp() {
        return new Date().getTime() / 1000;
    }


    private void setItemAvailability(List<SimpleTxItem> items) {
        // don't explicitly set the value if the Livedata value is already true
        if (itemExists.getValue() == null || !itemExists.getValue()) {
            itemExists.setValue(items.size() != 0);
        }
    }

    private Single<SimpleTxItemResult> getNetworkServiceSingle(TxListRepository.Type type,
                                                               String address,
                                                               String tokenAddress,
                                                               long lastTimestamp) {
        switch (type) {
            case ETH_TXS:
                return mNetworkService.getEthTransactionListInfo(address,
                        ApiManager.API_KEY_ETHPLORER,
                        lastTimestamp,
                        PAGE_SIZE);

            case TOKEN_TXS:
                return mNetworkService.getTokenTransactionListInfo(address,
                        ApiManager.API_KEY_ETHPLORER,
                        lastTimestamp,
                        PAGE_SIZE);

            case TOKEN_TXS_SPECIFIC:
                return mNetworkService.getSpecificTokenTransactionListInfo(address,
                        tokenAddress,
                        ApiManager.API_KEY_ETHPLORER,
                        lastTimestamp,
                        PAGE_SIZE);

            case CONTRACT_TXS:
                return mNetworkService.getContractTokenTransactionListInfo(address,
                        ApiManager.API_KEY_ETHPLORER,
                        lastTimestamp,
                        PAGE_SIZE);
        }
        return null;
    }


    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<SimpleTxItem> callback) {
        Timber.d("Type: %s, Load before", type == TxListRepository.Type.ETH_TXS ? "eth" : "token");
    }

    @NonNull
    @Override
    public Long getKey(@NonNull SimpleTxItem item) {
        return item.getTimestamp() - 1;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> getItemExists() {
        return itemExists;
    }
}
