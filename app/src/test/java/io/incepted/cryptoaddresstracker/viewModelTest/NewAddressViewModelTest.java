package io.incepted.cryptoaddresstracker.viewModelTest;

import android.app.Application;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.TestUtils;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.address.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.navigators.AddressStateNavigator;
import io.incepted.cryptoaddresstracker.viewModels.NewAddressViewModel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewAddressViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static final String NAME_TEST = "name_test";
    private static final String ADDR_VALUE_TEST = "0x123";
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

        mNewAddressViewModel.name.set(NAME_TEST);
        mNewAddressViewModel.name.set(ADDR_VALUE_TEST);

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
