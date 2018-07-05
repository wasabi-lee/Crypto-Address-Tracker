package io.incepted.cryptoaddresstracker.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.zxing.integration.android.IntentResult;

import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.R;

public class TxScanViewModel extends AndroidViewModel {

    private static final String TAG = TxScanViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;

    public ObservableField<String> mTxHashInput = new ObservableField<>("");

    private MutableLiveData<Void> mInitiateScan = new MutableLiveData<>();
    private MutableLiveData<String> mOpenTxDetail = new MutableLiveData<>();

    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();

    public TxScanViewModel(@NonNull Application application, @NonNull AddressRepository repository) {
        super(application);
        this.mAddressRepository = repository;
    }


    public void toTxDetailActivity() {
        mOpenTxDetail.setValue(mTxHashInput.get());
    }

    public void initiateScan() {
        mInitiateScan.setValue(null);
    }

    public void handleActivityResult(IntentResult result) {
        if (result.getContents() == null) {
            // Cancel
            Log.d(TAG, "handleActivityResult: Scan cancelled");
        } else {
            // Scanned successfully
            String scannedText = result.getContents();
            mTxHashInput.set(scannedText);
        }
    }


    // ---------------------- Getters -------------------------

    public MutableLiveData<Void> getInitiateScan() {
        return mInitiateScan;
    }

    public MutableLiveData<String> getOpenTxDetail() {
        return mOpenTxDetail;
    }

    public MutableLiveData<String> getSnackbarText() {
        return mSnackbarText;
    }

    public MutableLiveData<Integer> getSnackbarTextResource() {
        return mSnackbarTextResource;
    }

}
