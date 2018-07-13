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
import io.incepted.cryptoaddresstracker.Data.Source.AddressDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.Network.NetworkManager;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.OperationWrapper;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.TransactionListInfo;
import io.incepted.cryptoaddresstracker.R;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TxViewModel extends AndroidViewModel implements AddressDataSource.OnAddressLoadedListener {

    private static final String TAG = TxViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;
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

    public TxViewModel(@NonNull Application application, AddressRepository repository) {
        super(application);
        this.mAddressRepository = repository;
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
        mAddressRepository.getAddress(addressId, this);
    }

    @SuppressLint("CheckResult")
    private void loadTransactions(String address, String tokenAddress) {
        if (fetchEthTx) {

            // When fetching Ethereum transactions

            Single<List<EthOperation>> networkCallSingle =
                    NetworkManager.getEthTransactionListInfoService()
                            .getEthTransactionListInfo(address, NetworkManager.API_KEY_ETHPLORER);

            networkCallSingle.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(operations -> {
                        isLoading.set(false);
                        refreshList(operations);
                    }, throwable -> {
                        isLoading.set(false);
                        handleError(throwable);
                    });

        } else {

            // When fetching token transactions
            Single<TransactionListInfo> networkCallSingle = isContractAddress ?
                    // Using 'getTokenHistory' API call for the contract address
                    NetworkManager.getContractTokenTransactionListInfoService()
                            .getContractTokenTransactionListInfo(address, NetworkManager.API_KEY_ETHPLORER)
                    :
                    // Using 'getAddressHistory' API call for the normal address
                    NetworkManager.getTokenTransactionListInfoService()
                            .getTokenTransactionListInfo(address, NetworkManager.API_KEY_ETHPLORER, tokenAddress);

            networkCallSingle.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(transactionListInfo -> {
                        // onSuccess
                        isLoading.set(false);
                        refreshList(transactionListInfo.getOperations());
                    }, throwable -> {
                        // onError
                        isLoading.set(false);
                        handleError(throwable);
                    });
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

    @Override
    public void onAddressLoaded(Address address) {
        this.mAddress.setValue(address);
        loadTransactions(address.getAddrValue(), tokenAddress);
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
    }
}
