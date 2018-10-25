package io.incepted.cryptoaddresstracker.viewModels;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.data.source.callbacks.TxInfoCallbacks;
import io.incepted.cryptoaddresstracker.listeners.CopyListener;
import io.incepted.cryptoaddresstracker.network.ConnectivityChecker;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.repository.TxInfoRepository;
import io.incepted.cryptoaddresstracker.utils.CopyUtils;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;

public class TokenTransferViewModel extends AndroidViewModel {

    private static final String TAG = TokenTransferViewModel.class.getSimpleName();

    private TxInfoRepository mTxInfoRepository;

    private String mTxHash;

    public ObservableField<TransactionInfo> mTxInfo = new ObservableField<>();
    public ObservableBoolean isLoading = new ObservableBoolean();
    public CopyListener mListener = value -> CopyUtils.copyText(getApplication().getApplicationContext(), value);

    private SingleLiveEvent<String> mSnackbarText = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mSnackbarTextResource = new SingleLiveEvent<>();

    public TokenTransferViewModel(@NonNull Application application,
                                  @NonNull TxInfoRepository txInfoRepository) {
        super(application);
        mTxInfoRepository = txInfoRepository;

    }

    public void start(String txHash) {
        this.mTxHash = txHash;
        if (mTxHash != null) {
            loadTransactionInfo(txHash);
        }
    }

    @SuppressLint("CheckResult")
    public void loadTransactionInfo(String txHash) {

        if (ConnectivityChecker.isConnected(getApplication())) {
            mTxInfoRepository.fetchTransactionDetail(txHash, new TxInfoCallbacks.TransactionInfoListener() {
                        @Override
                        public void onCallReady() {
                            isLoading.set(true);
                        }

                        @Override
                        public void onTransactionDetailReady(TransactionInfo transactionDetail) {
                            isLoading.set(false);
                            mTxInfo.set(transactionDetail);
                            mTxInfo.notifyChange();
                        }

                        @Override
                        public void onDataNotAvailable(Throwable throwable) {
                            isLoading.set(false);
                            handleError(throwable);
                        }
                    });
        } else {
            mSnackbarTextResource.setValue(R.string.error_offline);
        }
    }


    // -------------------------------- Getters ----------------------------------


    public ObservableField<TransactionInfo> getmTxInfo() {
        return mTxInfo;
    }

    public MutableLiveData<String> getSnackbarText() {
        return mSnackbarText;
    }

    public MutableLiveData<Integer> getSnackbarTextResource() {
        return mSnackbarTextResource;
    }

    private void handleError(int errorMessage) {
        mSnackbarTextResource.setValue(errorMessage);
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        mSnackbarTextResource.setValue(R.string.unexpected_error);
    }
}
