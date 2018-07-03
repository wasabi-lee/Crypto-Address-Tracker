package io.incepted.cryptoaddresstracker.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;

public class QRScanViewModel extends AndroidViewModel {

    AddressRepository mAddressRepository;

    public QRScanViewModel(@NonNull Application application, @NonNull AddressRepository repository) {
        super(application);
        this.mAddressRepository = repository;
    }


}
