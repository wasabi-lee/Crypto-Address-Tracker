package io.incepted.cryptoaddresstracker.data.source;

import androidx.annotation.NonNull;

import java.util.List;

import io.incepted.cryptoaddresstracker.data.model.Address;

public interface AddressLocalDataSource {

    interface OnAddressesLoadedListener {
        void onAddressesLoaded(List<Address> addresses);

        void onAddressesNotAvailable();
    }

    interface OnAddressLoadedListener {
        void onAddressLoaded(Address address);

        void onAddressNotAvailable();
    }

    interface OnAddressSavedListener {
        void onAddressSaved();

        void onSaveNotAvailable();
    }

    interface OnAddressDeletedListener {
        void onAddressDeleted();

        void onDeletionNotAvailable();
    }

    interface OnAllAddressDeletedListener {
        void onAddressesDeleted();

        void onDeletionNotAvailable();
    }

    interface OnAddressCountListener {
        void onAddressCount(Integer addressCount);

        void onAddressCountNotAvailable();
    }

    interface OnAddressUpdatedListener {
        void onAddressUpdated();

        void onUpdateNotAvailable();
    }


    void getAddressCount(@NonNull OnAddressCountListener callback);

    void getAddresses(@NonNull OnAddressesLoadedListener callback);

    void getAddress(@NonNull int addressId, @NonNull OnAddressLoadedListener callback);

    void saveAddress(@NonNull Address address, @NonNull OnAddressSavedListener callback);

    void updateAddress(@NonNull Address address, @NonNull OnAddressUpdatedListener callback);

    void deleteAddress(@NonNull int addressId, @NonNull OnAddressDeletedListener callback);

    void deleteAllAddresses(@NonNull OnAllAddressDeletedListener callback);

    void refreshAddresses();


}