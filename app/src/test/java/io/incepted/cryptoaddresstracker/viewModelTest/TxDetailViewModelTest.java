package io.incepted.cryptoaddresstracker.viewModelTest;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.databinding.ObservableField;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import io.incepted.cryptoaddresstracker.data.source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.data.source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.Operation;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.TestUtils;
import io.incepted.cryptoaddresstracker.viewModels.TxDetailViewModel;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class TxDetailViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static final String TX_HASH_TEST = "tx_hash_test";


    @Mock
    private AddressLocalRepository mLocalRepository;

    @Mock
    private AddressRemoteRepository mRemoteRepository;

    @Mock
    private Application mContext;

    @Mock
    private ObservableField<TransactionInfo> mTxInfoObservableField;

    @Mock
    private TransactionInfo mTransactionInfo;

    @Captor
    private ArgumentCaptor<AddressRemoteDataSource.TransactionInfoListener> mTransactionInfoCaptor;

    private TxDetailViewModel mTxDetailViewModel;

    private TransactionInfo TX_INFO_TEST;


    @Before
    public void setupTxDetailViewModel() {
        MockitoAnnotations.initMocks(this);

        setupContext();

        mTxDetailViewModel = new TxDetailViewModel(mContext, mLocalRepository, mRemoteRepository);
        TX_INFO_TEST  = new TransactionInfo();
        TX_INFO_TEST.setHash(TX_HASH_TEST);
    }

    private void setupContext() {
        when(mContext.getApplicationContext()).thenReturn(mContext);
    }


    @Test
    public void tokenTransferClicked_toTokenTransferActivity() {
        Observer<String> observer = mock(Observer.class);
        mTxDetailViewModel.getOpenTokenOperations().observe(TestUtils.TEST_OBSERVER, observer);

        // Operation exists
        when(mTransactionInfo.getOperations()).thenReturn(Lists.newArrayList(new Operation()));
        when(mTxInfoObservableField.get()).thenReturn(mTransactionInfo);

        // Setting mock objects
        mTxDetailViewModel.mTxInfo = mTxInfoObservableField;

        // Trigger method
        mTxDetailViewModel.toTokenTransferActivity(TX_HASH_TEST);

        // Invocation expected
        verify(observer).onChanged(TX_HASH_TEST);
    }

    @Test
    public void tokenTransferClicked_toTokenTransferActivity_noOperation() {
        Observer<String> observer = mock(Observer.class);
        mTxDetailViewModel.getOpenTokenOperations().observe(TestUtils.TEST_OBSERVER, observer);

        // Operation doesn't exist
        when(mTransactionInfo.getOperations()).thenReturn(new ArrayList<>());
        when(mTxInfoObservableField.get()).thenReturn(mTransactionInfo);

        // Setting mock objects
        mTxDetailViewModel.mTxInfo = mTxInfoObservableField;

        // Trigger method
        mTxDetailViewModel.toTokenTransferActivity(TX_HASH_TEST);

        // No invocation expected
        verify(observer, times(0)).onChanged(TX_HASH_TEST);
    }


    @Test
    public void fetchTxDetail_validTx() {
        mTxDetailViewModel.fetchTxDetail(TX_HASH_TEST);

        verify(mRemoteRepository).fetchTransactionDetail(any(), any(), any(), mTransactionInfoCaptor.capture());

        mTransactionInfoCaptor.getValue().onTransactionDetailReady(TX_INFO_TEST);

        assertFalse(mTxDetailViewModel.isLoading.get());

        assertEquals(mTxDetailViewModel.mTxInfo.get().getHash(), TX_INFO_TEST.getHash());

    }

    @Test
    public void fetchTxDetail_onReady() {
        mTxDetailViewModel.fetchTxDetail(TX_HASH_TEST);

        verify(mRemoteRepository).fetchTransactionDetail(any(), any(), any(), mTransactionInfoCaptor.capture());

        mTransactionInfoCaptor.getValue().onCallReady();

        assertTrue(mTxDetailViewModel.isLoading.get());
    }

    @Test
    public void lfetchTxDetail_onError() {
        Observer<Integer> observer = mock(Observer.class);
        mTxDetailViewModel.getSnackbarTextResource().observe(TestUtils.TEST_OBSERVER, observer);

        mTxDetailViewModel.fetchTxDetail(TX_HASH_TEST);

        verify(mRemoteRepository).fetchTransactionDetail(any(), any(), any(), mTransactionInfoCaptor.capture());

        mTransactionInfoCaptor.getValue().onDataNotAvailable(new Throwable());

        verify(observer, times(2)).onChanged(any());
    }


}
