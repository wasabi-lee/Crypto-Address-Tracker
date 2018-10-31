package io.incepted.cryptoaddresstracker.viewModels;

import android.app.Application;
import android.util.Log;

import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;
import timber.log.Timber;

public class TxScanViewModel extends AndroidViewModel {

    private static final String TAG = TxScanViewModel.class.getSimpleName();


    public ObservableField<String> mTxHashInput = new ObservableField<>("");

    private SingleLiveEvent<Void> mInitiateScan = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mOpenTxDetail = new SingleLiveEvent<>();

    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();

    public TxScanViewModel(@NonNull Application application) {
        super(application);
    }


    public void toTxDetailActivity() {
        String input = mTxHashInput.get();
        if (input == null || input.isEmpty() ||
                input.length() != 66 || !input.startsWith("0x")) {
            getSnackbarTextResource().setValue(R.string.error_not_txhash);
        } else {
            mOpenTxDetail.setValue(input);
        }
    }

    public void initiateScan() {
        mInitiateScan.setValue(null);
    }

    public void handleActivityResult(IntentResult result) {
        if (result.getContents() == null) {
            // Cancel
            Timber.d("handleActivityResult: Scan cancelled");
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
