package io.incepted.cryptoaddresstracker.viewModels;

import android.annotation.SuppressLint;
import android.app.Application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressLocalCallbacks;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressRemoteCallbacks;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.data.source.callbacks.TxListCallbacks;
import io.incepted.cryptoaddresstracker.network.ConnectivityChecker;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.OperationWrapper;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.TransactionListInfo;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TxViewModel extends AndroidViewModel implements AddressLocalCallbacks.OnAddressLoadedListener {

    private static final String TAG = TxViewModel.class.getSimpleName();

    private AddressRemoteRepository mRemoteRepository;
    private AddressRepository mAddressRepository;


    public ObservableField<Address> mAddress = new ObservableField<>();
    public ObservableField<String> tokenName = new ObservableField<>("-");
    public ObservableField<String> tokenAddress = new ObservableField<>("-");
    private boolean isContractAddress =false;
    public ObservableField<String> lastUpdated = new ObservableField<>();

    /**
     * @value boolean fetchEthTx:
     * Making different API call for 'Ethereum transaction' and 'Token transaction.'
     * When we need to whow Ethereum transactions, call 'getAddressTransactions' request.
     * For token transactions, call 'getAddressHistory'.
     */
    public boolean fetchEthTx;
    public Calendar calendar;

    public ObservableArrayList<OperationWrapper> mTxOperations = new ObservableArrayList<>();
    public ObservableBoolean isLoading = new ObservableBoolean(false);

    private SingleLiveEvent<String> mOpenTxDetail = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();

    public TxViewModel(@NonNull Application application,
                       @NonNull AddressRemoteRepository remoteRepository,
                       @NonNull AddressRepository addressRepository) {
        super(application);
        mRemoteRepository = remoteRepository;
        mAddressRepository = addressRepository;

    }

    public void start(int addressId, String tokenName, String tokenAddress, boolean isContractAddress) {
        this.tokenName.set(tokenName);
        this.tokenAddress.set(tokenAddress);
        this.isContractAddress = isContractAddress;
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
        isLoading.set(true);
        mAddressRepository.getAddress(addressId, this);
    }

    @SuppressLint("CheckResult")
    private void loadTransactions(String address, String tokenAddress) {
        if (fetchEthTx) {

            // When fetching Ethereum transactions

            mRemoteRepository.fetchEthTransactionListInfo(address, Schedulers.io(), AndroidSchedulers.mainThread(),
                    new TxListCallbacks.EthTransactionListInfoListener() {
                        @Override
                        public void onCallReady() {
                            /* empty */
                        }

                        @Override
                        public void onEthTransactionListInfoReady(List<EthOperation> ethOperationList) {
                            isLoading.set(false);
                            refreshList(ethOperationList);
                        }

                        @Override
                        public void onDataNotAvailable(Throwable throwable) {
                            isLoading.set(false);
                            handleError(throwable);
                        }
                    });

        } else {

            TxListCallbacks.TransactionListInfoListener callback =
                    new TxListCallbacks.TransactionListInfoListener() {
                @Override
                public void onCallReady() {
                    /* empty */
                }

                @Override
                public void onTransactionListInfoLoaded(TransactionListInfo transactionListInfo) {
                    isLoading.set(false);
                    refreshList(transactionListInfo.getTokenTxList());
                }

                @Override
                public void onDataNotAvailable(Throwable throwable) {
                    isLoading.set(false);
                    handleError(throwable);
                }
            };

                // Using 'getAddressHistory' API call for the normal address
                mRemoteRepository.fetchTokenTransactionListInfo(address, tokenAddress, Schedulers.io(),
                        AndroidSchedulers.mainThread(), callback);

        }
    }

    private void refreshList(List<? extends OperationWrapper> operations) {
        mTxOperations.clear();
        mTxOperations.addAll(operations);
    }

    public void toTxDetailActivity(String transactionHash) {
        mOpenTxDetail.setValue(transactionHash);
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

    public void setFetchEthTx(boolean fetchEthTx) {
        this.fetchEthTx = fetchEthTx;
    }

    public void setContractAddress(boolean contractAddress) {
        isContractAddress = contractAddress;
    }

    @Override
    public void onAddressLoaded(Address address) {
        this.mAddress.set(address);
        if (ConnectivityChecker.isConnected(getApplication())) {
            loadTransactions(address.getAddrValue(), tokenAddress.get());
        } else {
            mSnackbarTextResource.setValue(R.string.error_offline);
        }
    }

    @Override
    public void onAddressNotAvailable() {
        handleError();
        isLoading.set(false);
    }


    private void handleError() {
        mSnackbarTextResource.setValue(R.string.address_loading_error);
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        mSnackbarTextResource.setValue(R.string.unexpected_error);
    }
}
