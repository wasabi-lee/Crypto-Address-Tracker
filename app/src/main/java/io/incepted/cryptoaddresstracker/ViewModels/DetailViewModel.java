package io.incepted.cryptoaddresstracker.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.Listeners.CopyListener;
import io.incepted.cryptoaddresstracker.Navigators.DeletionStateNavigator;
import io.incepted.cryptoaddresstracker.Network.NetworkManager;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.Token;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.CopyUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailViewModel extends AndroidViewModel implements AddressDataSource.OnAddressLoadedListener, AddressDataSource.OnAddressDeletedListener {

    private static final String TAG = DetailViewModel.class.getSimpleName();

    private final AddressRepository mAddressRepository;

    private int mAddressId;

    public ObservableField<Address> mAddress = new ObservableField<>();
    public ObservableArrayList<Token> mTokens = new ObservableArrayList<>();

    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();
    private MutableLiveData<String> mOpenTokenTransactions = new MutableLiveData<>();

    private MutableLiveData<DeletionStateNavigator> mDeletionState = new MutableLiveData<>();
    public CopyListener copyListener = value -> CopyUtils.copyText(getApplication().getApplicationContext(), value);

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

    public MutableLiveData<String> getOpenTokenTransactions() {
        return mOpenTokenTransactions;
    }

    // ----------------------------- Callbacks -------------------------

    @SuppressLint("CheckResult")
    @Override
    public void onAddressLoaded(Address address) {
        // Notifying databinding layout the data change first
        this.mAddress.set(address);

        // Network call after
        NetworkManager.getDetailedAddressInfoService()
                .getDetailedAddressInfo(address.getAddrValue(), NetworkManager.API_KEY, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(remoteAddressInfo -> {
                            // Change address info
                            Address updatedAddress = mAddress.get();
                            updatedAddress.setRemoteAddressInfo(remoteAddressInfo);
                            mAddress.set(updatedAddress);
                            mAddress.notifyChange();

                            // Update token list
                            if (remoteAddressInfo.getContractInfo() != null) {
                                // Contract address!
                                // Show placeholder screen
                            } else {
                                mTokens.clear();
                                mTokens.addAll(remoteAddressInfo.getTokens());
                            }
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            getSnackbarTextResource().setValue(R.string.unexpected_error);
                        });
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
