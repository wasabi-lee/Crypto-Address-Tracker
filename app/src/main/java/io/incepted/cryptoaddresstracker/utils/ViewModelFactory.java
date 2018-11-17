package io.incepted.cryptoaddresstracker.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import io.incepted.cryptoaddresstracker.di.AddressRepositoryInjection;
import io.incepted.cryptoaddresstracker.di.PriceRepositoryInjection;
import io.incepted.cryptoaddresstracker.di.TxInfoRepositoryInjection;
import io.incepted.cryptoaddresstracker.di.TxListRepositoryInjection;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.repository.PriceRepository;
import io.incepted.cryptoaddresstracker.repository.TxInfoRepository;
import io.incepted.cryptoaddresstracker.repository.TxListRepository;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;
import io.incepted.cryptoaddresstracker.viewModels.MainViewModel;
import io.incepted.cryptoaddresstracker.viewModels.NewAddressViewModel;
import io.incepted.cryptoaddresstracker.viewModels.OverviewViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TokenTransferViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TxDetailViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TxListEthViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TxListTokenViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TxScanViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TxViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private static volatile ViewModelFactory INSTANCE;

    private final Application mApplication;
    private final AddressRepository mAddressRepository;
    private final TxListRepository mTxListRepository;
    private final TxInfoRepository mTxInfoRepository;
    private final PriceRepository mPriceRepository;

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null)
                    INSTANCE = new ViewModelFactory(application,
                            AddressRepositoryInjection.provideAddressRepository(application.getApplicationContext()),
                            TxInfoRepositoryInjection.provideTxListRepository(),
                            TxListRepositoryInjection.provideTxListRepository(),
                            PriceRepositoryInjection.providePriceRepository());

            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(Application application,
                             AddressRepository addressRepository,
                             TxInfoRepository txInfoRepository,
                             TxListRepository txListRepository,
                             PriceRepository priceRepository) {
        mApplication = application;
        mAddressRepository = addressRepository;
        mTxListRepository = txListRepository;
        mTxInfoRepository = txInfoRepository;
        mPriceRepository = priceRepository;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            //noinspection unchecked
            return (T) new MainViewModel(mApplication, mAddressRepository, mPriceRepository);
        } else if (modelClass.isAssignableFrom(NewAddressViewModel.class)) {
            //noinspection unchecked
            return (T) new NewAddressViewModel(mApplication, mAddressRepository);
        } else if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            //noinspection unchecked
            return (T) new DetailViewModel(mApplication, mAddressRepository, mPriceRepository, mTxListRepository);
        } else if (modelClass.isAssignableFrom(OverviewViewModel.class)) {
            //noinspection unchecked
            return (T) new OverviewViewModel(mApplication, mAddressRepository, mPriceRepository);
        } else if (modelClass.isAssignableFrom(TxListEthViewModel.class)) {
            //noinspection unchecked
            return (T) new TxListEthViewModel(mApplication, mTxListRepository);
        } else if (modelClass.isAssignableFrom(TxListTokenViewModel.class)) {
            //noinspection unchecked
            return (T) new TxListTokenViewModel(mApplication, mTxListRepository);
        } else if (modelClass.isAssignableFrom(TxViewModel.class)) {
            //noinspection unchecked
            return (T) new TxViewModel(mApplication, mAddressRepository, mTxListRepository);
        } else if (modelClass.isAssignableFrom(TxDetailViewModel.class)) {
            //noinspection unchecked
            return (T) new TxDetailViewModel(mApplication, mTxInfoRepository);
        } else if (modelClass.isAssignableFrom(TokenTransferViewModel.class)) {
            //noinspection unchecked
            return (T) new TokenTransferViewModel(mApplication, mTxInfoRepository);
        } else if (modelClass.isAssignableFrom(TxScanViewModel.class)) {
            //noinspection unchecked
            return (T) new TxScanViewModel(mApplication);
        }
        return super.create(modelClass);
    }
}
