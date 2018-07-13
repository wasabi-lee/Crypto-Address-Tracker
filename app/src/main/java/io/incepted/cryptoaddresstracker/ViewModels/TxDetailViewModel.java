package io.incepted.cryptoaddresstracker.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.Objects;

import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.Listeners.CopyListener;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.Operation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.CopyUtils;
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
        fetchTxDetail(txHash);
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
