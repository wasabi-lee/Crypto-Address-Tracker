package io.incepted.cryptoaddresstracker.viewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.data.source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.navigators.ActivityNavigator;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.network.ConnectivityChecker;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel implements AddressLocalDataSource.OnAddressesLoadedListener {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private AddressLocalRepository mLocalRepository;
    private AddressRemoteRepository mRemoteRepository;

    public ObservableField<Boolean> addressesExist = new ObservableField<>();
    public ObservableField<Boolean> isDataLoading = new ObservableField<>(false);
    public ObservableList<Address> mAddressList = new ObservableArrayList<>();

    private SingleLiveEvent<ActivityNavigator> mActivityNavigator = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mOpenAddressDetail = new SingleLiveEvent<>();

    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();


    public MainViewModel(@NonNull Application application,
                         @NonNull AddressLocalRepository addressLocalRepository,
                         @NonNull AddressRemoteRepository remoteRepository) {
        super(application);
        mLocalRepository = addressLocalRepository;
        mRemoteRepository = remoteRepository;
    }

    public void start() {
        loadAddresses();
    }

    public void loadAddresses() {
        isDataLoading.set(true);
        if (ConnectivityChecker.isConnected(getApplication())) {
            mLocalRepository.getAddresses(this);
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

        mRemoteRepository.fetchMultipleSimpleAddressInfo(positionTaggedAddresses, Schedulers.io(), AndroidSchedulers.mainThread(),
                new AddressRemoteDataSource.SimpleAddressInfoListener() {
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
