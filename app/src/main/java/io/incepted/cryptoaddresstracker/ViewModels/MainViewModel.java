package io.incepted.cryptoaddresstracker.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;

import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.Navigators.ActivityNavigator;
import io.incepted.cryptoaddresstracker.Network.NetworkManager;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.Network.NetworkService;
import io.incepted.cryptoaddresstracker.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel implements AddressDataSource.OnAddressesLoadedListener {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;

    public ObservableField<Boolean> addressesExist = new ObservableField<>();
    public ObservableField<Boolean> isDataLoading = new ObservableField<>(false);
    public ObservableList<Address> mAddressList = new ObservableArrayList<>();

    private MutableLiveData<ActivityNavigator> mActivityNavigator = new MutableLiveData<>();
    private MutableLiveData<Integer> mOpenAddressDetail = new MutableLiveData<>();

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
        isDataLoading.set(true);
        mAddressRepository.getAddresses(this);
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
            return;
        }

        // hide the 'no data' layout
        addressesExist.set(true);
        // populate the RecyclerView
        populateAddressListView(addresses);


        // Tagging each item with their position to keep the list in order even after the flatMap() call.
        List<Address> positionTaggedAddresses = tagListWithPositions(addresses);

        NetworkService networkService = NetworkManager.getSimpleAddressInfoService();
        Observable.fromIterable(positionTaggedAddresses)
                // Using flatMap() over concatMap() for the concurrency.
                .flatMap(address ->
                        Observable.zip(
                                // List item data
                                Observable.just(address),
                                // Network call observable
                                networkService.getSimpleAddressInfo(address.getAddrValue(), NetworkManager.API_KEY, true),
                                // Merging function
                                (BiFunction<Address, RemoteAddressInfo, Object>) (emittedAddress, simpleAddressInfo) -> {
                                    emittedAddress.setRemoteAddressInfo(simpleAddressInfo);
                                    return emittedAddress;
                                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // OnNext. Set the result to the appropriate position
                        result -> {
                            Address address = (Address) result;
                            addresses.set(address.getListPosition(), address);
                        },
                        // OnError
                        throwable -> {
                            throwable.printStackTrace();
                            mSnackbarTextResource.setValue(R.string.unexpected_error);
                        },
                        // OnComplete. Updating the RecyclerView.
                        () -> {
                            isDataLoading.set(false);
                            populateAddressListView(addresses);
                        });

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

    @Override
    public void onAddressesNotAvailable() {

    }
}
