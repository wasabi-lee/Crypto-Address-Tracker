package io.incepted.cryptoaddresstracker.data.source.callbacks;

import java.util.List;

import io.incepted.cryptoaddresstracker.data.model.Address;

public interface AddressLocalCallbacks {
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

}
