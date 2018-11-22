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

public class TxListEthViewModel extends AndroidViewModel {

    private TxListRepository mTxListRepository;
    private MutableLiveData<Boolean> mIsTokenAddress = new MutableLiveData<>();
    private MutableLiveData<String> mAddressValue = new MutableLiveData<>();

    private LiveData<SimpleTxItemResult> mEthResult;
    private LiveData<PagedList<SimpleTxItem>> mEthTxList;
    private LiveData<String> mNetworkError;
    private LiveData<Boolean> mEthTxExists;

    private SingleLiveEvent<Integer> mSnackbarTextRes = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();

    public TxListEthViewModel(@NonNull Application application,
                              @NonNull TxListRepository txListRepository) {
        super(application);
        mTxListRepository = txListRepository;


        mEthResult = Transformations.map(mIsTokenAddress, this::getEthTxs);

        mEthTxList = Transformations.switchMap(mEthResult, SimpleTxItemResult::getItemLiveDataList);

        mNetworkError = Transformations.switchMap(mEthResult, SimpleTxItemResult::getError);

        mEthTxExists = Transformations.switchMap(mEthResult, SimpleTxItemResult::getItemExists);

    }


    private SimpleTxItemResult getEthTxs(Boolean isTokenAddress) {
        // For ETH transactions, boolean isTokenAddress works as a trigger of the Transformations.map(),
        // which means we trigger the loadTransaction() method ONLY when we are sure
        // that the address we're working on is not a token address,
        // since the ERC-20 token addresses cannot have external ETH transactions
        if (!isTokenAddress)
            return loadTransactions(TxListRepository.Type.ETH_TXS, mAddressValue.getValue());
        else return SimpleTxItemResult.getEmptyInstance();
    }


    private SimpleTxItemResult loadTransactions(TxListRepository.Type type, String address) {
        return mTxListRepository.getTxs(type, address, address);
    }


    public MutableLiveData<String> getAddressValue() {
        return mAddressValue;
    }

    public String getAddressValueStr() {
        return mAddressValue.getValue();
    }

    public MutableLiveData<Boolean> getIsTokenAddress() {
        return mIsTokenAddress;
    }

    public LiveData<PagedList<SimpleTxItem>> getEthTxList() {
        return mEthTxList;
    }

    public LiveData<String> getNetworkError() {
        return mNetworkError;
    }

    public LiveData<Boolean> getEthTxExists() {
        return mEthTxExists;
    }

    public SingleLiveEvent<Integer> getSnackbarTextRes() {
        return mSnackbarTextRes;
    }
}
