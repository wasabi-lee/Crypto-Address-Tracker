package io.incepted.cryptoaddresstracker.ViewModelTest;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.TokenOperation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.TransactionListInfo;
import io.incepted.cryptoaddresstracker.TestUtils;
import io.incepted.cryptoaddresstracker.ViewModels.TxViewModel;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class TxViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static final int ID_TEST = 0;
    private static final String NAME_TEST = "name";
    private static final String ADDR_VALUE_TEST = "addr_value";

    @Mock
    private AddressLocalRepository mLocalRepository;

    @Mock
    private AddressRemoteRepository mRemoteRepository;

    @Mock
    private Application mContext;

    @Captor
    private ArgumentCaptor<AddressLocalDataSource.OnAddressLoadedListener> mAddressLoadCallbackCaptor;

    @Captor
    private ArgumentCaptor<AddressRemoteDataSource.EthTransactionListInfoListener> mEthTxListInfoCaptor;

    @Captor
    private ArgumentCaptor<AddressRemoteDataSource.TransactionListInfoListener> mTxListInfoCaptor;

    private TxViewModel mTxViewModel;

    private Address mAddress;

    private List<EthOperation> ETH_TX_LIST_INFO_TEST;

    private TransactionListInfo TX_LIST_INFO_TEST;

    @Before
    public void setupTxViewModel() {
        MockitoAnnotations.initMocks(this);

        setupContext();

        mTxViewModel = new TxViewModel(mContext, mLocalRepository, mRemoteRepository);
        mAddress = new Address(NAME_TEST, ADDR_VALUE_TEST, new Date());
        mAddress.set_id(ID_TEST);

        // Setting up expected test results
        ETH_TX_LIST_INFO_TEST = Lists.newArrayList(new EthOperation(), new EthOperation(), new EthOperation());
        TX_LIST_INFO_TEST = new TransactionListInfo();
        TX_LIST_INFO_TEST.setOperations(Lists.newArrayList(new TokenOperation(), new TokenOperation(), new TokenOperation()));
    }

    private void setupContext() {
        when(mContext.getApplicationContext()).thenReturn(mContext);
    }

    @Test
    public void loadAddressTest() {
        // Trigger address loading
        setupViewModelRepositoryCallback();

        assertEquals(mTxViewModel.mAddress.getValue().getName(), mAddress.getName());
        assertEquals(mTxViewModel.getmAddress().getValue().getAddrValue(), mAddress.getAddrValue());

    }

    @Test
    public void clickedAddress_toTxDetailActivity() {

        Observer<String> observer = mock(Observer.class);

        mTxViewModel.getOpenTxDetail().observe(TestUtils.TEST_OBSERVER, observer);

        // When adding a new address
        mTxViewModel.toTxDetailActivity("0x123");

        verify(observer).onChanged("0x123");
    }


    @Test
    public void loadTransactions_FetchEthTx_onSuccess() {
        mTxViewModel.setFetchEthTx(true);

        setupViewModelRepositoryCallback();

        verify(mRemoteRepository).fetchEthTransactionListInfo(any(), any(), any(), mEthTxListInfoCaptor.capture());

        mEthTxListInfoCaptor.getValue().onEthTransactionListInfoReady(ETH_TX_LIST_INFO_TEST);

        assertFalse(mTxViewModel.isLoading.get());
        assertEquals(mTxViewModel.mTxOperations.size(), ETH_TX_LIST_INFO_TEST.size());
    }

    @Test
    public void loadTransactions_FetchEthTx_onError() {
        mTxViewModel.setFetchEthTx(true);

        Observer<Integer> observer = mock(Observer.class);
        mTxViewModel.getSnackbarTextResource().observe(TestUtils.TEST_OBSERVER, observer);

        setupViewModelRepositoryCallback();

        verify(mRemoteRepository).fetchEthTransactionListInfo(any(), any(), any(), mEthTxListInfoCaptor.capture());

        mEthTxListInfoCaptor.getValue().onDataNotAvailable(new Throwable());

        assertFalse(mTxViewModel.isLoading.get());
        assertTrue(mTxViewModel.mTxOperations.isEmpty());

        verify(observer, times(2)).onChanged(any());

    }


    @Test
    public void loadTransactions_FetchTokenTx_onSuccess() {
        mTxViewModel.setFetchEthTx(false);
        mTxViewModel.setContractAddress(false);

        setupViewModelRepositoryCallback();

        verify(mRemoteRepository).fetchTokenTransactionListInfo(any(), any(), any(), any(), mTxListInfoCaptor.capture());

        mTxListInfoCaptor.getValue().onTransactionListInfoLoaded(TX_LIST_INFO_TEST);

        assertFalse(mTxViewModel.isLoading.get());
        assertEquals(mTxViewModel.mTxOperations.size(), TX_LIST_INFO_TEST.getOperations().size());
    }

    @Test
    public void loadTransactions_FetchTokenTx_onError() {
        mTxViewModel.setFetchEthTx(false);
        mTxViewModel.setContractAddress(false);

        Observer<Integer> observer = mock(Observer.class);
        mTxViewModel.getSnackbarTextResource().observe(TestUtils.TEST_OBSERVER, observer);

        setupViewModelRepositoryCallback();

        verify(mRemoteRepository).fetchTokenTransactionListInfo(any(), any(), any(), any(), mTxListInfoCaptor.capture());

        mTxListInfoCaptor.getValue().onDataNotAvailable(new Throwable());

        assertFalse(mTxViewModel.isLoading.get());
        assertTrue(mTxViewModel.mTxOperations.isEmpty());

        verify(observer, times(2)).onChanged(any());

    }


    @Test
    public void loadTransactions_FetchContractTx_onSuccess() {
        mTxViewModel.setFetchEthTx(false);
        mTxViewModel.setContractAddress(true);

        setupViewModelRepositoryCallback();

        verify(mRemoteRepository).fetchContractTokenTransactionListInfo(any(), any(), any(), mTxListInfoCaptor.capture());

        mTxListInfoCaptor.getValue().onTransactionListInfoLoaded(TX_LIST_INFO_TEST);

        assertFalse(mTxViewModel.isLoading.get());
        assertEquals(mTxViewModel.mTxOperations.size(), TX_LIST_INFO_TEST.getOperations().size());
    }

    @Test
    public void loadTransactions_FetchContractTx_onError() {
        mTxViewModel.setFetchEthTx(false);
        mTxViewModel.setContractAddress(true);

        Observer<Integer> observer = mock(Observer.class);
        mTxViewModel.getSnackbarTextResource().observe(TestUtils.TEST_OBSERVER, observer);

        setupViewModelRepositoryCallback();

        verify(mRemoteRepository).fetchContractTokenTransactionListInfo(any(), any(), any(), mTxListInfoCaptor.capture());

        mTxListInfoCaptor.getValue().onDataNotAvailable(new Throwable());

        assertFalse(mTxViewModel.isLoading.get());
        assertTrue(mTxViewModel.mTxOperations.isEmpty());

        verify(observer, times(2)).onChanged(any());

    }




    private void setupViewModelRepositoryCallback() {
        // Load address
        mTxViewModel.loadAddress(mAddress.get_id());

        assertTrue(mTxViewModel.isLoading.get());

        // Is the repository call triggered
        verify(mLocalRepository).getAddress(eq(mAddress.get_id()), mAddressLoadCallbackCaptor.capture());

        // Trigger callback
        mAddressLoadCallbackCaptor.getValue().onAddressLoaded(mAddress);

    }


}
