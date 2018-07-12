package io.incepted.cryptoaddresstracker.Utils;

import android.content.Context;
import android.support.annotation.NonNull;

import io.incepted.cryptoaddresstracker.Data.DBCompat.AppDatabase;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {

    public static AddressRepository provideAddressRepository(@NonNull Context context) {
        checkNotNull(context);
        return AddressRepository.getInstance(AppDatabase.getAppDatabase(context).addressDao());
    }
}
