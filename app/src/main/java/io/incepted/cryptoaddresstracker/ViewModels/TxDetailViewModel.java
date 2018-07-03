package io.incepted.cryptoaddresstracker.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.Listeners.CopyListener;
import io.incepted.cryptoaddresstracker.Network.NetworkManager;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.Operation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.CopyUtils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TxDetailViewModel extends AndroidViewModel {

    private static final String TAG = TxDetailViewModel.class.getSimpleName();

    private AddressRepository mAddressRepository;
    private String txHash;

//    public BottomSheetActionListener mBottomSheetActionListener = this::bottomSheetClicked;
    public ObservableField<TransactionInfo> mTxInfo = new ObservableField<>();
    public CopyListener mListener = value -> CopyUtils.copyText(getApplication().getApplicationContext(), value);

//    private MutableLiveData<Void> mExpandBottomSheet = new MutableLiveData<>();
    private MutableLiveData<String> openTokenOperations = new MutableLiveData<>();
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
                        }
                        , this::handleError);

    }

//    private void bottomSheetClicked() {
//        mExpandBottomSheet.setValue(null);
//    }
//
//    public MutableLiveData<Void> getExpandBottomSheet() {
//        return mExpandBottomSheet;
//    }


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
    }

}
