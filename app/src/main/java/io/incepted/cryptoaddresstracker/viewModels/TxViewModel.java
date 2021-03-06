package io.incepted.cryptoaddresstracker.viewModels;

import android.annotation.SuppressLint;
import android.app.Application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressLocalCallbacks;
import io.incepted.cryptoaddresstracker.network.ConnectivityChecker;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.SimpleTxItemResult;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.repository.TxListRepository;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;
import timber.log.Timber;

public class TxViewModel extends AndroidViewModel implements AddressLocalCallbacks.OnAddressLoadedListener {

    private AddressRepository mAddressRepository;
    private TxListRepository mTxListRepository;

    private LiveData<SimpleTxItemResult> result;
    private LiveData<PagedList<SimpleTxItem>> ethTxList;
    private LiveData<String> networkError;
    private LiveData<Boolean> isLoading;
    private LiveData<Boolean> itemExists;

    public MutableLiveData<String> mAddrValue = new MutableLiveData<>();
    public ObservableField<Address> mAddress = new ObservableField<>();
    public ObservableField<String> tokenName = new ObservableField<>("-");
    public ObservableField<String> mTokenAddress = new ObservableField<>("-");
    public ObservableField<String> lastUpdated = new ObservableField<>();

    public boolean fetchEthTx;
    //    private boolean isContractAddress = false;

    public Calendar calendar;

    public ObservableArrayList<SimpleTxItem> mTxOperations = new ObservableArrayList<>();

    private SingleLiveEvent<String> mOpenTxDetail = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();

    public TxViewModel(@NonNull Application application,
                       @NonNull AddressRepository addressRepository,
                       @NonNull TxListRepository txListRepository) {
        super(application);
        mAddressRepository = addressRepository;
        mTxListRepository = txListRepository;

        result = Transformations.map(mAddrValue, input -> loadTransactions(input, mTokenAddress.get()));
        ethTxList = Transformations.switchMap(result, SimpleTxItemResult::getItemLiveDataList);
        networkError = Transformations.switchMap(result, SimpleTxItemResult::getError);
        isLoading = Transformations.switchMap(result, SimpleTxItemResult::getIsLoading);
        itemExists = Transformations.switchMap(result, SimpleTxItemResult::getItemExists);
    }

    public void start(int addressId, String tokenName, String tokenAddress) {
        this.tokenName.set(tokenName);
        this.mTokenAddress.set(tokenAddress);
        this.fetchEthTx = tokenAddress.equals("base_currency_ethereum");
        this.lastUpdated.set(getCurrentTimeInString());
        loadAddress(addressId);

    }

    private String getCurrentTimeInString() {
        calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        DateFormat sdf = SimpleDateFormat.getInstance();
        return sdf.format(time);
    }

    public void loadAddress(int addressId) {
        mAddressRepository.getAddress(addressId, this);
    }

    @SuppressLint("CheckResult")
    private SimpleTxItemResult loadTransactions(String address, String tokenAddress) {
        TxListRepository.Type type;
        if (fetchEthTx) {
            type = TxListRepository.Type.ETH_TXS;
        } else {
            type = TxListRepository.Type.TOKEN_TXS_SPECIFIC;
        }
        return mTxListRepository.getTxs(type, address, tokenAddress);
    }


    public void toTxDetailActivity(String transactionHash) {
        mOpenTxDetail.setValue(transactionHash);
    }


    public LiveData<PagedList<SimpleTxItem>> getEthTxList() {
        return ethTxList;
    }

    public ObservableField<Address> getmAddress() {
        return mAddress;
    }

    public MutableLiveData<String> getOpenTxDetail() {
        return mOpenTxDetail;
    }

    public MutableLiveData<String> getSnackbarText() {
        return mSnackbarText;
    }

    public MutableLiveData<Integer> getSnackbarTextResource() {
        return mSnackbarTextResource;
    }

    public LiveData<String> getNetworkError() {
        return networkError;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getItemExists() {
        return itemExists;
    }

    public void setFetchEthTx(boolean fetchEthTx) {
        this.fetchEthTx = fetchEthTx;
    }


    @Override
    public void onAddressLoaded(Address address) {
        this.mAddrValue.setValue(address.getAddrValue());
        this.mAddress.set(address);
        this.mAddress.notifyChange();
        if (ConnectivityChecker.isConnected(getApplication())) {
            loadTransactions(address.getAddrValue(), mTokenAddress.get());
        } else {
            mSnackbarTextResource.setValue(R.string.error_offline);
        }
    }

    @Override
    public void onAddressNotAvailable() {
        handleError();
    }


    private void handleError() {
        mSnackbarTextResource.setValue(R.string.address_loading_error);
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        mSnackbarTextResource.setValue(R.string.unexpected_error);
    }
}
