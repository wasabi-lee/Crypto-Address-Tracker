package io.incepted.cryptoaddresstracker.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.repository.PriceRepository;

public class OverviewViewModel extends AndroidViewModel {

    private AddressRepository mAddressRepository;
    private PriceRepository mPriceRepository;

    public OverviewViewModel(@NonNull Application application,
                             @NonNull AddressRepository addressRepository,
                             @NonNull PriceRepository priceRepository) {
        super(application);
        mAddressRepository = addressRepository;
        mPriceRepository = priceRepository;
    }
}
