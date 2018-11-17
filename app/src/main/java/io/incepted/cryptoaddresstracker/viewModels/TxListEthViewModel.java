package io.incepted.cryptoaddresstracker.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.incepted.cryptoaddresstracker.repository.TxListRepository;

public class TxListEthViewModel extends AndroidViewModel {

    private TxListRepository mTxListRepository;

    public TxListEthViewModel(@NonNull Application application,
                              @NonNull TxListRepository txListRepository) {
        super(application);
        mTxListRepository = txListRepository;
    }
}
