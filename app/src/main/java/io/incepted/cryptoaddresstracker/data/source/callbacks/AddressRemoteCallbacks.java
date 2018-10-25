package io.incepted.cryptoaddresstracker.data.source.callbacks;

import java.util.List;

import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.TransactionListInfo;

public interface AddressRemoteCallbacks {
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



}
