package io.incepted.cryptoaddresstracker.utils;

import io.incepted.cryptoaddresstracker.data.source.AddressRemoteRepository;

public class RemoteAddressRepositoryInjection {
    public static AddressRemoteRepository provideAddressRepository() {
    return AddressRemoteRepository.getInstance();
}
}
