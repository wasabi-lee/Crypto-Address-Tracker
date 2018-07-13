package io.incepted.cryptoaddresstracker;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.ViewModels.TokenTransferViewModel;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TokenTransferViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static final String TX_HASH_TEST = "tx_hash_test";


    @Mock
    private AddressRepository mAddressRepository;

    @Mock
    private Application mContext;

    private TokenTransferViewModel mTxDetailViewModel;


    @Before
    public void setupTokenTransferViewModel() {
        MockitoAnnotations.initMocks(this);

        setupContext();

        mTxDetailViewModel = new TokenTransferViewModel(mContext, mAddressRepository);
    }


    private void setupContext() {
        when(mContext.getApplicationContext()).thenReturn(mContext);
    }

    @Test
    public void loadTransactionInfo_validTx() {
        mTxDetailViewModel.loadTransactionInfo(TX_HASH_TEST);

        assertTrue(mTxDetailViewModel.isLoading.get());

    }
}
