package io.incepted.cryptoaddresstracker.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.Listeners.CopyListener;
import io.incepted.cryptoaddresstracker.Network.NetworkManager;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.CopyUtils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TokenTransferViewModel extends AndroidViewModel {

    private static final String TAG = TokenTransferViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;
    private String mTxHash;

    public ObservableField<TransactionInfo> mTxInfo = new ObservableField<>();
    public ObservableBoolean isLoading = new ObservableBoolean();
    public CopyListener mListener = value -> CopyUtils.copyText(getApplication().getApplicationContext(), value);

    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();

    public TokenTransferViewModel(@NonNull Application application, AddressRepository mAddressRepository) {
        super(application);
        this.mAddressRepository = mAddressRepository;
    }

    public void start(String txHash) {
        this.mTxHash = txHash;
        if (mTxHash != null) {
            loadTransactionInfo(txHash);
        }
    }

    @SuppressLint("CheckResult")
    private void loadTransactionInfo(String txHash) {

        isLoading.set(true);

        Single<TransactionInfo> txInfoSingle = NetworkManager.getTransactionDetailService()
                .getTransactionDetail(txHash, NetworkManager.API_KEY);

        txInfoSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            isLoading.set(false);
                            mTxInfo.set(result);
                            mTxInfo.notifyChange();
                        }
                        , this::handleError);

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
