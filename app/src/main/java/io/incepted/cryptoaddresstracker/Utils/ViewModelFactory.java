package io.incepted.cryptoaddresstracker.Utils;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.ViewModels.DetailViewModel;
import io.incepted.cryptoaddresstracker.ViewModels.MainViewModel;
import io.incepted.cryptoaddresstracker.ViewModels.NewAddressViewModel;
import io.incepted.cryptoaddresstracker.ViewModels.TokenTransferViewModel;
import io.incepted.cryptoaddresstracker.ViewModels.TxDetailViewModel;
import io.incepted.cryptoaddresstracker.ViewModels.TxScanViewModel;
import io.incepted.cryptoaddresstracker.ViewModels.TxViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private static volatile ViewModelFactory INSTANCE;

    private final Application mApplication;
    private final AddressRepository mRepository;

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null)
                    INSTANCE = new ViewModelFactory(application,
                            Injection.provideAddressRepository(application.getApplicationContext()));
            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(Application application, AddressRepository repository) {
        mApplication = application;
        mRepository = repository;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            //noinspection unchecked
            return (T) new MainViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(NewAddressViewModel.class)) {
            //noinspection unchecked
            return (T) new NewAddressViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            //noinspection unchecked
            return (T) new DetailViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(TxViewModel.class)) {
            //noinspection unchecked
            return (T) new TxViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(TxDetailViewModel.class)) {
            //noinspection unchecked
            return (T) new TxDetailViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(TokenTransferViewModel.class)) {
            //noinspection unchecked
            return (T) new TokenTransferViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(TxScanViewModel.class)) {
            //noinspection unchecked
            return (T) new TxScanViewModel(mApplication, mRepository);
        }
        return super.create(modelClass);
    }
}
