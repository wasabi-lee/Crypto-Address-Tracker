package io.incepted.cryptoaddresstracker.Data.Source;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Map;

import io.incepted.cryptoaddresstracker.Data.DBCompat.AddressDao;
import io.incepted.cryptoaddresstracker.Data.DBCompat.AppDatabase;
import io.incepted.cryptoaddresstracker.Data.Model.Address;

public class AddressRepository implements AddressDataSource {


    private static final String TAG = AddressRepository.class.getSimpleName();

    private volatile static AddressRepository INSTANCE = null;
    private AddressDao mAddressDao;

    Map<Integer, Address> mCachedAddresses;
    private boolean mCacheIsDirty = false;


    private AddressRepository(Context context) {
        mAddressDao = AppDatabase.getAppDatabase(context).addressDao();
    }

    public static AddressRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AddressRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AddressRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getAddressCount(@NonNull OnAddressCountListener callback) {
        try {
            callback.onAddressCount(mAddressDao.getAddressCount());
        } catch (Exception e) {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getAddresses(@NonNull OnAddressesLoadedListener callback) {
        try {
            callback.onAddressesLoaded(mAddressDao.getAllAddresses());
        } catch (Exception e) {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getAddress(@NonNull int addressId, @NonNull OnAddressLoadedListener callback) {
        try {
            callback.onAddressLoaded(mAddressDao.getAddressById(addressId));
        } catch (Exception e) {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveAddress(@NonNull Address address, @NonNull OnAddressSavedListener callback) {
        try {
            mAddressDao.insert(address);
            callback.onAddressSaved();
        } catch (Exception e) {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void updateAddress(@NonNull Address address, @NonNull OnAddressUpdatedListener callback) {
        try {
            mAddressDao.update(address);
            callback.onAddressUpdated();
        } catch (Exception e) {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void deleteAddress(@NonNull int addressId, @NonNull OnAddressDeletedListener callback) {
        mAddressDao.delete(addressId);
        try {
            callback.onAddressDeleted();
        } catch (Exception e) {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void refreshAddresses() {

    }
}
