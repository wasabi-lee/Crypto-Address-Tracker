package io.incepted.cryptoaddresstracker.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.Network.NetworkManager;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.Operation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.TransactionListInfo;
import io.incepted.cryptoaddresstracker.Network.NetworkService;
import io.incepted.cryptoaddresstracker.R;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TxViewModel extends AndroidViewModel implements AddressDataSource.OnAddressLoadedListener {

    private static final String TAG = TxViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;
    public MutableLiveData<Address> mAddress = new MutableLiveData<>();
    public String tokenName;
    public String tokenAddress;

    public ObservableArrayList<Operation> mTxOperations = new ObservableArrayList<>();
    public ObservableBoolean isLoading = new ObservableBoolean(false);

    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();

    public TxViewModel(@NonNull Application application, AddressRepository repository) {
        super(application);
        this.mAddressRepository = repository;
    }

    public void start(int addressId, String tokenName, String tokenAddress) {
        this.tokenName = tokenName;
        this.tokenAddress = tokenAddress;
        loadAddress(addressId);
    }

    private void loadAddress(int addressId) {
        isLoading.set(true);
        mAddressRepository.getAddress(addressId, this);
    }

    @SuppressLint("CheckResult")
    private void loadTransactions(String address, String tokenAddress) {
        Single<TransactionListInfo> networkCallSingle =
                NetworkManager.getTransactionListInfoService()
                        .getTransactionListInfo(address, NetworkManager.API_KEY, tokenAddress);

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

    private void refreshList(List<Operation> operations) {
        mTxOperations.clear();
        mTxOperations.addAll(operations);
    }

    public void toTxDetailActivity(String transactionHash) {
        //TODO TxDetailActivity connection
        Log.d(TAG, "Clicked TxHash: " + transactionHash);
    }


    public MutableLiveData<Address> getmAddress() {
        return mAddress;
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
