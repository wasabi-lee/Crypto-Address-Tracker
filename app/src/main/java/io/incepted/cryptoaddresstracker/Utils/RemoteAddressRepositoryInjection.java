package io.incepted.cryptoaddresstracker.Utils;

import androidx.annotation.NonNull;

import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteRepository;

public class RemoteAddressRepositoryInjection {
    public static AddressRemoteRepository provideAddressRepository() {
    return AddressRemoteRepository.getInstance();
}
}
