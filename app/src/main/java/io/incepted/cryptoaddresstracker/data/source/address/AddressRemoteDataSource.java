package io.incepted.cryptoaddresstracker.data.source.address;

import android.annotation.SuppressLint;

import java.util.List;

import androidx.annotation.NonNull;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressRemoteCallbacks;
import io.incepted.cryptoaddresstracker.network.ApiManager;
import io.incepted.cryptoaddresstracker.network.NetworkManager;
import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleAddressInfoDeserializer;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddressRemoteDataSource {

    private static volatile AddressRemoteDataSource INSTANCE;

    private Scheduler backgroundScheduler = Schedulers.io();

    private Scheduler mainScheduler = AndroidSchedulers.mainThread();


    private NetworkService mSimpleAddressInfoService =
            NetworkManager.getCustomNetworkService(NetworkManager.BASE_URL_ETHPLORER,
                    RemoteAddressInfo.class, new SimpleAddressInfoDeserializer());

    private NetworkService mDefaultAddressInfoService =
            NetworkManager.getDefaultNetworkService(NetworkManager.BASE_URL_ETHPLORER);


    private AddressRemoteDataSource() {
    }


    public static AddressRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (AddressLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AddressRemoteDataSource();
                }
            }
        }
        return INSTANCE;
    }


    @SuppressLint("CheckResult")
    public void fetchMultipleSimpleAddressInfo(@NonNull List<Address> addresses,
                                               @NonNull AddressRemoteCallbacks.SimpleAddressInfoListener callback) {
        callback.onCallReady();
        Observable.fromIterable(addresses)
                .flatMap(address ->
                        Observable.zip(Observable.just(address),
                                mSimpleAddressInfoService.getSimpleAddressInfo(address.getAddrValue(),
                                        ApiManager.API_KEY_ETHPLORER, true),
                                (emittedAddress, simpleAddressInfo) -> {
                                    emittedAddress.setRemoteAddressInfo(simpleAddressInfo);
                                    return emittedAddress;
                                }))
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onNextSimpleAddressInfoLoaded, // onNext
                        callback::onDataNotAvailable,
                        callback::onSimpleAddressInfoLoadingCompleted);
    }

    @SuppressLint("CheckResult")
    public void fetchDetailedAddressInfo(@NonNull String address,
                                         @NonNull AddressRemoteCallbacks.DetailAddressInfoListener callback) {

        callback.onCallReady();
        mDefaultAddressInfoService
                .getDetailedAddressInfo(address, ApiManager.API_KEY_ETHPLORER, true)
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onRemoteAddressInfoLoaded,
                        callback::onDataNotAvailable);
    }


}
