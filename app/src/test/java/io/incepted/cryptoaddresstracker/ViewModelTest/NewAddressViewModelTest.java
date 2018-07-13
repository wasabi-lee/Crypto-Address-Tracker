package io.incepted.cryptoaddresstracker.ViewModelTest;

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
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.Data.Source.AddressLocalRepository;
import io.incepted.cryptoaddresstracker.Data.Source.AddressRemoteRepository;
import io.incepted.cryptoaddresstracker.Navigators.AddressStateNavigator;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.TestUtils;
import io.incepted.cryptoaddresstracker.ViewModels.NewAddressViewModel;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewAddressViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static final String NAME_TEST = "name";
    private static final String ADDR_VALUE_TEST = "addr_value";
    private static final String QR_STRING_TEST = "qr_string";

    @Mock
    private AddressLocalRepository mLocalRepository;

    @Mock
    private AddressRemoteRepository mRemoteRepository;

    @Mock
    private Application mContext;

    @Captor
    private ArgumentCaptor<AddressLocalDataSource.OnAddressSavedListener> mAddressSavedCaptor;

    private NewAddressViewModel mNewAddressViewModel;

    private Address mAddress;

    @Before
    public void setupNewAddressViewModel() {
        // Init mock object
        MockitoAnnotations.initMocks(this);

        setupContext();

        mAddress = new Address(NAME_TEST, ADDR_VALUE_TEST, new Date());


        mNewAddressViewModel = new NewAddressViewModel(mContext, mLocalRepository, mRemoteRepository);
        mNewAddressViewModel.name.set(NAME_TEST);
        mNewAddressViewModel.address.set(ADDR_VALUE_TEST);
    }

    private void setupContext() {
        when(mContext.getApplicationContext()).thenReturn(mContext);
    }

    @Test
    public void clickSave_validName() {
        Observer<AddressStateNavigator> observer = mock(Observer.class);
        mNewAddressViewModel.getAddressState().observe(TestUtils.TEST_OBSERVER, observer);

        mNewAddressViewModel.saveAddress();

        verify(mLocalRepository).saveAddress(any(), mAddressSavedCaptor.capture());

        mAddressSavedCaptor.getValue().onAddressSaved();

        verify(observer, times(2)).onChanged(any());
    }

    @Test
    public void clickSave_Error() {
        Observer<AddressStateNavigator> addressStateObserver = mock(Observer.class);
        mNewAddressViewModel.getAddressState().observe(TestUtils.TEST_OBSERVER, addressStateObserver);

        Observer<Integer> snackbarObserver = mock(Observer.class);
        mNewAddressViewModel.getSnackbarTextResource().observe(TestUtils.TEST_OBSERVER, snackbarObserver);

        mNewAddressViewModel.saveAddress();

        verify(mLocalRepository).saveAddress(any(), mAddressSavedCaptor.capture());

        mAddressSavedCaptor.getValue().onSaveNotAvailable();

        verify(addressStateObserver, times(2)).onChanged(any());
        verify(snackbarObserver).onChanged(R.string.unexpected_error);

    }

    @Test
    public void clickQrScan_toQrScanActivity() {
        Observer<Void> observer = mock(Observer.class);
        mNewAddressViewModel.getOpenQRScanActivity().observe(TestUtils.TEST_OBSERVER, observer);

        mNewAddressViewModel.toQRScanActivity();

        verify(observer).onChanged(any());
    }

    //TODO Move handleActivityResult() test to Android test since IntentResult cannot be mocked.




}
