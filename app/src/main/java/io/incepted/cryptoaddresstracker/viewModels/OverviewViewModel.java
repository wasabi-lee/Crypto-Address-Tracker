package io.incepted.cryptoaddresstracker.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.repository.PriceRepository;
import io.incepted.cryptoaddresstracker.utils.CurrencyUtils;
import io.incepted.cryptoaddresstracker.utils.SharedPreferenceHelper;
import io.incepted.cryptoaddresstracker.utils.SingleLiveEvent;
import timber.log.Timber;

public class OverviewViewModel extends AndroidViewModel implements PriceRepository.OnPriceLoadedListener {

    private AddressRepository mAddressRepository;
    private PriceRepository mPriceRepository;

    private ObservableField<Address> mAddress = new ObservableField<>();
    public ObservableField<CurrentPrice> mCurrentPrice = new ObservableField<>();

    private SingleLiveEvent<Integer> mSnackbarTextRes = new SingleLiveEvent<>();


    public OverviewViewModel(@NonNull Application application,
                             @NonNull AddressRepository addressRepository,
                             @NonNull PriceRepository priceRepository) {
        super(application);
        mAddressRepository = addressRepository;
        mPriceRepository = priceRepository;
    }



    public void loadCurrentPrice() {
        int tsymIntValue = SharedPreferenceHelper.getBaseCurrencyPrefValue(getApplication().getApplicationContext());
        String tsym = CurrencyUtils.getBaseCurrencyString(tsymIntValue);
        mPriceRepository.loadCurrentPrice(tsym, this);
    }

    @Override
    public void onPriceLoaded(CurrentPrice currentPrice) {
        Timber.d("Current Price: %s %s", currentPrice.getPrice(), currentPrice.getTsym());
        mCurrentPrice.set(currentPrice);
        mCurrentPrice.notifyChange();
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        mSnackbarTextRes.setValue(R.string.error_loading_price);
    }

    public void setAddress(Address address) {
        this.mAddress.set(address);
    }


    public ObservableField<CurrentPrice> getmCurrentPrice() {
        return mCurrentPrice;
    }

    public SingleLiveEvent<Integer> getSnackbarTextRes() {
        return mSnackbarTextRes;
    }
}
