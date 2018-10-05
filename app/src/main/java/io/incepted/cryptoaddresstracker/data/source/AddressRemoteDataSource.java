package io.incepted.cryptoaddresstracker.data.source;

import androidx.annotation.NonNull;

import java.util.List;

import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.TransactionListInfo;
import io.reactivex.Scheduler;

public interface AddressRemoteDataSource {

    interface SimpleAddressInfoListener {
        void onCallReady();

        void onNextSimpleAddressInfoLoaded(Address addressInfo);

        void onSimpleAddressInfoLoadingCompleted();

        void onDataNotAvailable(Throwable throwable);
    }

    interface DetailAddressInfoListener {
        void onCallReady();

        void onSimpleAddressInfoLoaded(RemoteAddressInfo remoteAddressInfo);

        void onDataNotAvailable(Throwable throwable);
    }

    interface TransactionListInfoListener {
        void onCallReady();

        void onTransactionListInfoLoaded(TransactionListInfo transactionListInfo);

        void onDataNotAvailable(Throwable throwable);
    }

    interface EthTransactionListInfoListener {
        void onCallReady();

        void onEthTransactionListInfoReady(List<EthOperation> ethOperationList);

        void onDataNotAvailable(Throwable throwable);
    }

    interface TransactionInfoListener {
        void onCallReady();

        void onTransactionDetailReady(TransactionInfo transactionDetail);

        void onDataNotAvailable(Throwable throwable);
    }

    void fetchMultipleSimpleAddressInfo(@NonNull List<Address> addresses,
                                        @NonNull Scheduler bkgdScheduler, @NonNull Scheduler mainScheduler,
                                        @NonNull SimpleAddressInfoListener callback);

    void fetchDetailedAddressInfo(@NonNull String address,
                                  @NonNull Scheduler bkgdScheduler,
                                  @NonNull Scheduler mainScheduler,
                                  @NonNull AddressRemoteDataSource.DetailAddressInfoListener callback);

    void fetchEthTransactionListInfo(@NonNull String address,
                                     @NonNull Scheduler bkgdScheduler,
                                     @NonNull  Scheduler mainScheduler,
                                     @NonNull AddressRemoteDataSource.EthTransactionListInfoListener callback);

    void fetchContractTokenTransactionListInfo(@NonNull String address,
                                               @NonNull Scheduler bkgdScheduler,
                                               @NonNull Scheduler mainScheduler,
                                               @NonNull AddressRemoteDataSource.TransactionListInfoListener callback);

    void fetchTokenTransactionListInfo(@NonNull String address,
                                       @NonNull  String tokenAddress,
                                       @NonNull  Scheduler bkgdScheduler,
                                       @NonNull  Scheduler mainScheduler,
                                       @NonNull  AddressRemoteDataSource.TransactionListInfoListener callback);

    void fetchTransactionDetail(@NonNull String txHash,
                                @NonNull Scheduler bkgdScheduler,
                                @NonNull Scheduler mainScheduler,
                                @NonNull AddressRemoteDataSource.TransactionInfoListener callback);

}
