package io.incepted.cryptoaddresstracker.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.R;

public class TxViewModel extends AndroidViewModel implements AddressDataSource.OnAddressLoadedListener {

    private static final String TAG = TxViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;
    public Address mAddress;
    public String tokenName;

    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();

    public TxViewModel(@NonNull Application application, AddressRepository repository) {
        super(application);
        this.mAddressRepository = repository;
    }

    public void start(int addressId, String tokenName, String tokenAddress) {
        this.tokenName = tokenName;
        loadAddress(addressId);
        loadTransactions(addressId, tokenAddress);
    }

    private void loadAddress(int addressId) {
        mAddressRepository.getAddress(addressId, this);
    }

    private void loadTransactions(int addressId, String tokenAddress) {

    }

    @Override
    public void onAddressLoaded(Address address) {
        this.mAddress = address;
        Log.d(TAG, "onAddressLoaded: debug");
    }

    @Override
    public void onAddressNotAvailable() {
        handleError(R.string.address_loading_error);
    }


    private void handleError(int errorMessage) {
        mSnackbarTextResource.setValue(errorMessage);
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        mSnackbarTextResource.setValue(R.string.unexpected_error);
    }
}
