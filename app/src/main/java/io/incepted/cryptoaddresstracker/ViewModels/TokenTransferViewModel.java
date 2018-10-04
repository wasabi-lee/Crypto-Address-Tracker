package io.incepted.cryptoaddresstracker.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.Listeners.CopyListener;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.Network.ConnectivityChecker;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.CopyUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TokenTransferViewModel extends AndroidViewModel {

    private static final String TAG = TokenTransferViewModel.class.getSimpleName();

    private AddressLocalRepository mLocalRepository;
    private AddressRemoteRepository mRemoteRepository;

    private String mTxHash;

    public ObservableField<TransactionInfo> mTxInfo = new ObservableField<>();
    public ObservableBoolean isLoading = new ObservableBoolean();
    public CopyListener mListener = value -> CopyUtils.copyText(getApplication().getApplicationContext(), value);

    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();

    public TokenTransferViewModel(@NonNull Application application,
                                  @NonNull AddressLocalRepository localRepository,
                                  @NonNull AddressRemoteRepository remoteRepository) {
        super(application);
        this.mLocalRepository = localRepository;
        mRemoteRepository = remoteRepository;

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
            mRemoteRepository.fetchTransactionDetail(txHash, Schedulers.io(), AndroidSchedulers.mainThread(),
                    new AddressRemoteDataSource.TransactionInfoListener() {
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
        mSnackbarTextResource.setValue(null);
    }
}
