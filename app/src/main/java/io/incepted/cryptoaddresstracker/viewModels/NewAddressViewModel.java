package io.incepted.cryptoaddresstracker.viewModels;

import android.app.Application;
import android.util.Log;

import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressLocalCallbacks;
import io.incepted.cryptoaddresstracker.navigators.AddressStateNavigator;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;

public class NewAddressViewModel extends AndroidViewModel implements AddressLocalCallbacks.OnAddressSavedListener {

    private static final String TAG = NewAddressViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;

    public ObservableField<String> name = new ObservableField<>("");
    public ObservableField<String> address = new ObservableField<>("");

    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();

    private SingleLiveEvent<Integer> mEditTextErrorText = new SingleLiveEvent<>();

    private SingleLiveEvent<Void> openQRScanActivity = new SingleLiveEvent<>();
    private SingleLiveEvent<AddressStateNavigator> mAddressState = new SingleLiveEvent<>();

    public NewAddressViewModel(@NonNull Application application,
                               @NonNull AddressRepository addressRepository) {
        super(application);
        mAddressRepository = addressRepository;

    }

    public void saveAddress() {
        mAddressState.setValue(AddressStateNavigator.SAVE_IN_PROGRESS);

        if (!address.get().startsWith("0x")) {
            mEditTextErrorText.setValue(R.string.error_not_address);
            mAddressState.setValue(AddressStateNavigator.SAVE_ERROR);
            return;
        }

        try {
            String nameInput = name.get().equals("") ? address.get() : name.get();
            String addressInput = address.get().trim();
            mAddressRepository.saveAddress(new Address(nameInput, addressInput, getCurrentTimestamp()), this);
        } catch (NullPointerException e) {
            e.printStackTrace();
            mSnackbarTextResource.setValue(R.string.unexpected_error);
        }
    }

    private Date getCurrentTimestamp() {
        return Calendar.getInstance().getTime();
    }

    public void toQRScanActivity() {
        openQRScanActivity.setValue(null);
    }

    public void handleActivityResult(IntentResult result) {
        if (result.getContents() == null) {
            // Cancel
            Log.d(TAG, "handleActivityResult: Scan cancelled");
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

    public MutableLiveData<Integer> getEditTextErrorText() {
        return mEditTextErrorText;
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
