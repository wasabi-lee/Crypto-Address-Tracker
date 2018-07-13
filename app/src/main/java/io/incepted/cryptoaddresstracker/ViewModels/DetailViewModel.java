package io.incepted.cryptoaddresstracker.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.Data.TxExtraWrapper.TxExtraWrapper;
import io.incepted.cryptoaddresstracker.Listeners.CopyListener;
import io.incepted.cryptoaddresstracker.Navigators.DeletionStateNavigator;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.CurrentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.Token;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.TokenInfo;
import io.incepted.cryptoaddresstracker.Network.PriceFetcher;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.CopyUtils;
import io.incepted.cryptoaddresstracker.Utils.CurrencyUtils;
import io.incepted.cryptoaddresstracker.Utils.SharedPreferenceHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailViewModel extends AndroidViewModel implements AddressLocalDataSource.OnAddressLoadedListener,
        AddressLocalDataSource.OnAddressDeletedListener, AddressLocalDataSource.OnAddressUpdatedListener {

    private static final String TAG = DetailViewModel.class.getSimpleName();

    private final AddressLocalRepository mLocalRepository;
    private AddressRemoteRepository mRemoteRepository;

    private int mAddressId;

    public ObservableField<Address> mAddress = new ObservableField<>();
    public ObservableField<CurrentPrice> mCurrentPrice = new ObservableField<>();
    public ObservableArrayList<Token> mTokens = new ObservableArrayList<>();

    public ObservableField<Boolean> isLoading = new ObservableField<>();
    public ObservableField<Boolean> isContractAddress = new ObservableField<>(false);

    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();
    private MutableLiveData<TxExtraWrapper> mOpenTokenTransactions = new MutableLiveData<>();

    private MutableLiveData<DeletionStateNavigator> mDeletionState = new MutableLiveData<>();
    public CopyListener copyListener = value -> CopyUtils.copyText(getApplication().getApplicationContext(), value);


    public DetailViewModel(@NonNull Application application,
                           @NonNull AddressLocalRepository localRepository,
                           @NonNull AddressRemoteRepository remoteRepository) {
        super(application);
        this.mLocalRepository = localRepository;
        mRemoteRepository = remoteRepository;

    }

    public void start(int addressId) {
        this.mAddressId = addressId;
        loadAddress(addressId);
        loadCurrentPrice();
    }

    public void loadAddress(int addressId) {
        isLoading.set(true); // Show progress bar
        this.mLocalRepository.getAddress(addressId, this);
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
        // If the new input is empty, set the address itself as the default name
        if (newName.isEmpty()) {
            newName = mAddress.get().getAddrValue();
        }
        Address newAddress = mAddress.get();
        newAddress.setName(newName);
        mLocalRepository.updateAddress(newAddress, this);

    }

    public void deleteAddress() {
        mDeletionState.setValue(DeletionStateNavigator.DELETION_IN_PROGRESS);
        mLocalRepository.deleteAddress(mAddressId, this);
    }

    public void toTxActivity(String tokenName, String tokenAddress) {
        try {
            TxExtraWrapper wrapper = new TxExtraWrapper(mAddressId, tokenName, tokenAddress, isContractAddress.get());
            mOpenTokenTransactions.setValue(wrapper);
        } catch (NullPointerException e) {
            handleError(e);
        }
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
        // Notifying databinding layout the data change first
        this.mAddress.set(address);

        // Network call after
        mRemoteRepository.fetchDetailedAddressInfo(address.getAddrValue(), Schedulers.io(), AndroidSchedulers.mainThread(),
                new AddressRemoteDataSource.DetailAddressInfoListener() {
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

//        NetworkManager.getDetailedAddressInfoService()
//                .getDetailedAddressInfo(address.getAddrValue(), NetworkManager.API_KEY_ETHPLORER, true)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::updateViews,
//                        throwable -> {
//                            isLoading.set(false);
//                            handleError(throwable);
//                        });
    }

    @Override
    public void onAddressNotAvailable() {
        Log.d(TAG, "onDataNotAvailable: Failed to load address with id: " + mAddressId);
        mSnackbarTextResource.setValue(R.string.address_loading_error);
    }

    @Override
    public void onAddressUpdated(Address updatedAddress) {
        mSnackbarTextResource.setValue(R.string.address_edit_successful);
        Address update = mAddress.get();
        update.setName(updatedAddress.getName());
        mAddress.set(update);
        mAddress.notifyChange();
    }

    @Override
    public void onUpdateNotAvailable() {
        isLoading.set(false);
        Log.d(TAG, "onUpdateNotAvailable: Failed to update data with id: " + mAddressId);
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
        Log.d(TAG, "onDeletionNotAvailable: Failed to delete address with id: " + mAddressId);
    }

    public void updateViews(RemoteAddressInfo remoteAddressInfo) {

        if (remoteAddressInfo.isError()) {
            getSnackbarText().setValue(remoteAddressInfo.getError().getMessage());
            isLoading.set(false);
            return;
        }

        if (remoteAddressInfo.getContractInfo() != null) {
            isContractAddress.set(true);

        } else {
            isContractAddress.set(false);
            realignTokenList(remoteAddressInfo);
            updateTokenList(remoteAddressInfo.getTokens());
        }
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
