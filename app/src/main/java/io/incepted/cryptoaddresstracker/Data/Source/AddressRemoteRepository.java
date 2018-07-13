package io.incepted.cryptoaddresstracker.Data.Source;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Network.Deserializer.SimpleAddressInfoDeserializer;
import io.incepted.cryptoaddresstracker.Network.NetworkManager;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.Network.NetworkService;
import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * A utility class dedicated for executing API calls to Ethplorer.io
 */

public class AddressRemoteRepository implements AddressRemoteDataSource {

    private static final String TAG = AddressRemoteRepository.class.getSimpleName();

    private volatile static AddressRemoteRepository INSTANCE = null;

    private static NetworkService mSimpleAddressInfoService =
            NetworkManager.getCustomNetworkService(NetworkManager.BASE_URL_ETHPLORER,
                    RemoteAddressInfo.class, new SimpleAddressInfoDeserializer());

    private static NetworkService mDefaultAddressInfoService =
            NetworkManager.getDefaultNetworkService(NetworkManager.BASE_URL_ETHPLORER);

    private AddressRemoteRepository() {
    }

    public static AddressRemoteRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (AddressLocalRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AddressRemoteRepository();
                }
            }
        }
        return INSTANCE;
    }

    @SuppressLint("CheckResult")
    @Override
    public void fetchMultipleSimpleAddressInfo(@NonNull List<Address> addresses, @NonNull Scheduler bkgdScheduler,
                                               @NonNull Scheduler mainScheduler, @NonNull SimpleAddressInfoListener callback) {
        callback.onCallReady();
        Observable.fromIterable(addresses)
                .flatMap(address ->
                        Observable.zip(Observable.just(address),
                                mSimpleAddressInfoService.getSimpleAddressInfo(address.getAddrValue(),
                                        NetworkManager.API_KEY_ETHPLORER, true),
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
    @Override
    public void fetchDetailedAddressInfo(@NonNull String address, @NonNull Scheduler bkgdScheduler,
                                         @NonNull Scheduler mainScheduler, @NonNull DetailAddressInfoListener callback) {

        callback.onCallReady();

        mDefaultAddressInfoService.getDetailedAddressInfo(address, NetworkManager.API_KEY_ETHPLORER, true)
                .subscribeOn(bkgdScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onSimpleAddressInfoLoaded,
                        callback::onDataNotAvailable);
    }

    @SuppressLint("CheckResult")
    @Override
    public void fetchEthTransactionListInfo(@NonNull String address, @NonNull Scheduler bkgdScheduler,
                                            @NonNull Scheduler mainScheduler, @NonNull EthTransactionListInfoListener callback) {
        callback.onCallReady();
        mDefaultAddressInfoService.getEthTransactionListInfo(address, NetworkManager.API_KEY_ETHPLORER)
                .subscribeOn(bkgdScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onEthTransactionListInfoReady,
                        callback::onDataNotAvailable);
    }

    @SuppressLint("CheckResult")
    @Override
    public void fetchContractTokenTransactionListInfo(@NonNull String address, @NonNull Scheduler bkgdScheduler,
                                                      @NonNull Scheduler mainScheduler, @NonNull TransactionListInfoListener callback) {
        callback.onCallReady();

        mDefaultAddressInfoService.getContractTokenTransactionListInfo(address, NetworkManager.API_KEY_ETHPLORER)
                .subscribeOn(bkgdScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onTransactionListInfoLoaded,
                        callback::onDataNotAvailable);
    }

    @SuppressLint("CheckResult")
    @Override
    public void fetchTokenTransactionListInfo(@NonNull String address, @NonNull String tokenAddress,
                                              @NonNull Scheduler bkgdScheduler, @NonNull Scheduler mainScheduler,
                                              @NonNull TransactionListInfoListener callback) {
        callback.onCallReady();

        mDefaultAddressInfoService.getTokenTransactionListInfo(address, NetworkManager.API_KEY_ETHPLORER, tokenAddress)
                .subscribeOn(bkgdScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onTransactionListInfoLoaded,
                        callback::onDataNotAvailable);
    }

    @SuppressLint("CheckResult")
    @Override
    public void fetchTransactionDetail(@NonNull String txHash, @NonNull Scheduler bkgdScheduler,
                                       @NonNull Scheduler mainScheduler, @NonNull TransactionInfoListener callback) {
        callback.onCallReady();
        mDefaultAddressInfoService.getTransactionDetail(txHash, NetworkManager.API_KEY_ETHPLORER)
                .subscribeOn(bkgdScheduler)
                .observeOn(mainScheduler)
                .subscribe(callback::onTransactionDetailReady,
                        callback::onDataNotAvailable);
    }

    public static void setSimpleAddressInfoService(NetworkService mSimpleAddressInfoService) {
        AddressRemoteRepository.mSimpleAddressInfoService = mSimpleAddressInfoService;
    }

    public static void setDefaultAddressInfoService(NetworkService mDefaultAddressInfoService) {
        AddressRemoteRepository.mDefaultAddressInfoService = mDefaultAddressInfoService;
    }
}
