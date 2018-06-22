package io.incepted.cryptoaddresstracker.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.Navigators.ActivityNavigator;

public class MainViewModel extends AndroidViewModel implements AddressDataSource.OnAddressesLoadedListener {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;

    private ObservableField<Boolean> addressesExist = new ObservableField<>();
    private MutableLiveData<ActivityNavigator> mActivityNavigator = new MutableLiveData<>();
    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();


    public MainViewModel(@NonNull Application application, @NonNull AddressRepository repository) {
        super(application);
        mAddressRepository = repository;
    }

    public void start() {
        loadAddresses();
    }

    private void loadAddresses() {
        mAddressRepository.getAddresses(this);
    }



    // ----------------------- Activity transition ------------------

    public void addNewAddress() {
        mActivityNavigator.setValue(ActivityNavigator.NEW_ADDRESS);
    }

    public void openAddressDetail() {
        mActivityNavigator.setValue(ActivityNavigator.ADDRESS_DETAIL);
    }

    public void toSettings() {
        mActivityNavigator.setValue(ActivityNavigator.SETTINGS);
    }

    public void toTokenAddresses() {
        mActivityNavigator.setValue(ActivityNavigator.TOKEN_ADDRESS);
    }



    // ----------------------- Getters -------------------

    public boolean getAddressesExist() {
        return addressesExist != null ? addressesExist.get() : false;
    }

    public MutableLiveData<ActivityNavigator> getmActivityNavigator() {
        return mActivityNavigator;
    }

    public MutableLiveData<String> getmSnackbarText() {
        return mSnackbarText;
    }

    public MutableLiveData<Integer> getmSnackbarTextResource() {
        return mSnackbarTextResource;
    }

    // ----------------------- Callbacks -------------------------

    @Override
    public void onAddressesLoaded(List<Address> addresses) {
        addressesExist.set(addresses.size() != 0);
        Log.d(TAG, "onAddressesLoaded: Addresses.size(): " + addresses.size());
    }

    @Override
    public void onDataNotAvailable() {

    }
}
