package io.incepted.cryptoaddresstracker.viewModels;

import android.annotation.SuppressLint;
import android.app.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
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
        AddressLocalCallbacks.OnAddressDeletedListener, AddressLocalCallbacks.OnAddressUpdatedListener,
        AddressRemoteCallbacks.DetailAddressInfoListener,
        PriceRepository.OnPriceLoadedListener {


    private int mAddressId;
    private boolean shouldUpdateAddrName = false;


    // ------------------------ Repositories ---------------------------
    private AddressRepository mAddressRepository;
    private PriceRepository mPriceRepository;
    private TxListRepository mTxListRepository;


    // ------------------------ TX list live data components ----------------------
    private LiveData<PagedList<SimpleTxItem>> tokenTxList;
    private LiveData<PagedList<SimpleTxItem>> ethTxList;
    private LiveData<String> ethNetworkError;
    private LiveData<String> tokenNetworkError;
    private MutableLiveData<String> mAddrValue = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> isTokenAddress = new SingleLiveEvent<>();


    // ------------------------ ObservableFields for databinding ----------------------
    public ObservableField<Address> mAddress = new ObservableField<>();
    public ObservableField<CurrentPrice> mCurrentPrice = new ObservableField<>();
    public ObservableArrayList<Token> mTokens = new ObservableArrayList<>();
    public ObservableField<Boolean> noTokenFound = new ObservableField<>(false);
    public ObservableBoolean noEthTxFound = new ObservableBoolean(false);
    public ObservableBoolean noTokenTxFound = new ObservableBoolean(false);
    public ObservableField<Boolean> isLoading = new ObservableField<>();


    // ------------------------ SingleLiveEvent ----------------------
    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();
    private SingleLiveEvent<TxExtraWrapper> mOpenTokenTransactions = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mOpenTxDetailActivity = new SingleLiveEvent<>();


    // ------------------------- etc ------------------------------------
    private SingleLiveEvent<DeletionStateNavigator> mDeletionState = new SingleLiveEvent<>();
    public CopyListener copyListener = value -> CopyUtils.copyText(getApplication().getApplicationContext(), value);



    public DetailViewModel(@NonNull Application application,
                           @NonNull AddressRepository addressRepository,
                           @NonNull PriceRepository priceRepository,
                           @NonNull TxListRepository txListRepository) {
        super(application);
        mAddressRepository = addressRepository;
        mPriceRepository = priceRepository;
        mTxListRepository = txListRepository;

        LiveData<SimpleTxItemResult> ethTxResult = Transformations.map(isTokenAddress, this::getEthTxs);
        LiveData<SimpleTxItemResult> tokenTxResult = Transformations.map(mAddrValue, this::getTokenTxs);

        ethTxList = Transformations.switchMap(ethTxResult, this::getEthTxsListFromResult);
        tokenTxList = Transformations.switchMap(tokenTxResult, this::getTokenTxsListFromResult);
        ethNetworkError = Transformations.switchMap(ethTxResult, SimpleTxItemResult::getError);
        tokenNetworkError = Transformations.switchMap(tokenTxResult, SimpleTxItemResult::getError);
    }


    // ------------------------------- Init -------------------------------------

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


    private SimpleTxItemResult loadTransactions(TxListRepository.Type type, String address) {
        return mTxListRepository.getTxs(type, address, address);
    }


    private void loadCurrentPrice() {
        int tsymIntValue = SharedPreferenceHelper.getBaseCurrencyPrefValue(getApplication().getApplicationContext());
        String tsym = CurrencyUtils.getBaseCurrencyString(tsymIntValue);
        mPriceRepository.loadCurrentPrice(tsym, this);
    }


    private SimpleTxItemResult getEthTxs(Boolean isTokenAddress) {
        // For ETH transactions, boolean isTokenAddress works as a trigger of a SwitchMap,
        // which means we trigger the loadTransaction() method ONLY when we know
        // if the address we're looking at is a token address or not.
        // We determine whether to call loadTransactions() or not by the isTokenAddress value
        // since the ERC-20 token addresses cannot have external ETH transactions
        if (!isTokenAddress)
            return loadTransactions(TxListRepository.Type.ETH_TXS, mAddrValue.getValue());
        else return SimpleTxItemResult.getEmptyReference();
    }


    private SimpleTxItemResult getTokenTxs(String address) {
        return loadTransactions(TxListRepository.Type.TOKEN_TXS, address);
    }


    private LiveData<PagedList<SimpleTxItem>> getEthTxsListFromResult(SimpleTxItemResult result) {
        return result.getItemLiveDataList();
    }


    private LiveData<PagedList<SimpleTxItem>> getTokenTxsListFromResult(SimpleTxItemResult result) {
        return result.getItemLiveDataList();
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
            // Notifying databinding layout the data change first
            mAddress.set(address);
            mAddress.notifyChange();

            // This triggers the tokenTxResult SwitchMap call
            mAddrValue.setValue(address.getAddrValue());

            // Network call after
            mAddressRepository.fetchDetailedAddressInfo(address.getAddrValue(),
                    this);
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


    // -------- RemoteAddressInfo fetch callbacks (network) -----------
    @Override
    public void onCallReady() {
        /* empty */
    }


    @Override
    public void onRemoteAddressInfoLoaded(RemoteAddressInfo remoteAddressInfo) {
        updateViews(remoteAddressInfo);
    }


    @Override
    public void onDataNotAvailable(Throwable throwable) {
        isLoading.set(false);
        handleError(throwable);
    }


    // -------- Price fetch callbacks (network) -----------

    @Override
    public void onPriceLoaded(CurrentPrice currentPrice) {
        mCurrentPrice.set(currentPrice);
        mCurrentPrice.notifyChange();
    }

    @Override
    public void onError(Throwable throwable) {
        handleError(throwable);
    }



    // ---------------------------------------- UI update ---------------------------------------

    public void updateViews(RemoteAddressInfo remoteAddressInfo) {
        if (remoteAddressInfo.isError()) {
            getSnackbarText().setValue(remoteAddressInfo.getError().getMessage());
            isLoading.set(false);
            return;
        }
        realignTokenList(remoteAddressInfo);
        updateTokenList(remoteAddressInfo.getTokens());
        updateAddressInfo(remoteAddressInfo);

        boolean tokenAddress = remoteAddressInfo.isTokenAddress();
        isTokenAddress.setValue(tokenAddress);
        isLoading.set(false);
    }


    private void realignTokenList(RemoteAddressInfo remoteAddressInfo) {
        ArrayList<Token> tokens = new ArrayList<>();
        attachEthObject(tokens, remoteAddressInfo);
        if (remoteAddressInfo.getTokens() != null) {
            sortTokenList(remoteAddressInfo.getTokens());
            tokens.addAll(remoteAddressInfo.getTokens());
        }
        remoteAddressInfo.setTokens(tokens);
    }


    private void attachEthObject(ArrayList<Token> tokens, RemoteAddressInfo
            remoteAddressInfo) {
        Double ethBalance = remoteAddressInfo.getEthBalanceInfo().getBalance();

        // don't attach ETH object if the eth balance is 0.
        if (ethBalance == 0)
            return;
        Token eth = new Token();
        eth.convertToEthObject(ethBalance);
        tokens.add(eth);
    }


    private void sortTokenList(List<Token> tokens) {
        if (tokens != null)
            Collections.sort(tokens, (obj1, obj2) ->
                    obj1.getTokenInfo().getName()
                            .compareToIgnoreCase(obj2.getTokenInfo().getName()));
    }


    private void updateTokenList(List<Token> tokens) {
        if (tokens.size() == 0)
            noTokenFound.set(true);
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

    public LiveData<PagedList<SimpleTxItem>> getEthTxList() {
        return ethTxList;
    }

    public LiveData<String> getEthNetworkError() {
        return ethNetworkError;
    }

    public LiveData<String> getTokenNetworkError() {
        return tokenNetworkError;
    }

    public SingleLiveEvent<Boolean> getIsTokenAddress() {
        return isTokenAddress;
    }

    // ----------------------------- Setters ---------------------------

    // for testing
    public void setAddressId(int mAddressId) {
        this.mAddressId = mAddressId;
    }

}
