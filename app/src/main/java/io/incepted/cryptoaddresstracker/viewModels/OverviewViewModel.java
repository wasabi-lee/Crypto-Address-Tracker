package io.incepted.cryptoaddresstracker.viewModels;

import android.app.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressRemoteCallbacks;
import io.incepted.cryptoaddresstracker.network.ConnectivityChecker;
import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.Token;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.repository.PriceRepository;
import io.incepted.cryptoaddresstracker.utils.CurrencyUtils;
import io.incepted.cryptoaddresstracker.utils.SharedPreferenceHelper;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;
import timber.log.Timber;

public class OverviewViewModel extends AndroidViewModel implements PriceRepository.OnPriceLoadedListener,
        AddressRemoteCallbacks.DetailAddressInfoListener {

    private AddressRepository mAddressRepository;
    private PriceRepository mPriceRepository;

    public ObservableField<Address> mAddress = new ObservableField<>();
    public ObservableField<CurrentPrice> mCurrentPrice = new ObservableField<>();
    public ObservableArrayList<Token> mTokens = new ObservableArrayList<>();
    public ObservableBoolean mNoTokenFound = new ObservableBoolean(false);

    private SingleLiveEvent<Integer> mSnackbarTextRes = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();

    private MutableLiveData<Boolean> mIsTokenAddress = new MutableLiveData<>();


    public OverviewViewModel(@NonNull Application application,
                             @NonNull AddressRepository addressRepository,
                             @NonNull PriceRepository priceRepository) {
        super(application);
        mAddressRepository = addressRepository;
        mPriceRepository = priceRepository;
    }


    public void loadDataFromNetwork(Address address) {
        setAddress(address);
        if (ConnectivityChecker.isConnected(getApplication())) {
            loadCurrentPrice();
            loadAddressInfo(address);
        } else {
            mSnackbarTextRes.setValue(R.string.error_offline);
        }
    }


    public void loadCurrentPrice() {
        int tsymIntValue = SharedPreferenceHelper.getBaseCurrencyPrefValue(getApplication().getApplicationContext());
        String tsym = CurrencyUtils.getBaseCurrencyString(tsymIntValue);
        mPriceRepository.loadCurrentPrice(tsym, this);
    }


    private void loadAddressInfo(Address address) {
        mAddressRepository.fetchDetailedAddressInfo(address.getAddrValue(), this);
    }


    @Override
    public void onPriceLoaded(CurrentPrice currentPrice) {
        Timber.d("Current Price: %s %s", currentPrice.getPrice(), currentPrice.getTsym());
        mCurrentPrice.set(currentPrice);
        mCurrentPrice.notifyChange();
    }


    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        mSnackbarTextRes.setValue(R.string.error_loading_price);
    }


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

    }

    public void updateViews(RemoteAddressInfo remoteAddressInfo) {
        if (remoteAddressInfo.isError()) {
            mSnackbarText.setValue(remoteAddressInfo.getError().getMessage());
//            isLoading.set(false);
            return;
        }
        realignTokenList(remoteAddressInfo);
        updateTokenList(remoteAddressInfo.getTokens());
        updateAddressInfo(remoteAddressInfo);

        boolean tokenAddress = remoteAddressInfo.isTokenAddress();
        mIsTokenAddress.setValue(tokenAddress);
//        isLoading.set(false);
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


    private void attachEthObject(ArrayList<Token> tokens, RemoteAddressInfo remoteAddressInfo) {
        Double ethBalance = remoteAddressInfo.getEthBalanceInfo().getBalance();

        // don't attach ETH object if the eth balance is 0.
        if (ethBalance == 0) return;

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
        mNoTokenFound.set(tokens.size() == 0);
        mTokens.clear();
        mTokens.addAll(tokens);
    }


    private void updateAddressInfo(RemoteAddressInfo remoteAddressInfo) {
        Address updatedAddress = mAddress.get();
        updatedAddress.setRemoteAddressInfo(remoteAddressInfo);
        mAddress.set(updatedAddress);
        mAddress.notifyChange();
    }


    public void setAddress(Address address) {
        mAddress.set(address);
        mAddress.notifyChange();
    }

    public MutableLiveData<Boolean> getIsTokenAddress() {
        return mIsTokenAddress;
    }

    public ObservableField<CurrentPrice> getmCurrentPrice() {
        return mCurrentPrice;
    }

    public SingleLiveEvent<Integer> getSnackbarTextRes() {
        return mSnackbarTextRes;
    }

    public SingleLiveEvent<String> getSnackbarText() {
        return mSnackbarText;
    }
}
