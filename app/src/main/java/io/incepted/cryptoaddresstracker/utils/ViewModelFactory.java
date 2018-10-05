package io.incepted.cryptoaddresstracker.utils;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import io.incepted.cryptoaddresstracker.data.source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteRepository;
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
    private final AddressLocalRepository mLocalRepository;
    private final AddressRemoteRepository mRemoteRepository;

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null)
                    INSTANCE = new ViewModelFactory(application,
                            LocalAddressRepositoryInjection.provideAddressRepository(application.getApplicationContext()),
                            RemoteAddressRepositoryInjection.provideAddressRepository());

            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(Application application, AddressLocalRepository localRepository, AddressRemoteRepository remoteRepository) {
        mApplication = application;
        mLocalRepository = localRepository;
        mRemoteRepository = remoteRepository;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            //noinspection unchecked
            return (T) new MainViewModel(mApplication, mLocalRepository, mRemoteRepository);
        } else if (modelClass.isAssignableFrom(NewAddressViewModel.class)) {
            //noinspection unchecked
            return (T) new NewAddressViewModel(mApplication, mLocalRepository, mRemoteRepository);
        } else if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            //noinspection unchecked
            return (T) new DetailViewModel(mApplication, mLocalRepository, mRemoteRepository);
        } else if (modelClass.isAssignableFrom(TxViewModel.class)) {
            //noinspection unchecked
            return (T) new TxViewModel(mApplication, mLocalRepository, mRemoteRepository);
        } else if (modelClass.isAssignableFrom(TxDetailViewModel.class)) {
            //noinspection unchecked
            return (T) new TxDetailViewModel(mApplication, mLocalRepository, mRemoteRepository);
        } else if (modelClass.isAssignableFrom(TokenTransferViewModel.class)) {
            //noinspection unchecked
            return (T) new TokenTransferViewModel(mApplication, mLocalRepository, mRemoteRepository);
        } else if (modelClass.isAssignableFrom(TxScanViewModel.class)) {
            //noinspection unchecked
            return (T) new TxScanViewModel(mApplication, mLocalRepository, mRemoteRepository);
        }
        return super.create(modelClass);
    }
}
