package io.incepted.cryptoaddresstracker.Utils;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import io.incepted.cryptoaddresstracker.Activities.MainActivity;
import io.incepted.cryptoaddresstracker.ViewModels.MainViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private static volatile ViewModelFactory INSTANCE;

    private final Application mApplication;

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null)
                    INSTANCE = new ViewModelFactory(application);
            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(Application application) {
        mApplication = application;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivity.class)) {
            //noinspection unchecked
            return (T) new MainViewModel(mApplication);
        }
        return super.create(modelClass);
    }
}
