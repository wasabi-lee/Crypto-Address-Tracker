package io.incepted.cryptoaddresstracker.viewModels;

import android.annotation.SuppressLint;
import android.app.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressLocalCallbacks;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressRemoteCallbacks;
import io.incepted.cryptoaddresstracker.data.txExtraWrapper.TxExtraWrapper;
import io.incepted.cryptoaddresstracker.listeners.CopyListener;
import io.incepted.cryptoaddresstracker.navigators.DeletionStateNavigator;
import io.incepted.cryptoaddresstracker.network.ConnectivityChecker;
import io.incepted.cryptoaddresstracker.network.PriceFetcher;
import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.Token;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.TokenInfo;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.utils.CopyUtils;
import io.incepted.cryptoaddresstracker.utils.CurrencyUtils;
import io.incepted.cryptoaddresstracker.utils.SharedPreferenceHelper;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailViewModel extends AndroidViewModel implements AddressLocalCallbacks.OnAddressLoadedListener,
        AddressLocalCallbacks.OnAddressDeletedListener, AddressLocalCallbacks.OnAddressUpdatedListener {

    private static final String TAG = DetailViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;

    private int mAddressId;

    private boolean shouldUpdateAddrName = false;

    public ObservableField<Address> mAddress = new ObservableField<>();
    public ObservableField<CurrentPrice> mCurrentPrice = new ObservableField<>();
    public ObservableArrayList<Token> mTokens = new ObservableArrayList<>();

    public ObservableField<Boolean> isLoading = new ObservableField<>();
    public ObservableField<Boolean> isContractAddress = new ObservableField<>(false);

    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();
    private SingleLiveEvent<TxExtraWrapper> mOpenTokenTransactions = new SingleLiveEvent<>();

    private SingleLiveEvent<DeletionStateNavigator> mDeletionState = new SingleLiveEvent<>();
    public CopyListener copyListener = value -> CopyUtils.copyText(getApplication().getApplicationContext(), value);


    public DetailViewModel(@NonNull Application application,
                           @NonNull AddressRepository addressRepository) {
        super(application);
        mAddressRepository = addressRepository;

    }

    public void start(int addressId) {
        this.mAddressId = addressId;
        if (ConnectivityChecker.isConnected(getApplication())) {
            loadAddress(addressId);
            loadCurrentPrice();
        } else {
            mSnackbarTextResource.setValue(R.string.error_offline);
        }
    }

    public void loadAddress(int addressId) {
        isLoading.set(true); // Show progress bar
        mAddressRepository.getAddress(addressId, this);
    }

    private void loadCurrentPrice() {
        int tsymIntValue = SharedPreferenceHelper.getBaseCurrencyPrefValue(getApplication().getApplicationContext());
        String tsym = CurrencyUtils.getBaseCurrencyString(tsymIntValue);

        PriceFetcher.loadCurrentPrice(tsym, Schedulers.io(),
                AndroidSchedulers.mainThread(),
                new PriceFetcher.OnPriceLoadedListener() {
                    @Override
                    public void onPriceLoaded(CurrentPrice currentPrice) {
                        mCurrentPrice.set(currentPrice);
                        mCurrentPrice.notifyChange();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        handleError(throwable);
                    }
                });

    }

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
        try {
            TxExtraWrapper wrapper = new TxExtraWrapper(mAddressId,
                    tokenName, tokenAddress, isContractAddress.get());
            mOpenTokenTransactions.setValue(wrapper);
        } catch (NullPointerException e) {
            handleError(e);
        }
    }

    private void updateNameChangeToView(Address address) {
        // Updating the view with the new name
        this.mAddress.get().setName(address.getName());
        this.mAddress.notifyChange();
        mSnackbarTextResource.setValue(R.string.address_edit_successful);

        // Setting the flag to default so that it can perform normal api call next time.
        shouldUpdateAddrName = false;
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


    // ----------------------------- Setters ---------------------------

    public void setmAddressId(int mAddressId) {
        this.mAddressId = mAddressId;
    }


    // ----------------------------- Callbacks -------------------------

    @SuppressLint("CheckResult")
    @Override
    public void onAddressLoaded(Address address) {

        if (shouldUpdateAddrName) {
            // Retrieving the updated address name only.
            // Do not trigger API call because we want to just update the name alone.
            updateNameChangeToView(address);

        } else {

            // Notifying databinding layout the data change first
            this.mAddress.set(address);

            // Network call after
            mAddressRepository.fetchDetailedAddressInfo(address.getAddrValue(),
                    new AddressRemoteCallbacks.DetailAddressInfoListener() {
                        @Override
                        public void onCallReady() {
                            /* empty */
                        }

                        @Override
                        public void onSimpleAddressInfoLoaded(RemoteAddressInfo remoteAddressInfo) {
                            updateViews(remoteAddressInfo);
                        }

                        @Override
                        public void onDataNotAvailable(Throwable throwable) {
                            isLoading.set(false);
                            handleError(throwable);
                        }
                    });
        }
    }

    @Override
    public void onAddressNotAvailable() {
        mSnackbarTextResource.setValue(R.string.address_loading_error);
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
        mSnackbarTextResource.setValue(R.string.unexpected_error);
    }

    public void updateViews(RemoteAddressInfo remoteAddressInfo) {

        if (remoteAddressInfo.isError()) {
            getSnackbarText().setValue(remoteAddressInfo.getError().getMessage());
            isLoading.set(false);
            return;
        }

        isContractAddress.set(remoteAddressInfo.getContractInfo() != null);
        realignTokenList(remoteAddressInfo);
        updateTokenList(remoteAddressInfo.getTokens());

        updateAddressInfo(remoteAddressInfo);

        // hide progress bar
        isLoading.set(false);
    }

    private void realignTokenList(RemoteAddressInfo remoteAddressInfo) {
        if (remoteAddressInfo.getTokens() == null) {
            // No tokens in this address. Initiate arraylist and put eth alone.
            remoteAddressInfo.setTokens(new ArrayList<>());
        } else {
            sortTokenList(remoteAddressInfo.getTokens());
        }
        putEthInfoFront(remoteAddressInfo);
    }

    private void putEthInfoFront(RemoteAddressInfo remoteAddressInfo) {
        Token eth = new Token();
        TokenInfo ethInfo = new TokenInfo();
        Double ethBalance = remoteAddressInfo.getEthBalanceInfo().getBalance();

        ethInfo.setAddress("base_currency_ethereum");
        ethInfo.setSymbol("ETH");
        ethInfo.setName("Ethereum");

        eth.setTokenInfo(ethInfo);
        eth.setBalance(ethBalance);

        remoteAddressInfo.getTokens().add(0, eth);
    }

    private void sortTokenList(List<Token> tokens) {
        Collections.sort(tokens, (obj1, obj2) ->
                obj1.getTokenInfo().getName()
                        .compareToIgnoreCase(obj2.getTokenInfo().getName()));
    }

    private void updateTokenList(List<Token> tokens) {
        mTokens.clear();
        mTokens.addAll(tokens);
    }

    private void updateAddressInfo(RemoteAddressInfo remoteAddressInfo) {
        Address updatedAddress = mAddress.get();
        updatedAddress.setRemoteAddressInfo(remoteAddressInfo);
        mAddress.set(updatedAddress);
        mAddress.notifyChange();
    }


    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        getSnackbarTextResource().setValue(R.string.unexpected_error);
    }
}
