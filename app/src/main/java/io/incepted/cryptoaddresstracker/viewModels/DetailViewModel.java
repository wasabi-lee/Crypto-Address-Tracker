package io.incepted.cryptoaddresstracker.viewModels;

import android.annotation.SuppressLint;
import android.app.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressRemoteCallbacks;
import io.incepted.cryptoaddresstracker.data.txExtraWrapper.TxExtraWrapper;
import io.incepted.cryptoaddresstracker.listeners.CopyListener;
import io.incepted.cryptoaddresstracker.navigators.DeletionStateNavigator;
import io.incepted.cryptoaddresstracker.network.ConnectivityChecker;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.Token;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.SimpleTxItemResult;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.repository.PriceRepository;
import io.incepted.cryptoaddresstracker.repository.TxListRepository;
import io.incepted.cryptoaddresstracker.utils.CopyUtils;
import io.incepted.cryptoaddresstracker.utils.CurrencyUtils;
import io.incepted.cryptoaddresstracker.utils.SharedPreferenceHelper;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;
import timber.log.Timber;

public class DetailViewModel extends AndroidViewModel implements AddressLocalCallbacks.OnAddressLoadedListener,
        AddressLocalCallbacks.OnAddressDeletedListener, AddressLocalCallbacks.OnAddressUpdatedListener {


    private int mAddressId;
    private boolean shouldUpdateAddrName = false;

    private AddressRepository mAddressRepository;


    // ------------------------ TX list live data components ----------------------
    private MutableLiveData<String> mAddrValue = new MutableLiveData<>();
    private MutableLiveData<Boolean> isTokenAddress = new MutableLiveData<>();


    // ------------------------ ObservableFields for databinding ----------------------
    public ObservableField<Address> mAddress = new ObservableField<>();
    public ObservableField<Boolean> isLoading = new ObservableField<>();


    // ------------------------ SingleLiveEvent ----------------------
    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();
    private SingleLiveEvent<TxExtraWrapper> mOpenTokenTransactions = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mOpenTxDetailActivity = new SingleLiveEvent<>();
    private MutableLiveData<Address> addressSLE = new MutableLiveData<>();

    private MutableLiveData<Boolean> mIsDBLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsPriceLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsAddressInfoLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsEthTxLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsTokenTxLoading = new MutableLiveData<>();


    // ------------------------- etc ------------------------------------
    private SingleLiveEvent<Void> mFinishActivity = new SingleLiveEvent<>();
    public CopyListener copyListener = value -> CopyUtils.copyText(getApplication().getApplicationContext(), value);


    public DetailViewModel(@NonNull Application application,
                           @NonNull AddressRepository addressRepository) {
        super(application);
        mAddressRepository = addressRepository;

        mIsDBLoading.setValue(false);
        mIsPriceLoading.setValue(false);
        mIsAddressInfoLoading.setValue(false);
        mIsEthTxLoading.setValue(false);
        mIsTokenTxLoading.setValue(false);
    }


    // ------------------------------- Init -------------------------------------

    public void start(int addressId) {
        this.mAddressId = addressId;
        loadAddress(addressId);
    }


    private void loadAddress(int addressId) {
        mIsDBLoading.setValue(true);
        mAddressRepository.getAddress(addressId, this);
    }


    // -------------------------- User Interactions ----------------------------

    public void handleLoadingState() {
        try {
            if (mIsDBLoading.getValue() ||
                    mIsPriceLoading.getValue() ||
                    mIsAddressInfoLoading.getValue() ||
                    mIsEthTxLoading.getValue() ||
                    mIsTokenTxLoading.getValue()) {
                isLoading.set(true);
            } else {
                isLoading.set(false);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void updateAddressNewName(String newName) {
        // Setting the flag upfront
        shouldUpdateAddrName = true;

        // If the new input is empty, set the address itself as the default name
        if (newName.isEmpty()) newName = mAddress.get().getAddrValue();

        Address newAddress = mAddress.get();
        newAddress.setName(newName);

        mIsDBLoading.setValue(true);
        mAddressRepository.updateAddress(newAddress, this);

    }

    public void deleteAddress() {
        mIsDBLoading.setValue(true);
        mAddressRepository.deleteAddress(mAddressId, this);
    }


    public void toTxActivity(String tokenName, String tokenAddress) {
        TxExtraWrapper wrapper = new TxExtraWrapper(mAddressId, tokenName, tokenAddress);
        mOpenTokenTransactions.setValue(wrapper);
    }


    public void toTxDetailActivity(String transactionHash) {
        mOpenTxDetailActivity.setValue(transactionHash);
    }


    private void updateNameChangeToView(Address address) {
        // Updating the view with the new name
        mAddress.get().setName(address.getName());
        mAddress.notifyChange();
        mSnackbarTextResource.setValue(R.string.address_edit_successful);

        // Setting the flag to default so that it can perform normal api call next time.
        shouldUpdateAddrName = false;
    }


    // ----------------------------- Callbacks -------------------------

    // -------- Room Database Callbacks -------------
    @SuppressLint("CheckResult")
    @Override
    public void onAddressLoaded(Address address) {
        mIsDBLoading.setValue(false);
        if (shouldUpdateAddrName) {
            // Retrieving the updated address name only.
            // Do not trigger API call because we want to just update the name alone.
            updateNameChangeToView(address);
        } else {
            addressSLE.setValue(address);

            // Notifying databinding layout the data change first
            mAddress.set(address);
            mAddress.notifyChange();

            // This triggers the tokenTxResult SwitchMap call
            mAddrValue.setValue(address.getAddrValue());
        }
    }


    @Override
    public void onAddressNotAvailable() {
        mIsDBLoading.setValue(false);
        handleError();
    }


    @Override
    public void onAddressUpdated() {
        mIsDBLoading.setValue(false);
        mAddressRepository.getAddress(mAddressId, this);
    }


    @Override
    public void onUpdateNotAvailable() {
        mIsDBLoading.setValue(false);
    }


    @Override
    public void onAddressDeleted() {
        mIsDBLoading.setValue(false);
        mFinishActivity.setValue(null);
    }


    @Override
    public void onDeletionNotAvailable() {
        mIsDBLoading.setValue(false);
        handleError();
    }



    // ---------------------------------------- UI update ---------------------------------------


    private void handleError() {
        getSnackbarTextResource().setValue(R.string.unexpected_error);
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        getSnackbarTextResource().setValue(R.string.unexpected_error);
    }

    // ----------------------------- Getters ---------------------------

    public MutableLiveData<String> getSnackbarText() {
        return mSnackbarText;
    }

    public MutableLiveData<Integer> getSnackbarTextResource() {
        return mSnackbarTextResource;
    }

    public SingleLiveEvent<Void> getFinishActivity() {
        return mFinishActivity;
    }

    public MutableLiveData<TxExtraWrapper> getOpenTokenTransactions() {
        return mOpenTokenTransactions;
    }

    public MutableLiveData<Boolean> getIsTokenAddress() {
        return isTokenAddress;
    }

    public SingleLiveEvent<String> getOpenTxDetailActivity() {
        return mOpenTxDetailActivity;
    }

    public MutableLiveData<Address> getAddressSLE() {
        return addressSLE;
    }

    public MutableLiveData<Boolean> getIsDBLoading() {
        return mIsDBLoading;
    }

    public MutableLiveData<Boolean> getIsPriceLoading() {
        return mIsPriceLoading;
    }

    public MutableLiveData<Boolean> getIsAddressInfoLoading() {
        return mIsAddressInfoLoading;
    }

    public MutableLiveData<Boolean> getIsEthTxLoading() {
        return mIsEthTxLoading;
    }

    public MutableLiveData<Boolean> getIsTokenTxLoading() {
        return mIsTokenTxLoading;
    }

    // ----------------------------- Setters ---------------------------

    // for testing
    public void setAddressId(int mAddressId) {
        this.mAddressId = mAddressId;
    }

}
