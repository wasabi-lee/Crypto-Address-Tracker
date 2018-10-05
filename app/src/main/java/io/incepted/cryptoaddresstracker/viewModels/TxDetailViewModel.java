package io.incepted.cryptoaddresstracker.viewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.Objects;

import io.incepted.cryptoaddresstracker.data.source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.listeners.CopyListener;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.network.ConnectivityChecker;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.Operation;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.utils.CopyUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TxDetailViewModel extends AndroidViewModel {

    private static final String TAG = TxDetailViewModel.class.getSimpleName();

    private AddressLocalRepository mLocalRepository;
    private AddressRemoteRepository mRemoteRepository;

    private String txHash;

    public ObservableField<TransactionInfo> mTxInfo = new ObservableField<>();
    public ObservableBoolean isLoading = new ObservableBoolean();
    public CopyListener mListener = value -> CopyUtils.copyText(getApplication().getApplicationContext(), value);

    private MutableLiveData<String> openTokenOperations = new MutableLiveData<>();
    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();

    public TxDetailViewModel(@NonNull Application application,
                             @NonNull AddressLocalRepository localRepository,
                             @NonNull AddressRemoteRepository remoteRepository) {
        super(application);
        mLocalRepository = localRepository;
        mRemoteRepository = remoteRepository;
    }

    public void start(String txHash) {
        // load txhash detail
        this.txHash = txHash;
        if (ConnectivityChecker.isConnected(getApplication())) {
            fetchTxDetail(txHash);
        } else {
            mSnackbarTextResource.setValue(R.string.error_offline);
        }
    }

    @SuppressLint("CheckResult")
    public void fetchTxDetail(String txHash) {

        // show progress bar

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

    }



    public MutableLiveData<String> getOpenTokenOperations() {
        return openTokenOperations;
    }

    public void toTokenTransferActivity(String txHash) {
        if (mTxInfo.get() != null) {
            List<Operation> operations = Objects.requireNonNull(mTxInfo.get()).getOperations();
            if (operations != null && operations.size() != 0) {
                Log.d(TAG, "toTokenTransferActivity: clicked");
                openTokenOperations.setValue(txHash);
            }
        }
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
