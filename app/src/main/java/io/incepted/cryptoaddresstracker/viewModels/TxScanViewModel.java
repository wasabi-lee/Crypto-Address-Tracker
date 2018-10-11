package io.incepted.cryptoaddresstracker.viewModels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.databinding.ObservableField;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.zxing.integration.android.IntentResult;

import io.incepted.cryptoaddresstracker.data.source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;

public class TxScanViewModel extends AndroidViewModel {

    private static final String TAG = TxScanViewModel.class.getSimpleName();

    private AddressLocalRepository mLocalRepository;
    private AddressRemoteRepository mRemoteRepository;

    public ObservableField<String> mTxHashInput = new ObservableField<>("");

    private SingleLiveEvent<Void> mInitiateScan = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mOpenTxDetail = new SingleLiveEvent<>();

    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();

    public TxScanViewModel(@NonNull Application application,
                           @NonNull AddressLocalRepository localRepository,
                           @NonNull AddressRemoteRepository remoteRepository) {
        super(application);
        this.mLocalRepository = localRepository;
        this.mRemoteRepository = remoteRepository;
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
