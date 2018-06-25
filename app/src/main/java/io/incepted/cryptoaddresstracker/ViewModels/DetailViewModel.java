package io.incepted.cryptoaddresstracker.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.Navigators.DeletionStateNavigator;
import io.incepted.cryptoaddresstracker.R;

public class DetailViewModel extends AndroidViewModel implements AddressDataSource.OnAddressLoadedListener, AddressDataSource.OnAddressDeletedListener {

    private static final String TAG = DetailViewModel.class.getSimpleName();

    private final AddressRepository mAddressRepository;

    private int mAddressId;

    public Address mAddress;

    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();

    private MutableLiveData<DeletionStateNavigator> mDeletionState = new MutableLiveData<>();

    public DetailViewModel(@NonNull Application application, @NonNull AddressRepository repository) {
        super(application);
        this.mAddressRepository = repository;
    }

    public void start(int addressId) {
        this.mAddressId = addressId;
        loadAddress(addressId);
    }

    private void loadAddress(int addressId) {
        this.mAddressRepository.getAddress(addressId, this);
    }

    public void deleteAddress() {
        mDeletionState.setValue(DeletionStateNavigator.DELETION_IN_PROGRESS);
        mAddressRepository.deleteAddress(mAddressId, this);
    }


    // ----------------------------- Getters ---------------------------

    public MutableLiveData<String> getSnackbarText() {
        return mSnackbarText;
    }

    public MutableLiveData<Integer> getSnackbarTextResource() {
        return mSnackbarTextResource;
    }

    public MutableLiveData<DeletionStateNavigator> getDeletionState() {
        return mDeletionState;
    }

    // ----------------------------- Callbacks -------------------------

    @Override
    public void onAddressLoaded(Address address) {
        this.mAddress = address;
    }

    @Override
    public void onAddressNotAvailable() {
        Log.d(TAG, "onDataNotAvailable: Failed to load address with id: " + mAddressId);
    }

    @Override
    public void onAddressDeleted() {
        mDeletionState.setValue(DeletionStateNavigator.DELETION_SUCCESSFUL);
    }

    @Override
    public void onDeletionNotAvailable() {
        mDeletionState.setValue(DeletionStateNavigator.DELETION_FAILED);
        mSnackbarTextResource.setValue(R.string.unexpected_error);
        Log.d(TAG, "onDeletionNotAvailable: Failed to delete address with id: " + mAddressId);
    }
}
