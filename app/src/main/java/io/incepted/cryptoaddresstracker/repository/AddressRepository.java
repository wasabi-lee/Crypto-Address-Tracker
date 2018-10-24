package io.incepted.cryptoaddresstracker.repository;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import io.incepted.cryptoaddresstracker.data.dbCompat.AddressDao;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.AddressLocalCallbacks;
import io.incepted.cryptoaddresstracker.data.source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteDataSource;

public class AddressRepository {

    private volatile static AddressRepository INSTANCE = null;

    private final AddressLocalDataSource mLocalDataSource;
    private final AddressRemoteDataSource mRemoteDataSource;

    public AddressRepository(AddressLocalDataSource localDataSource,
                             AddressRemoteDataSource remoteDataSource) {
        this.mLocalDataSource = localDataSource;
        this.mRemoteDataSource = remoteDataSource;
    }

    public static AddressRepository getInstance(AddressLocalDataSource localDataSource,
                                                AddressRemoteDataSource remoteDataSource) {
        if (INSTANCE == null) {
            synchronized (AddressRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AddressRepository(localDataSource, remoteDataSource);
                }
            }
        }
        return INSTANCE;
    }


    public void saveAddress(@NonNull Address address, @NonNull AddressLocalCallbacks.OnAddressSavedListener callback) {
        mLocalDataSource.saveAddress(address, callback);
    }

    public void getAddresses(@NonNull AddressLocalCallbacks.OnAddressesLoadedListener callback) {
        mLocalDataSource.getAddresses(callback);
    }

    public void getAddress(@NonNull int addressId, @NonNull AddressLocalCallbacks.OnAddressLoadedListener callback) {
        mLocalDataSource.getAddress(addressId, callback);
    }

    public void getAddressCount(@NonNull AddressLocalCallbacks.OnAddressCountListener callback) {
        mLocalDataSource.getAddressCount(callback);
    }

    public void updateAddress(@NonNull Address address, @NonNull AddressLocalCallbacks.OnAddressUpdatedListener callback) {
        mLocalDataSource.updateAddress(address, callback);
    }

    public void deleteAddress(@NonNull int addressId, @NonNull AddressLocalCallbacks.OnAddressDeletedListener callback) {
        mLocalDataSource.deleteAddress(addressId, callback);
    }

    public void deleteAllAddresses(@NonNull AddressLocalCallbacks.OnAllAddressDeletedListener callback) {
        mLocalDataSource.deleteAllAddresses(callback);
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
