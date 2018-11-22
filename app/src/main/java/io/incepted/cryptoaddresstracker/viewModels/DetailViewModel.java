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


    // ------------------------ Repositories ---------------------------
    private AddressRepository mAddressRepository;
    private TxListRepository mTxListRepository;


    // ------------------------ TX list live data components ----------------------
    private LiveData<SimpleTxItemResult> tokenTxResult;
    private LiveData<PagedList<SimpleTxItem>> tokenTxList;
    private LiveData<String> tokenNetworkError;
    private LiveData<Boolean> tokenTxExists;

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

    // ------------------------- etc ------------------------------------
    private SingleLiveEvent<DeletionStateNavigator> mDeletionState = new SingleLiveEvent<>();
    public CopyListener copyListener = value -> CopyUtils.copyText(getApplication().getApplicationContext(), value);


    public DetailViewModel(@NonNull Application application,
                           @NonNull AddressRepository addressRepository,
                           @NonNull PriceRepository priceRepository,
                           @NonNull TxListRepository txListRepository) {
        super(application);
        mAddressRepository = addressRepository;
        mTxListRepository = txListRepository;

        tokenTxResult = Transformations.map(mAddrValue, this::getTokenTxs);

        tokenTxList = Transformations.switchMap(tokenTxResult, SimpleTxItemResult::getItemLiveDataList);
        tokenNetworkError = Transformations.switchMap(tokenTxResult, SimpleTxItemResult::getError);
        tokenTxExists = Transformations.switchMap(tokenTxResult, SimpleTxItemResult::getItemExists);
    }


    // ------------------------------- Init -------------------------------------

    public void start(int addressId) {
        this.mAddressId = addressId;
        loadAddress(addressId);
    }


    private void loadAddress(int addressId) {
        isLoading.set(true); // Show progress bar
        mAddressRepository.getAddress(addressId, this);
    }


    private SimpleTxItemResult loadTransactions(TxListRepository.Type type, String address) {
        return mTxListRepository.getTxs(type, address, address);
    }


    private SimpleTxItemResult getTokenTxs(String address) {
        return loadTransactions(TxListRepository.Type.TOKEN_TXS, address);
    }


    // -------------------------- User Interactions ----------------------------

    public void updateAddressNewName(String newName) {
        // Setting the flag upfront
        shouldUpdateAddrName = true;

        // If the new input is empty, set the address itself as the default name
        if (newName.isEmpty()) {
            newName = mAddress.get().getAddrValue();
        }
        Address newAddress = mAddress.get();
        newAddress.setName(newName);

        mAddressRepository.updateAddress(newAddress, this);

    }

    public void deleteAddress() {
        mDeletionState.setValue(DeletionStateNavigator.DELETION_IN_PROGRESS);
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
        handleError();
    }


    @Override
    public void onAddressUpdated() {
        mAddressRepository.getAddress(mAddressId, this);
    }


    @Override
    public void onUpdateNotAvailable() {
        isLoading.set(false);
    }


    @Override
    public void onAddressDeleted() {
        mDeletionState.setValue(DeletionStateNavigator.DELETION_SUCCESSFUL);
    }


    @Override
    public void onDeletionNotAvailable() {
        isLoading.set(false);
        mDeletionState.setValue(DeletionStateNavigator.DELETION_FAILED);
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

    public MutableLiveData<DeletionStateNavigator> getDeletionState() {
        return mDeletionState;
    }

    public MutableLiveData<TxExtraWrapper> getOpenTokenTransactions() {
        return mOpenTokenTransactions;
    }

    public SingleLiveEvent<String> getOpenTxDetailActivity() {
        return mOpenTxDetailActivity;
    }

    public LiveData<PagedList<SimpleTxItem>> getTokenTxList() {
        return tokenTxList;
    }

    public LiveData<String> getTokenNetworkError() {
        return tokenNetworkError;
    }

    public MutableLiveData<Boolean> getIsTokenAddress() {
        return isTokenAddress;
    }

    public LiveData<Boolean> getTokenTxExists() {
        return tokenTxExists;
    }

    public MutableLiveData<Address> getAddressSLE() {
        return addressSLE;
    }

    // ----------------------------- Setters ---------------------------

    // for testing
    public void setAddressId(int mAddressId) {
        this.mAddressId = mAddressId;
    }

}
