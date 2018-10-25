package io.incepted.cryptoaddresstracker.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.repository.PriceRepository;
import io.incepted.cryptoaddresstracker.repository.TxInfoRepository;
import io.incepted.cryptoaddresstracker.repository.TxListRepository;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;
import io.incepted.cryptoaddresstracker.viewModels.MainViewModel;
import io.incepted.cryptoaddresstracker.viewModels.NewAddressViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TokenTransferViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TxDetailViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TxScanViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TxViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private static volatile ViewModelFactory INSTANCE;

    private final Application mApplication;
    private final AddressRemoteRepository mRemoteRepository;
    private final AddressRepository mAddressRepository;

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null)
                    INSTANCE = new ViewModelFactory(application,
                            RemoteAddressRepositoryInjection.provideAddressRepository(),
                            LocalAddressRepositoryInjection.provideAddressRepository(application.getApplicationContext()),
                            null,
                            null,
                            null);

            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(Application application,
                             AddressRemoteRepository remoteRepository,
                             AddressRepository addressRepository,
                             TxInfoRepository txInfoRepository,
                             TxListRepository txListRepository,
                             PriceRepository priceRepository) {
        mApplication = application;
        mRemoteRepository = remoteRepository;
        mAddressRepository = addressRepository;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            //noinspection unchecked
            return (T) new MainViewModel(mApplication, mAddressRepository);
        } else if (modelClass.isAssignableFrom(NewAddressViewModel.class)) {
            //noinspection unchecked
            return (T) new NewAddressViewModel(mApplication, mAddressRepository);
        } else if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            //noinspection unchecked
            return (T) new DetailViewModel(mApplication, mAddressRepository);
        } else if (modelClass.isAssignableFrom(TxViewModel.class)) {
            //noinspection unchecked
            return (T) new TxViewModel(mApplication, mRemoteRepository, mAddressRepository);
        } else if (modelClass.isAssignableFrom(TxDetailViewModel.class)) {
            //noinspection unchecked
            return (T) new TxDetailViewModel(mApplication, mRemoteRepository);
        } else if (modelClass.isAssignableFrom(TokenTransferViewModel.class)) {
            //noinspection unchecked
            return (T) new TokenTransferViewModel(mApplication, mRemoteRepository);
        } else if (modelClass.isAssignableFrom(TxScanViewModel.class)) {
            //noinspection unchecked
            return (T) new TxScanViewModel(mApplication);
        }
        return super.create(modelClass);
    }
}
