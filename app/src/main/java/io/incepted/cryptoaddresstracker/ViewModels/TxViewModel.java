package io.incepted.cryptoaddresstracker.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;

import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.Network.ConnectivityChecker;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.OperationWrapper;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.TransactionListInfo;
import io.incepted.cryptoaddresstracker.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TxViewModel extends AndroidViewModel implements AddressLocalDataSource.OnAddressLoadedListener {

    private static final String TAG = TxViewModel.class.getSimpleName();

    private AddressLocalRepository mLocalRepository;
    private AddressRemoteRepository mRemoteRepository;


    public MutableLiveData<Address> mAddress = new MutableLiveData<>();
    public String tokenName;
    public String tokenAddress;
    public boolean isContractAddress;

    /**
     * @value boolean fetchEthTx:
     * Making different API call for 'Ethereum transaction' and 'Token transaction.'
     * When we need to whow Ethereum transactions, call 'getAddressTransactions' request.
     * For token transactions, call 'getAddressHistory'.
     */
    public boolean fetchEthTx;

    public ObservableArrayList<OperationWrapper> mTxOperations = new ObservableArrayList<>();
    public ObservableBoolean isLoading = new ObservableBoolean(false);

    private MutableLiveData<String> mOpenTxDetail = new MutableLiveData<>();
    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();

    public TxViewModel(@NonNull Application application,
                       @NonNull AddressLocalRepository localRepository,
                       @NonNull AddressRemoteRepository remoteRepository) {
        super(application);
        this.mLocalRepository = localRepository;
        mRemoteRepository = remoteRepository;
    }

    public void start(int addressId, String tokenName, String tokenAddress, boolean isContractAddress) {
        this.tokenName = tokenName;
        this.tokenAddress = tokenAddress;
        this.isContractAddress = isContractAddress;
        this.fetchEthTx = tokenAddress.equals("base_currency_ethereum");
        loadAddress(addressId);
    }

    public void loadAddress(int addressId) {
        isLoading.set(true);
        mLocalRepository.getAddress(addressId, this);
    }

    @SuppressLint("CheckResult")
    private void loadTransactions(String address, String tokenAddress) {
        if (fetchEthTx) {

            // When fetching Ethereum transactions

            mRemoteRepository.fetchEthTransactionListInfo(address, Schedulers.io(), AndroidSchedulers.mainThread(),
                    new AddressRemoteDataSource.EthTransactionListInfoListener() {
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

            AddressRemoteDataSource.TransactionListInfoListener callback = new AddressRemoteDataSource.TransactionListInfoListener() {
                @Override
                public void onCallReady() {
                    /* empty */
                }

                @Override
                public void onTransactionListInfoLoaded(TransactionListInfo transactionListInfo) {
                    isLoading.set(false);
                    refreshList(transactionListInfo.getOperations());
                }

                @Override
                public void onDataNotAvailable(Throwable throwable) {
                    isLoading.set(false);
                    handleError(throwable);
                }
            };

            // Using 'getTokenHistory' API call for the contract address
            if (isContractAddress) {
                mRemoteRepository.fetchContractTokenTransactionListInfo(address, Schedulers.io(),
                        AndroidSchedulers.mainThread(), callback);
            } else {
                // Using 'getAddressHistory' API call for the normal address
                mRemoteRepository.fetchTokenTransactionListInfo(address, tokenAddress, Schedulers.io(),
                        AndroidSchedulers.mainThread(), callback);
            }

        }
    }

    private void refreshList(List<? extends OperationWrapper> operations) {
        mTxOperations.clear();
        mTxOperations.addAll(operations);
    }

    public void toTxDetailActivity(String transactionHash) {
        mOpenTxDetail.setValue(transactionHash);
    }


    public MutableLiveData<Address> getmAddress() {
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
        this.mAddress.setValue(address);
        if (ConnectivityChecker.isConnected(getApplication())) {
            loadTransactions(address.getAddrValue(), tokenAddress);
        } else {
            mSnackbarTextResource.setValue(R.string.error_offline);
        }
    }

    @Override
    public void onAddressNotAvailable() {
        handleError(R.string.address_loading_error);
        isLoading.set(false);
    }


    private void handleError(int errorMessage) {
        mSnackbarTextResource.setValue(errorMessage);
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        mSnackbarTextResource.setValue(R.string.unexpected_error);
        mSnackbarTextResource.setValue(null);
    }
}
