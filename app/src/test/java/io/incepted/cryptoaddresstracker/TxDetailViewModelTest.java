package io.incepted.cryptoaddresstracker;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.databinding.ObservableField;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.Operation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.ViewModels.TxDetailViewModel;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class TxDetailViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static final String TX_HASH_TEST = "tx_hash_test";


    @Mock
    private AddressLocalRepository mAddressRepository;

    @Mock
    private Application mContext;

    @Mock
    private ObservableField<TransactionInfo> mTxInfoObservableField;

    @Mock
    private TransactionInfo mTransactionInfo;

    private TxDetailViewModel mTxDetailViewModel;

    @Before
    public void setupTxDetailViewModel() {
        MockitoAnnotations.initMocks(this);

        setupContext();

        mTxDetailViewModel = new TxDetailViewModel(mContext, mAddressRepository);
    }

    private void setupContext() {
        when(mContext.getApplicationContext()).thenReturn(mContext);
    }

    @Test
    public void fetchTxDetail_validTx() {
        mTxDetailViewModel.fetchTxDetail(TX_HASH_TEST);
        assertTrue(mTxDetailViewModel.isLoading.get());
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


}
