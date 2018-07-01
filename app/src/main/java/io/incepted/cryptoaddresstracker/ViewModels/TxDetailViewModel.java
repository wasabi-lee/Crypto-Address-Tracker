package io.incepted.cryptoaddresstracker.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.Listeners.BottomSheetActionListener;
import io.incepted.cryptoaddresstracker.Network.NetworkManager;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.R;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TxDetailViewModel extends AndroidViewModel {

    private static final String TAG = TxDetailViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;
    private String txHash;

    public BottomSheetActionListener mBottomSheetActionListener = this::bottomSheetClicked;
    public ObservableField<TransactionInfo> mTxInfo = new ObservableField<>();

    private MutableLiveData<Void> mExpandBottomSheet = new MutableLiveData<>();
    private MutableLiveData<String> mSnackbarText = new MutableLiveData<>();
    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();

    public TxDetailViewModel(@NonNull Application application, AddressRepository repository) {
        super(application);
        mAddressRepository = repository;
    }

    public void start(String txHash) {
        // load txhash detail
        this.txHash = txHash;
        fetchTxDetail(txHash);
    }

    @SuppressLint("CheckResult")
    private void fetchTxDetail(String txHash) {
        Single<TransactionInfo> txInfoSingle = NetworkManager.getTransactionDetailService()
                .getTransactionDetail(txHash, NetworkManager.API_KEY);

        txInfoSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            mTxInfo.set(result);
                            mTxInfo.notifyChange();
                            Log.d(TAG, "fetchTxDetail: debug");
                        }
                        , this::handleError);

    }

    private void bottomSheetClicked() {
        mExpandBottomSheet.setValue(null);
    }

    public MutableLiveData<Void> getExpandBottomSheet() {
        return mExpandBottomSheet;
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
