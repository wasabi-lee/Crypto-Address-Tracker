package io.incepted.cryptoaddresstracker.viewModelTest;

import android.app.Application;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import io.incepted.cryptoaddresstracker.TestUtils;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressLocalCallbacks;
import io.incepted.cryptoaddresstracker.data.source.callbacks.AddressRemoteCallbacks;
import io.incepted.cryptoaddresstracker.navigators.ActivityNavigator;
import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.repository.AddressRepository;
import io.incepted.cryptoaddresstracker.repository.PriceRepository;
import io.incepted.cryptoaddresstracker.viewModels.MainViewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Application mContext;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PriceRepository priceRepository;

    @Captor
    private ArgumentCaptor<AddressLocalCallbacks.OnAddressesLoadedListener> mAddressesLoadedCaptor;

    @Captor
    private ArgumentCaptor<AddressRemoteCallbacks.SimpleAddressInfoListener> mSimpleAddrInfoCaptor;

    @Captor
    private ArgumentCaptor<PriceRepository.OnPriceLoadedListener> mPriceInfoCaptor;


    private static List<Address> ADDRESSES;

    private MainViewModel mMainViewModel;

    @Before
    public void setupMainViewModel() {
        MockitoAnnotations.initMocks(this);

        setupContext();

        mMainViewModel = new MainViewModel(mContext, addressRepository, priceRepository);

        ADDRESSES = Lists.newArrayList(new Address("Title1", "Addr1", new Date()),
                new Address("Title2", "Addr2", new Date()),
                new Address("Title3", "Addr3", new Date()));
    }


    private void setupContext() {
        when(mContext.getApplicationContext()).thenReturn(mContext);
    }


    @Test
    public void loadAddresses_onSuccess() {
        // Before loading
        mMainViewModel.loadAddresses();

        // Is getAddresses called
        verify(addressRepository).getAddresses(mAddressesLoadedCaptor.capture());

        // Setting dummy result
        mAddressesLoadedCaptor.getValue().onAddressesLoaded(ADDRESSES);

        // Address should exists. Hiding placeholder view.
        assertTrue(mMainViewModel.addressesExist.get());

        // Do addresses exists?
        assertFalse(mMainViewModel.mAddressList.isEmpty());
        assertEquals(mMainViewModel.mAddressList.size(), 3);

        // Is the remote api call triggered?
        verify(addressRepository).fetchMultipleSimpleAddressInfo(any(), mSimpleAddrInfoCaptor.capture());
    }


    @Test
    public void loadAddresses_onEmpty() {
        // Before loading
        mMainViewModel.loadAddresses();

        // Is getAddresses called
        verify(addressRepository).getAddresses(mAddressesLoadedCaptor.capture());

        // Setting dummy result
        mAddressesLoadedCaptor.getValue().onAddressesLoaded(new ArrayList<>());

        // Address shouldn't exists.
        assertFalse(mMainViewModel.addressesExist.get());

        // Do addresses exists?
        assertTrue(mMainViewModel.mAddressList.isEmpty());
        assertEquals(mMainViewModel.mAddressList.size(), 0);

        // Is the remote api call triggered?
        verify(addressRepository, times(0)).fetchMultipleSimpleAddressInfo(any(), mSimpleAddrInfoCaptor.capture());

    }


    @Test
    public void loadAddresses_onError() {

        // setup observer
        Observer<Integer> observer = mock(Observer.class);
        mMainViewModel.getSnackbarTextResource().observe(TestUtils.TEST_OBSERVER, observer);

        // Before loading
        mMainViewModel.loadAddresses();

        // Is getAddresses called
        verify(addressRepository).getAddresses(mAddressesLoadedCaptor.capture());

        // Setting dummy result
        mAddressesLoadedCaptor.getValue().onAddressesNotAvailable();

        // Is the remote api call triggered?
        verify(addressRepository, times(0)).fetchMultipleSimpleAddressInfo(any(), mSimpleAddrInfoCaptor.capture());

        // Is the error message shown
        verify(observer).onChanged(any());
    }


    @Test
    public void loadEthPrice_onSuccess() {
        mMainViewModel.mDisplayEthPrice = false;

        mMainViewModel.loadEthPrice("USD");

        verify(priceRepository).loadCurrentPrice(any(), mPriceInfoCaptor.capture());

        CurrentPrice price = mock(CurrentPrice.class);

        mPriceInfoCaptor.getValue().onPriceLoaded(price);

        assertEquals(mMainViewModel.mBaseCurrencyPrice, price);

        assertEquals(mMainViewModel.getCurrentPrice().getValue(), price);

    }


    @Test
    public void loadEthPrice_onError() {

        Observer<Integer> observer = mock(Observer.class);

        mMainViewModel.getSnackbarTextResource().observe(TestUtils.TEST_OBSERVER, observer);

        mMainViewModel.mDisplayEthPrice = false;

        mMainViewModel.loadEthPrice("USD");

        verify(priceRepository).loadCurrentPrice(any(), mPriceInfoCaptor.capture());

        mPriceInfoCaptor.getValue().onError(new Throwable());

        verify(observer).onChanged(any());
    }


    @Test
    public void clickNewAddress_launchNewAddressActivity() {
        Observer<ActivityNavigator> observer = mock(Observer.class);

        mMainViewModel.getActivityNavigator().observe(TestUtils.TEST_OBSERVER, observer);

        // When adding a new address
        mMainViewModel.addNewAddress();

        verify(observer).onChanged(any());
    }


    @Test
    public void clickToSettings_launchSettingsActivity() {
        Observer<ActivityNavigator> observer = mock(Observer.class);

        mMainViewModel.getActivityNavigator().observe(TestUtils.TEST_OBSERVER, observer);

        // When adding a new address
        mMainViewModel.toSettings();

        verify(observer).onChanged(any());
    }


    @Test
    public void clickNewAddress_toTxScanActivity() {
        Observer<ActivityNavigator> observer = mock(Observer.class);

        mMainViewModel.getActivityNavigator().observe(TestUtils.TEST_OBSERVER, observer);

        // When adding a new address
        mMainViewModel.toTxScan();

        verify(observer).onChanged(any());
    }


    @Test
    public void clickAddress_toDetailActivity() {
        Observer<Integer> observer = mock(Observer.class);

        mMainViewModel.getOpenAddressDetail().observe(TestUtils.TEST_OBSERVER, observer);

        // When adding a new address
        mMainViewModel.openAddressDetail(1);

        verify(observer).onChanged(any());
    }


}
