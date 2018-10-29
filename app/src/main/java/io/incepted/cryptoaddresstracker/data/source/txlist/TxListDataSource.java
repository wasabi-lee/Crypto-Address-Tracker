package io.incepted.cryptoaddresstracker.data.source.txlist;

import android.annotation.SuppressLint;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ItemKeyedDataSource;
import io.incepted.cryptoaddresstracker.network.ApiManager;
import io.incepted.cryptoaddresstracker.network.NetworkManager;
import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItemDeserializer;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.SimpleTxItemResult;
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

    public TxListDataSource() {
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<SimpleTxItem> callback) {
        //TODO handle error
        Timber.d("Load initial");
        mNetworkService
                .getEthTransactionListInfo("0x7a4ef3fBBB7a4DB8FfDc62B34242Bfed45cb61f5",
                        ApiManager.API_KEY_ETHPLORER,
                        new Date().getTime() / 1000,
                        PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isLoading.postValue(true))
                .subscribe(simpleTxItemResult -> {
                            Timber.d("list size: " + simpleTxItemResult.getItems().size());
                            callback.onResult(simpleTxItemResult.getItems());
                            isLoading.setValue(false);
                            if (simpleTxItemResult.getError() != null)
                                errorMessage.postValue(simpleTxItemResult.getError().getMessage());
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
        Timber.d("Load after: %s", params.key);
        mNetworkService
                .getEthTransactionListInfo("0x7a4ef3fBBB7a4DB8FfDc62B34242Bfed45cb61f5",
                        ApiManager.API_KEY_ETHPLORER,
                        params.key,
                        PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isLoading.postValue(true))
                .subscribe(simpleTxItemResult -> {
                    Timber.d("list size: " + simpleTxItemResult.getItems().size());
                            callback.onResult(simpleTxItemResult.getItems());
                            isLoading.setValue(false);
                            if (simpleTxItemResult.getError() != null)
                                errorMessage.postValue(simpleTxItemResult.getError().getMessage());
                        },
                        throwable -> {
                            Timber.e(throwable);
                            errorMessage.postValue(throwable.getMessage());
                            isLoading.setValue(false);
                        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<SimpleTxItem> callback) {
        Timber.d("Load before");
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
}
