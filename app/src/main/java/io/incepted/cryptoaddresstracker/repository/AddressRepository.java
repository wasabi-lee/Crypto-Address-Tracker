package io.incepted.cryptoaddresstracker.repository;

import java.util.List;

import androidx.annotation.NonNull;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressLocalCallbacks;
import io.incepted.cryptoaddresstracker.data.source.address.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressRemoteCallbacks;
import io.incepted.cryptoaddresstracker.data.source.address.AddressRemoteDataSource;

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

    public void fetchMultipleSimpleAddressInfo(@NonNull List<Address> addresses,
                                               @NonNull AddressRemoteCallbacks.SimpleAddressInfoListener callback) {
        mRemoteDataSource.fetchMultipleSimpleAddressInfo(addresses, callback);
    }


    public void fetchDetailedAddressInfo(@NonNull String address,
                                         @NonNull AddressRemoteCallbacks.DetailAddressInfoListener callback) {
        mRemoteDataSource.fetchDetailedAddressInfo(address, callback);
    }
}
