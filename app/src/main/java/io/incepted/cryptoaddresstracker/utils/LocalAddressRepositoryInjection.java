package io.incepted.cryptoaddresstracker.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import io.incepted.cryptoaddresstracker.data.dbCompat.AppDatabase;
import io.incepted.cryptoaddresstracker.data.source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocalAddressRepositoryInjection {

    public static AddressLocalDataSource provideLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        return AddressLocalDataSource.getInstance(AppDatabase.getAppDatabase(context).addressDao());
    }

    public static AddressRemoteDataSource provideRemoteDataSource() {
        return AddressRemoteDataSource.getInstance();
    }

    public static AddressRepository provideAddressRepository(@NonNull Context context) {
        checkNotNull(context);
        return AddressRepository.getInstance(provideLocalDataSource(context),
                provideRemoteDataSource());
    }
}
