package io.incepted.cryptoaddresstracker;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.TransactionListInfo;
import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class AddressRemoteRepositoryTest {

    private static final ArrayList<Address> ADDRESSES_TEST =
            Lists.newArrayList(new Address("NAME_1", "ADDR_1", null),
                    new Address("NAME_2", "ADDR_2", null),
                    new Address("NAME_3", "ADDR_3", null));

    private static final String ADDRESS_TEST = "address";
    private static final String TX_HASH_TEST = "tx_hash";
    private static final String TOKEN_ADDRESS_TEST = "token_address";

    @Mock
    private AddressRemoteDataSource.SimpleAddressInfoListener mSimpleListener;

    @Mock
    private AddressRemoteDataSource.DetailAddressInfoListener mDetailListener;

    @Mock
    private AddressRemoteDataSource.EthTransactionListInfoListener mEthTxListInfoListener;

    @Mock
    private AddressRemoteDataSource.TransactionListInfoListener mTxListInfoListener;

    @Mock
    private AddressRemoteDataSource.TransactionInfoListener mTxInfoListener;

    @Mock
    private NetworkService mNetworkService;

    private AddressRemoteRepository mRemoteRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mRemoteRepository = AddressRemoteRepository.getInstance();
    }

    @Test
    public void getMultipleSimpleAddressInfo_onSuccess() {
        when(mNetworkService.getSimpleAddressInfo(any(), any(), anyBoolean()))
                .thenReturn(Observable.just(new RemoteAddressInfo()));

        mRemoteRepository.setSimpleAddressInfoService(mNetworkService);

        mRemoteRepository.fetchMultipleSimpleAddressInfo(ADDRESSES_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mSimpleListener);

        verify(mSimpleListener).onCallReady();
        verify(mSimpleListener, times(3)).onNextSimpleAddressInfoLoaded(any());
        verify(mSimpleListener).onSimpleAddressInfoLoadingCompleted();
    }

    @Test
    public void getMultipleSimpleAddressInfo_onError() {
        Exception exception = new Exception();

        when(mNetworkService.getSimpleAddressInfo(any(), any(), anyBoolean()))
                .thenReturn(Observable.error(exception));

        mRemoteRepository.setSimpleAddressInfoService(mNetworkService);

        mRemoteRepository.fetchMultipleSimpleAddressInfo(ADDRESSES_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mSimpleListener);

        verify(mSimpleListener).onDataNotAvailable(exception);
    }

    @Test
    public void getDetailedAddressInfo_onSuccess() {
        when(mNetworkService.getDetailedAddressInfo(any(), any(), anyBoolean()))
                .thenReturn(Single.just(new RemoteAddressInfo()));

        mRemoteRepository.setDefaultAddressInfoService(mNetworkService);

        mRemoteRepository.fetchDetailedAddressInfo(ADDRESS_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mDetailListener);

        verify(mDetailListener).onCallReady();
        verify(mDetailListener).onSimpleAddressInfoLoaded(any());
    }

    @Test
    public void getDetailedAddressInfo_onError() {
        Exception exception = new Exception();

        when(mNetworkService.getDetailedAddressInfo(any(), any(), anyBoolean()))
                .thenReturn(Single.error(exception));

        mRemoteRepository.setDefaultAddressInfoService(mNetworkService);

        mRemoteRepository.fetchDetailedAddressInfo(ADDRESS_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mDetailListener);

        verify(mDetailListener).onDataNotAvailable(exception);
    }

    @Test
    public void getEthTransactionListInfo_onSuccess() {
        when(mNetworkService.getEthTransactionListInfo(any(), any()))
                .thenReturn(Single.just(new ArrayList<>()));

        mRemoteRepository.setDefaultAddressInfoService(mNetworkService);

        mRemoteRepository.fetchEthTransactionListInfo(ADDRESS_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mEthTxListInfoListener);

        verify(mEthTxListInfoListener).onCallReady();
        verify(mEthTxListInfoListener).onEthTransactionListInfoReady(any());
    }

    @Test
    public void getEthTransactionListInfo_onError() {
        Exception exception = new Exception();

        when(mNetworkService.getEthTransactionListInfo(any(), any()))
                .thenReturn(Single.error(exception));

        mRemoteRepository.setDefaultAddressInfoService(mNetworkService);

        mRemoteRepository.fetchEthTransactionListInfo(ADDRESS_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mEthTxListInfoListener);

        verify(mEthTxListInfoListener).onDataNotAvailable(exception);
    }


    @Test
    public void getContractTokenTransactionListInfo_onSuccess() {
        when(mNetworkService.getContractTokenTransactionListInfo(any(), any()))
                .thenReturn(Single.just(new TransactionListInfo()));

        mRemoteRepository.setDefaultAddressInfoService(mNetworkService);

        mRemoteRepository.fetchContractTokenTransactionListInfo(ADDRESS_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mTxListInfoListener);

        verify(mTxListInfoListener).onCallReady();
        verify(mTxListInfoListener).onTransactionListInfoLoaded(any());
    }

    @Test
    public void getContractTokenTransactionListInfo_onError() {
        Exception exception = new Exception();

        when(mNetworkService.getContractTokenTransactionListInfo(any(), any()))
                .thenReturn(Single.error(exception));

        mRemoteRepository.setDefaultAddressInfoService(mNetworkService);

        mRemoteRepository.fetchContractTokenTransactionListInfo(ADDRESS_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mTxListInfoListener);

        verify(mTxListInfoListener).onDataNotAvailable(exception);
    }

    @Test
    public void getTokenTransactionListInfo_onSuccess() {
        when(mNetworkService.getTokenTransactionListInfo(any(), any(), any()))
                .thenReturn(Single.just(new TransactionListInfo()));

        mRemoteRepository.setDefaultAddressInfoService(mNetworkService);

        mRemoteRepository.fetchTokenTransactionListInfo(ADDRESS_TEST,
                TOKEN_ADDRESS_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mTxListInfoListener);

        verify(mTxListInfoListener).onCallReady();
        verify(mTxListInfoListener).onTransactionListInfoLoaded(any());
    }

    @Test
    public void getTokenTransactionListInfo_onError() {
        Exception exception = new Exception();

        when(mNetworkService.getTokenTransactionListInfo(any(), any(), any()))
                .thenReturn(Single.error(exception));

        mRemoteRepository.setDefaultAddressInfoService(mNetworkService);

        mRemoteRepository.fetchTokenTransactionListInfo(ADDRESS_TEST,
                TOKEN_ADDRESS_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mTxListInfoListener);

        verify(mTxListInfoListener).onDataNotAvailable(exception);
    }

    @Test
    public void getTransactionDetail_onSuccess() {
        when(mNetworkService.getTransactionDetail(any(), any()))
                .thenReturn(Single.just(new TransactionInfo()));

        mRemoteRepository.setDefaultAddressInfoService(mNetworkService);

        mRemoteRepository.fetchTransactionDetail(ADDRESS_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mTxInfoListener);

        verify(mTxInfoListener).onCallReady();
        verify(mTxInfoListener).onTransactionDetailReady(any());
    }

    @Test
    public void getTransactionDetail_onError() {
        Exception exception = new Exception();

        when(mNetworkService.getTransactionDetail(any(), any()))
                .thenReturn(Single.error(exception));

        mRemoteRepository.setDefaultAddressInfoService(mNetworkService);

        mRemoteRepository.fetchTransactionDetail(ADDRESS_TEST,
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mTxInfoListener);

        verify(mTxInfoListener).onDataNotAvailable(exception);
    }
}
