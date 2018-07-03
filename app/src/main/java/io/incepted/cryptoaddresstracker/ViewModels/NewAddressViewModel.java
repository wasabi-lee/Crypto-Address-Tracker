package io.incepted.cryptoaddresstracker.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;

import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.Date;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.Navigators.AddressStateNavigator;
import io.incepted.cryptoaddresstracker.R;

public class NewAddressViewModel extends AndroidViewModel implements AddressDataSource.OnAddressSavedListener {

    private static final String TAG = NewAddressViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;

    public ObservableField<String> name = new ObservableField<>("");
    public ObservableField<String> address = new ObservableField<>("");

    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();
    private MutableLiveData<Void> openQRScanActivity = new MutableLiveData<>();
    private MutableLiveData<AddressStateNavigator> mAddressState = new MutableLiveData<>();

    public NewAddressViewModel(Application application, AddressRepository repository) {
        super(application);
        this.mAddressRepository = repository;
    }

    public void saveAddress() {
        mAddressState.setValue(AddressStateNavigator.SAVE_IN_PROGRESS);

        try {
            String nameInput = name.get().equals("") ? address.get() : name.get();
            String addressInput = address.get().trim();
            mAddressRepository.saveAddress(new Address(nameInput, addressInput, getCurrentTimestamp()), this);
        } catch (NullPointerException e) {
            e.printStackTrace();
            mSnackbarTextResource.setValue(R.string.unexpected_error);
        }
    }

    public Date getCurrentTimestamp() {
        return Calendar.getInstance().getTime();
    }

    public void toQRScanActivity() {
        openQRScanActivity.setValue(null);
    }

    public void handleActivityResult(IntentResult result) {
        if (result.getContents() == null) {
            // Cancel
            mSnackbarTextResource.setValue(R.string.scan_qr_code);
        } else {
            // Scanned successfully
            String scannedText = result.getContents();
            address.set(scannedText);
        }
    }


    // ------------- Getters

    public MutableLiveData<String> getSnackbarText() {
        return mSnackbarText;
    }

    public MutableLiveData<Integer> getSnackbarTextResource() {
        return mSnackbarTextResource;
    }

    public MutableLiveData<AddressStateNavigator> getAddressState() {
        return mAddressState;
    }

    public MutableLiveData<Void> getOpenQRScanActivity() {
        return openQRScanActivity;
    }

    // ------------- Callbacks

    @Override
    public void onAddressSaved() {
        // Close the activity and populate the list
        mAddressState.setValue(AddressStateNavigator.SAVE_SUCCESSFUL);
    }

    @Override
    public void onSaveNotAvailable() {
        mAddressState.setValue(AddressStateNavigator.SAVE_ERROR);
        mSnackbarTextResource.setValue(R.string.unexpected_error);
    }
}
