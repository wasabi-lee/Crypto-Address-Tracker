package io.incepted.cryptoaddresstracker.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.SimpleTxItemResult;
import io.incepted.cryptoaddresstracker.repository.TxListRepository;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;

public class TxListTokenViewModel extends AndroidViewModel {

    private TxListRepository mTxListRepository;

    private LiveData<SimpleTxItemResult> mTokenTxResult;
    private LiveData<PagedList<SimpleTxItem>> mTokenTxList;
    private LiveData<String> mTokenNetworkError;
    private LiveData<Boolean> mTokenTxExists;
    private LiveData<Boolean> mIsTokenTxLoading;

    private MutableLiveData<String> mAddrValue = new MutableLiveData<>();

    private SingleLiveEvent<Integer> mSnackbarTextRes = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();


    public TxListTokenViewModel(@NonNull Application application,
                                @NonNull TxListRepository txListRepository) {
        super(application);
        mTxListRepository = txListRepository;


        mTokenTxResult = Transformations.map(mAddrValue, this::getTokenTxs);

        mTokenTxList = Transformations.switchMap(mTokenTxResult, SimpleTxItemResult::getItemLiveDataList);
        mTokenNetworkError = Transformations.switchMap(mTokenTxResult, SimpleTxItemResult::getError);
        mTokenTxExists = Transformations.switchMap(mTokenTxResult, SimpleTxItemResult::getItemExists);
        mIsTokenTxLoading = Transformations.switchMap(mTokenTxResult, SimpleTxItemResult::getIsLoading);
    }


    private SimpleTxItemResult loadTransactions(TxListRepository.Type type, String address) {
        return mTxListRepository.getTxs(type, address, address);
    }


    private SimpleTxItemResult getTokenTxs(String address) {
        return loadTransactions(TxListRepository.Type.TOKEN_TXS, address);
    }


    public MutableLiveData<String> getAddressValue() {
        return mAddrValue;
    }

    public String getAddressValueStr() {
        return mAddrValue.getValue();
    }

    public LiveData<Boolean> getIsTokenTxLoading() {
        return mIsTokenTxLoading;
    }

    public LiveData<PagedList<SimpleTxItem>> getTokenTxList() {
        return mTokenTxList;
    }

    public LiveData<String> getTokenNetworkError() {
        return mTokenNetworkError;
    }

    public LiveData<Boolean> getTokenTxExists() {
        return mTokenTxExists;
    }

    public SingleLiveEvent<Integer> getSnackbarTextRes() {
        return mSnackbarTextRes;
    }

    public SingleLiveEvent<String> getSnackbarText() {
        return mSnackbarText;
    }
}
