package io.incepted.cryptoaddresstracker;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;
import io.incepted.cryptoaddresstracker.ViewModels.TxViewModel;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TxViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static final int ID_TEST = 0;
    private static final String NAME_TEST = "name";
    private static final String ADDR_VALUE_TEST = "addr_value";

    @Mock
    private AddressRepository mAddressRepository;

    @Mock
    private Application mContext;

    @Captor
    private ArgumentCaptor<AddressDataSource.OnAddressLoadedListener> mAddressLoadCallbackCaptor;

    private TxViewModel mTxViewModel;

    private Address mAddress;

    @Before
    public void setupTxViewModel() {
        MockitoAnnotations.initMocks(this);

        setupContext();

        mTxViewModel = new TxViewModel(mContext, mAddressRepository);
        mAddress = new Address(NAME_TEST, ADDR_VALUE_TEST, new Date());
        mAddress.set_id(ID_TEST);
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


    private void setupViewModelRepositoryCallback() {
        // Load address
        mTxViewModel.loadAddress(mAddress.get_id());

        assertTrue(mTxViewModel.isLoading.get());

        // Is the repository call triggered
        verify(mAddressRepository).getAddress(eq(mAddress.get_id()), mAddressLoadCallbackCaptor.capture());

        // Trigger callback
        mAddressLoadCallbackCaptor.getValue().onAddressLoaded(mAddress);

    }

}
