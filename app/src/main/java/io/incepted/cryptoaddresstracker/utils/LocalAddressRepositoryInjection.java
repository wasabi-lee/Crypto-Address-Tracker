package io.incepted.cryptoaddresstracker.utils;

import android.content.Context;
import androidx.annotation.NonNull;

import io.incepted.cryptoaddresstracker.data.dbCompat.AppDatabase;
import io.incepted.cryptoaddresstracker.data.source.AddressLocalRepository;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocalAddressRepositoryInjection {

    public static AddressLocalRepository provideAddressRepository(@NonNull Context context) {
        checkNotNull(context);
        return AddressLocalRepository.getInstance(AppDatabase.getAppDatabase(context).addressDao());
    }
}
