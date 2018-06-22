package io.incepted.cryptoaddresstracker.Data.Source;

import android.support.annotation.NonNull;

import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;

public interface AddressDataSource {

    interface OnAddressesLoadedListener {
        void onAddressesLoaded(List<Address> addresses);

        void onDataNotAvailable();
    }

    interface OnAddressLoadedListener {
        void onAddressLoaded(Address addresses);

        void onDataNotAvailable();
    }

    interface OnAddressSavedListener {
        void onAddressSaved();

        void onDataNotAvailable();
    }

    interface OnAddressDeletedListener {
        void onAddressDeleted();

        void onDataNotAvailable();
    }

    interface OnAddressCountListener {
        void onAddressCount(Integer addressCount);

        void onDataNotAvailable();
    }

    interface OnAddressUpdatedListener {
        void onAddressUpdated();

        void onDataNotAvailable();
    }


    void getAddressCount(@NonNull OnAddressCountListener callback);

    void getAddresses(@NonNull OnAddressesLoadedListener callback);

    void getAddress(@NonNull int addressId, @NonNull OnAddressLoadedListener callback);

    void saveAddress(@NonNull Address address, @NonNull OnAddressSavedListener callback);

    void updateAddress(@NonNull Address address, @NonNull OnAddressUpdatedListener callback);

    void deleteAddress(@NonNull int addressId, @NonNull OnAddressDeletedListener callback);

    void refreshAddresses();


}
