package io.incepted.cryptoaddresstracker.ViewModelTest;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.TestUtils;
import io.incepted.cryptoaddresstracker.ViewModels.TokenTransferViewModel;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class TokenTransferViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static final String TX_HASH_TEST = "tx_hash_test";

    @Mock
    private AddressLocalRepository mLocalRepository;

    @Mock
    private AddressRemoteRepository mRemoteRepository;

    @Mock
    private Application mContext;

    @Captor
    private ArgumentCaptor<AddressRemoteDataSource.TransactionInfoListener> mTransactionInfoCaptor;

    private TokenTransferViewModel mTxDetailViewModel;

    private TransactionInfo TX_INFO_TEST;


    @Before
    public void setupTokenTransferViewModel() {
        MockitoAnnotations.initMocks(this);

        setupContext();

        mTxDetailViewModel = new TokenTransferViewModel(mContext, mLocalRepository, mRemoteRepository);
        TX_INFO_TEST  = new TransactionInfo();
        TX_INFO_TEST.setHash(TX_HASH_TEST);
    }


    private void setupContext() {
        when(mContext.getApplicationContext()).thenReturn(mContext);
    }

    @Test
    public void loadTransactionInfo_validTx() {
        mTxDetailViewModel.loadTransactionInfo(TX_HASH_TEST);

        verify(mRemoteRepository).fetchTransactionDetail(any(), any(), any(), mTransactionInfoCaptor.capture());

        mTransactionInfoCaptor.getValue().onTransactionDetailReady(TX_INFO_TEST);

        assertFalse(mTxDetailViewModel.isLoading.get());

        assertEquals(mTxDetailViewModel.mTxInfo.get().getHash(), TX_INFO_TEST.getHash());

    }

    @Test
    public void loadTransactionInfo_onReady() {
        mTxDetailViewModel.loadTransactionInfo(TX_HASH_TEST);

        verify(mRemoteRepository).fetchTransactionDetail(any(), any(), any(), mTransactionInfoCaptor.capture());

        mTransactionInfoCaptor.getValue().onCallReady();

        assertTrue(mTxDetailViewModel.isLoading.get());
    }

    @Test
    public void loadTransactionInfo_onError() {
        Observer<Integer> observer = mock(Observer.class);
        mTxDetailViewModel.getSnackbarTextResource().observe(TestUtils.TEST_OBSERVER, observer);

        mTxDetailViewModel.loadTransactionInfo(TX_HASH_TEST);

        verify(mRemoteRepository).fetchTransactionDetail(any(), any(), any(), mTransactionInfoCaptor.capture());

        mTransactionInfoCaptor.getValue().onDataNotAvailable(new Throwable());

        verify(observer, times(2)).onChanged(any());
    }
}
