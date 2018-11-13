package io.incepted.cryptoaddresstracker.viewModels;

import android.annotation.SuppressLint;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressLocalCallbacks;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressRemoteCallbacks;
import io.incepted.cryptoaddresstracker.navigators.ActivityNavigator;
import io.incepted.cryptoaddresstracker.network.ConnectivityChecker;
import io.incepted.cryptoaddresstracker.network.NetworkLiveData;
import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.repository.PriceRepository;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;

public class MainViewModel extends AndroidViewModel implements AddressLocalCallbacks.OnAddressesLoadedListener,
        PriceRepository.OnPriceLoadedListener {


    private AddressRepository mAddressRepository;
    private PriceRepository mPriceRepository;

    private boolean isBalanceLoading = false;
    private boolean isPriceLoading = false;
    public CurrentPrice mBaseCurrencyPrice = CurrentPrice.getDefaultBaseCurrencyObject();

    public SingleLiveEvent<CurrentPrice> mCurrentPrice = new SingleLiveEvent<>();

    public boolean mDisplayEthPrice = true;

    public ObservableField<Boolean> addressesExist = new ObservableField<>();
    public ObservableField<Boolean> isDataLoading = new ObservableField<>(false);
    public ObservableList<Address> mAddressList = new ObservableArrayList<>();

    private SingleLiveEvent<ActivityNavigator> mActivityNavigator = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mOpenAddressDetail = new SingleLiveEvent<>();

    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();

//    private ConnectivityChecker connectivityChecker;

    public NetworkLiveData networkLiveData;

    public MainViewModel(@NonNull Application application,
                         @NonNull AddressRepository addressRepository,
                         @NonNull PriceRepository priceRepository) {
        super(application);
        mAddressRepository = addressRepository;
        mPriceRepository = priceRepository;
        networkLiveData = new NetworkLiveData(application);
    }

    public void start(String baseCurrency, boolean displayEthInitially) {
        // configure which currency setting should be shown fist
        mDisplayEthPrice = displayEthInitially;
        updateDisplayCurrency(mDisplayEthPrice);

        // show progress bar
        isDataLoading.set(true);

        // load data
        loadAddresses();
        loadEthPrice(baseCurrency);
    }

    private void updateDisplayCurrency(boolean displayEthPrice) {
        if (displayEthPrice) {
            mCurrentPrice.setValue(CurrentPrice.getEthCurrencyObject());
        } else {
            mCurrentPrice.setValue(mBaseCurrencyPrice);
        }
    }

    public void loadAddresses() {
        isBalanceLoading = true;
        mAddressRepository.getAddresses(this);
    }


    public void loadEthPrice(String baseCurrency) {
        isPriceLoading = true;
        mPriceRepository.loadCurrentPrice(baseCurrency, this);
    }

    public boolean toggleDisplayCurrency() {
        mDisplayEthPrice = !mDisplayEthPrice;
        updateDisplayCurrency(mDisplayEthPrice);
        return mDisplayEthPrice;
    }


    // ----------------------- Activity transition ------------------

    public void addNewAddress() {
        mActivityNavigator.setValue(ActivityNavigator.NEW_ADDRESS);
    }

    public void openAddressDetail(int addressId) {
        mOpenAddressDetail.setValue(addressId);
    }

    public void toSettings() {
        mActivityNavigator.setValue(ActivityNavigator.SETTINGS);
    }

    public void toTokenAddresses() {
        mActivityNavigator.setValue(ActivityNavigator.TOKEN_ADDRESS);
    }

    public void toTxScan() {
        mActivityNavigator.setValue(ActivityNavigator.TX_SCAN);
    }


    // ----------------------- Getters -------------------


    public MutableLiveData<ActivityNavigator> getActivityNavigator() {
        return mActivityNavigator;
    }

    public MutableLiveData<String> getSnackbarText() {
        return mSnackbarText;
    }

    public MutableLiveData<Integer> getSnackbarTextResource() {
        return mSnackbarTextResource;
    }

    public MutableLiveData<Integer> getOpenAddressDetail() {
        return mOpenAddressDetail;
    }

    public SingleLiveEvent<CurrentPrice> getCurrentPrice() {
        return mCurrentPrice;
    }

    public boolean getShouldDisplayEthPrice() {
        return mDisplayEthPrice;
    }

    // ----------------------- Callbacks -------------------------

    @SuppressLint("CheckResult")
    @Override
    public void onAddressesLoaded(List<Address> addresses) {

        // show the 'no data' layout
        if (addresses.size() == 0) {
            addressesExist.set(false);
            populateAddressListView(new ArrayList<>());
            updateBalanceLoadingStatus(false);

        } else if (networkLiveData.getValue() != null && !networkLiveData.getValue()) {
            mSnackbarTextResource.setValue(R.string.error_offline);
            updateBalanceLoadingStatus(false);

        } else {
            // hide the 'no data' layout
            addressesExist.set(true);

            // populate the RecyclerView
            populateAddressListView(addresses);

            // Tagging each item with their position to keep the list in order even after the flatMap() call.
            List<Address> positionTaggedAddresses = tagListWithPositions(addresses);

            mAddressRepository.fetchMultipleSimpleAddressInfo(positionTaggedAddresses,
                    new AddressRemoteCallbacks.SimpleAddressInfoListener() {
                        @Override
                        public void onCallReady() {
                            /* empty */
                        }

                        @Override
                        public void onNextSimpleAddressInfoLoaded(Address addressInfo) {
                            addresses.set(addressInfo.getListPosition(), addressInfo);
                        }

                        @Override
                        public void onSimpleAddressInfoLoadingCompleted() {
                            populateAddressListView(addresses);
                            updateBalanceLoadingStatus(false);
                        }

                        @Override
                        public void onDataNotAvailable(Throwable throwable) {
                            throwable.printStackTrace();
                            populateAddressListView(addresses);
                            mSnackbarTextResource.setValue(R.string.unexpected_error);
                            updateBalanceLoadingStatus(false);
                        }
                    });
        }

    }

    @Override
    public void onAddressesNotAvailable() {
        updateBalanceLoadingStatus(false);
        mSnackbarTextResource.setValue(R.string.address_loading_error);
    }


    // ------------ Price load callbacks -----------

    @Override
    public void onPriceLoaded(CurrentPrice currentPrice) {
        mBaseCurrencyPrice = currentPrice;
        updateDisplayCurrency(mDisplayEthPrice);
        updatePriceLoadingStatus(false);
    }

    @Override
    public void onError(Throwable throwable) {
        mSnackbarTextResource.setValue(R.string.price_loading_error);
        updatePriceLoadingStatus(false);
    }


    private void updateBalanceLoadingStatus(boolean isLoading) {
        isBalanceLoading = isLoading;
        setLoadingStatus();
    }


    private void updatePriceLoadingStatus(boolean isLoading) {
        isPriceLoading = isLoading;
        setLoadingStatus();
    }


    private void setLoadingStatus() {
        if (!isBalanceLoading && !isPriceLoading) {
            isDataLoading.set(false);
        }
    }


    private void populateAddressListView(List<Address> addresses) {
        mAddressList.clear();
        mAddressList.addAll(addresses);
    }


    private List<Address> tagListWithPositions(List<Address> addresses) {
        for (int i = 0; i < addresses.size(); i++) {
            addresses.get(i).setListPosition(i);
        }
        return addresses;
    }

}
