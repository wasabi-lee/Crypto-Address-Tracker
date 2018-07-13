package io.incepted.cryptoaddresstracker;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.Navigators.ActivityNavigator;
import io.incepted.cryptoaddresstracker.ViewModels.MainViewModel;

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

    private static List<Address> ADDRESSES;

    @Mock
    private AddressLocalRepository mAddressRepository;

    @Mock
    private Application mContext;

    @Captor
    private ArgumentCaptor<AddressLocalDataSource.OnAddressesLoadedListener> mAddressesLoadedCaptor;

    private MainViewModel mMainViewModel;

    @Before
    public void setupMainViewModel() {
        MockitoAnnotations.initMocks(this);

        setupContext();

        mMainViewModel = new MainViewModel(mContext, mAddressRepository);

        ADDRESSES = Lists.newArrayList(new Address("Title1", "Addr1", new Date()),
                new Address("Title2", "Addr2", new Date()),
                new Address("Title3", "Addr3", new Date()));
    }

    private void setupContext() {
        when(mContext.getApplicationContext()).thenReturn(mContext);
    }

    @Test
    public void loadAllAddressesFromRepository_dataLoaded() {

        // Before loading
        mMainViewModel.loadAddresses();

        // Is getAddresses called
        verify(mAddressRepository).getAddresses(mAddressesLoadedCaptor.capture());

        // Progress bar shown
        assertTrue(mMainViewModel.isDataLoading.get());

        // Setting dummy result
        mAddressesLoadedCaptor.getValue().onAddressesLoaded(ADDRESSES);

        // Address should exists. Hiding placeholder view.
        assertTrue(mMainViewModel.addressesExist.get());

        assertFalse(mMainViewModel.mAddressList.isEmpty());
        assertTrue(mMainViewModel.mAddressList.size() == 3);
    }

    @Test
    public void loadAllAddressesFromRepository_emptyDataLoaded() {

        // Before loading
        mMainViewModel.loadAddresses();

        // Is getAddresses called
        verify(mAddressRepository).getAddresses(mAddressesLoadedCaptor.capture());

        // Progress bar shown
        assertTrue(mMainViewModel.isDataLoading.get());

        // Setting dummy result
        mAddressesLoadedCaptor.getValue().onAddressesLoaded(new ArrayList<>());

        // Hide progress bar
        assertFalse(mMainViewModel.isDataLoading.get());

        // Address should exists. Hiding placeholder view.
        assertFalse(mMainViewModel.addressesExist.get());

        assertTrue(mMainViewModel.mAddressList.isEmpty());
    }


    @Test
    public void clickNewAddress_launchNewAddressActivity() {
        Observer<ActivityNavigator> observer = mock(Observer.class);

        mMainViewModel.getActivityNavigator().observe(TestUtils.TEST_OBSERVER, observer);

        // When adding a new address
        mMainViewModel.addNewAddress();

        verify(observer, times(2)).onChanged(any());
    }


    @Test
    public void clickToSettings_launchSettingsActivity() {
        Observer<ActivityNavigator> observer = mock(Observer.class);

        mMainViewModel.getActivityNavigator().observe(TestUtils.TEST_OBSERVER, observer);

        // When adding a new address
        mMainViewModel.toSettings();

        verify(observer, times(2)).onChanged(any());
    }


    @Test
    public void clickNewAddress_toTxScanActivity() {
        Observer<ActivityNavigator> observer = mock(Observer.class);

        mMainViewModel.getActivityNavigator().observe(TestUtils.TEST_OBSERVER, observer);

        // When adding a new address
        mMainViewModel.toTxScan();

        verify(observer, times(2)).onChanged(any());
    }


    @Test
    public void clickAddress_toDetailActivity() {
        Observer<Integer> observer = mock(Observer.class);

        mMainViewModel.getOpenAddressDetail().observe(TestUtils.TEST_OBSERVER, observer);

        // When adding a new address
        mMainViewModel.openAddressDetail(1);

        verify(observer, times(2)).onChanged(any());
    }


    @Test
    public void resetActivityNav_Test() {
        Observer<ActivityNavigator> observer = mock(Observer.class);
        mMainViewModel.getActivityNavigator().observe(TestUtils.TEST_OBSERVER, observer);
        mMainViewModel.resetActivityNav();
        verify(observer).onChanged(any());
    }


}
