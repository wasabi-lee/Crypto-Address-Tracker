package io.incepted.cryptoaddresstracker.data.source;

import android.annotation.SuppressLint;

import java.util.List;

import androidx.annotation.NonNull;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.network.ApiManager;
import io.incepted.cryptoaddresstracker.network.NetworkManager;
import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleAddressInfoDeserializer;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;
import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * A utility class dedicated for executing API calls to Ethplorer.io
 */

public class AddressRemoteRepository {

    private static final String TAG = AddressRemoteRepository.class.getSimpleName();

    private volatile static AddressRemoteRepository INSTANCE = null;

    private NetworkService mSimpleAddressInfoService =
            NetworkManager.getCustomNetworkService(NetworkManager.BASE_URL_ETHPLORER,
                    RemoteAddressInfo.class, new SimpleAddressInfoDeserializer());

    private NetworkService mDefaultAddressInfoService =
            NetworkManager.getDefaultNetworkService(NetworkManager.BASE_URL_ETHPLORER);

    private AddressRemoteRepository() {
    }

    public static AddressRemoteRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (AddressRemoteRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AddressRemoteRepository();
                }
            }
        }
        return INSTANCE;
    }

    @SuppressLint("CheckResult")
    public void fetchMultipleSimpleAddressInfo(@NonNull List<Address> addresses,
                                               @NonNull Scheduler bkgdScheduler,
                                               @NonNull Scheduler mainScheduler,
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
                .subscribeOn(bkgdScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onNextSimpleAddressInfoLoaded,
                        callback::onDataNotAvailable,
                        callback::onSimpleAddressInfoLoadingCompleted);
    }


    @SuppressLint("CheckResult")
    public void fetchDetailedAddressInfo(@NonNull String address, @NonNull Scheduler bkgdScheduler,
                                         @NonNull Scheduler mainScheduler,
                                         @NonNull AddressRemoteCallbacks.DetailAddressInfoListener callback) {

    }

    @SuppressLint("CheckResult")
    public void fetchEthTransactionListInfo(@NonNull String address, @NonNull Scheduler bkgdScheduler,
                                            @NonNull Scheduler mainScheduler,
                                            @NonNull AddressRemoteCallbacks.EthTransactionListInfoListener callback) {
        callback.onCallReady();
        mDefaultAddressInfoService
                .getEthTransactionListInfo(address, ApiManager.API_KEY_ETHPLORER)
                .subscribeOn(bkgdScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onEthTransactionListInfoReady,
                        callback::onDataNotAvailable);
    }

    @SuppressLint("CheckResult")
    public void fetchContractTokenTransactionListInfo(@NonNull String address,
                                                      @NonNull Scheduler bkgdScheduler,
                                                      @NonNull Scheduler mainScheduler,
                                                      @NonNull AddressRemoteCallbacks.TransactionListInfoListener callback) {
        callback.onCallReady();

        mDefaultAddressInfoService
                .getContractTokenTransactionListInfo(address, ApiManager.API_KEY_ETHPLORER)
                .subscribeOn(bkgdScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onTransactionListInfoLoaded,
                        callback::onDataNotAvailable);
    }

    @SuppressLint("CheckResult")
    public void fetchTokenTransactionListInfo(@NonNull String address, @NonNull String tokenAddress,
                                              @NonNull Scheduler bkgdScheduler,
                                              @NonNull Scheduler mainScheduler,
                                              @NonNull AddressRemoteCallbacks.TransactionListInfoListener callback) {
        callback.onCallReady();

        mDefaultAddressInfoService
                .getTokenTransactionListInfo(address, ApiManager.API_KEY_ETHPLORER, tokenAddress)
                .subscribeOn(bkgdScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onTransactionListInfoLoaded,
                        callback::onDataNotAvailable);
    }

    @SuppressLint("CheckResult")
    public void fetchTransactionDetail(@NonNull String txHash, @NonNull Scheduler bkgdScheduler,
                                       @NonNull Scheduler mainScheduler,
                                       @NonNull AddressRemoteCallbacks.TransactionInfoListener callback) {
        callback.onCallReady();
        mDefaultAddressInfoService
                .getTransactionDetail(txHash, ApiManager.API_KEY_ETHPLORER)
                .subscribeOn(bkgdScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onTransactionDetailReady,
                        callback::onDataNotAvailable);
    }

    public void setSimpleAddressInfoService(NetworkService mSimpleAddressInfoService) {
        this.mSimpleAddressInfoService = mSimpleAddressInfoService;
    }

    public void setDefaultAddressInfoService(NetworkService mDefaultAddressInfoService) {
        this.mDefaultAddressInfoService = mDefaultAddressInfoService;
    }
}
