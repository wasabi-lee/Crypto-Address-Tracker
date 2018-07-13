package io.incepted.cryptoaddresstracker.Utils;

import android.content.Context;
import android.support.annotation.NonNull;

import io.incepted.cryptoaddresstracker.Data.DBCompat.AppDatabase;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocalAddressRepositoryInjection {

    public static AddressLocalRepository provideAddressRepository(@NonNull Context context) {
        checkNotNull(context);
        return AddressLocalRepository.getInstance(AppDatabase.getAppDatabase(context).addressDao());
    }
}
