package io.incepted.cryptoaddresstracker.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.Navigators.ActivityNavigator;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel implements AddressLocalDataSource.OnAddressesLoadedListener {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private AddressLocalRepository mLocalRepository;
    private AddressRemoteRepository mRemoteRepository;

    public ObservableField<Boolean> addressesExist = new ObservableField<>();
    public ObservableField<Boolean> isDataLoading = new ObservableField<>(false);
    public ObservableList<Address> mAddressList = new ObservableArrayList<>();

    private MutableLiveData<ActivityNavigator> mActivityNavigator = new MutableLiveData<>();
    private MutableLiveData<Integer> mOpenAddressDetail = new MutableLiveData<>();

    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();


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
        mLocalRepository.getAddresses(this);
    }


    // ----------------------- Activity transition ------------------

    // Resetting the value of the navigator back to null after the assignment
    // to prevent unnecessary activity transition call after recreating MainActivity.
    // This behavior occurs because when MutableLiveData.observe() method is called in MainActivity's OnCreate(),
    // and when the data already exists in that LiveData, that data gets delivered immediately to the activity
    // causing the transition to another activity even without the user click.
    // Resetting the navigator's value to null prevents this behavior.

    public void resetActivityNav() {
        mActivityNavigator.setValue(null);
    }

    public void addNewAddress() {
        mActivityNavigator.setValue(ActivityNavigator.NEW_ADDRESS);
        resetActivityNav();
    }

    public void openAddressDetail(int addressId) {
        mOpenAddressDetail.setValue(addressId);
        mOpenAddressDetail.setValue(null);
    }

    public void toSettings() {
        mActivityNavigator.setValue(ActivityNavigator.SETTINGS);
        resetActivityNav();
    }

    public void toTokenAddresses() {
        mActivityNavigator.setValue(ActivityNavigator.TOKEN_ADDRESS);
        resetActivityNav();
    }

    public void toTxScan() {
        mActivityNavigator.setValue(ActivityNavigator.TX_SCAN);
        resetActivityNav();
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
                        mSnackbarTextResource.setValue(null);
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
