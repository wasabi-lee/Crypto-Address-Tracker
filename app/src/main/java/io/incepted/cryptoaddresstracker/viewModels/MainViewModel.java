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
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;

public class MainViewModel extends AndroidViewModel implements AddressLocalCallbacks.OnAddressesLoadedListener {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;

    public ObservableField<Boolean> addressesExist = new ObservableField<>();
    public ObservableField<Boolean> isDataLoading = new ObservableField<>(false);
    public ObservableList<Address> mAddressList = new ObservableArrayList<>();

    private SingleLiveEvent<ActivityNavigator> mActivityNavigator = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mOpenAddressDetail = new SingleLiveEvent<>();

    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();


    public MainViewModel(@NonNull Application application,
                         @NonNull AddressRepository addressRepository) {
        super(application);
        mAddressRepository = addressRepository;
    }

    public void start() {
        loadAddresses();
    }

    public void loadAddresses() {
        isDataLoading.set(true);
        if (ConnectivityChecker.isConnected(getApplication())) {
            mAddressRepository.getAddresses(this);
        } else {
            mSnackbarTextResource.setValue(R.string.error_offline);
        }
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


    // ----------------------- Callbacks -------------------------

    @SuppressLint("CheckResult")
    @Override
    public void onAddressesLoaded(List<Address> addresses) {

        // show the 'no data' layout
        if (addresses.size() == 0) {
            addressesExist.set(false);
            isDataLoading.set(false);
            populateAddressListView(new ArrayList<>());
            return;
        }

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
                        isDataLoading.set(false);
                        populateAddressListView(addresses);
                    }

                    @Override
                    public void onDataNotAvailable(Throwable throwable) {
                        throwable.printStackTrace();
                        isDataLoading.set(false);
                        populateAddressListView(addresses);
                        mSnackbarTextResource.setValue(R.string.unexpected_error);
                    }
                });

    }

    public void populateAddressListView(List<Address> addresses) {
        mAddressList.clear();
        mAddressList.addAll(addresses);
    }


    public List<Address> tagListWithPositions(List<Address> addresses) {
        for (int i = 0; i < addresses.size(); i++) {
            addresses.get(i).setListPosition(i);
        }
        return addresses;
    }


    @Override
    public void onAddressesNotAvailable() {

    }
}
