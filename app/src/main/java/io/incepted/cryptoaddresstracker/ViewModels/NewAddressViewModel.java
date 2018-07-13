package io.incepted.cryptoaddresstracker.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.Date;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.Navigators.AddressStateNavigator;
import io.incepted.cryptoaddresstracker.R;

public class NewAddressViewModel extends AndroidViewModel implements AddressLocalDataSource.OnAddressSavedListener {

    private static final String TAG = NewAddressViewModel.class.getSimpleName();

    private AddressLocalRepository mLocalRepository;
    private AddressRemoteRepository mRemoteRepository;

    public ObservableField<String> name = new ObservableField<>("");
    public ObservableField<String> address = new ObservableField<>("");

    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();

    private MutableLiveData<Integer> mEditTextErrorText = new MutableLiveData<>();

    private MutableLiveData<Void> openQRScanActivity = new MutableLiveData<>();
    private MutableLiveData<AddressStateNavigator> mAddressState = new MutableLiveData<>();

    public NewAddressViewModel(@NonNull Application application,
                               @NonNull AddressLocalRepository localRepository,
                               @NonNull AddressRemoteRepository remoteRepository) {
        super(application);
        this.mLocalRepository = localRepository;
        mRemoteRepository = remoteRepository;

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
            mLocalRepository.saveAddress(new Address(nameInput, addressInput, getCurrentTimestamp()), this);
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
