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
import io.incepted.cryptoaddresstracker.data.source.address.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.data.source.address.AddressRemoteDataSource;
import io.incepted.cryptoaddresstracker.data.txExtraWrapper.TxExtraWrapper;
import io.incepted.cryptoaddresstracker.navigators.DeletionStateNavigator;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.ContractInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.ETH;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.Token;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.TokenInfo;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DetailViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static final String NAME_TEST = "name";
    private static final String ADDR_VALUE_TEST = "addr_value";
    private static final String NEW_NAME_TEST = "new_name";
    private static final int ID_TEST = 0;

    private static final String TOKEN_NAME_TEST = "token_name";
    private static final String TOKEN_ADDR_VALUE_TEST = "token_value";

    @Mock
    private AddressLocalRepository mLocalRepository;

    @Mock
    private AddressRemoteRepository mRemoteRepository;

    @Mock
    private Application mContext;

    @Mock
    private AddressLocalDataSource.OnAddressLoadedListener mRepositoryCallback;

    @Mock
    private AddressLocalDataSource.OnAddressLoadedListener mViewModelCallback;


    @Captor
    private ArgumentCaptor<AddressLocalDataSource.OnAddressLoadedListener> mAddressLoadCallbackCaptor;

    @Captor
    private ArgumentCaptor<AddressLocalDataSource.OnAddressUpdatedListener> mAddressUpdateCallbackCaptor;

    @Captor
    private ArgumentCaptor<AddressLocalDataSource.OnAddressDeletedListener> mAddressDeletedCallbackCaptor;

    @Captor
    private ArgumentCaptor<AddressRemoteDataSource.DetailAddressInfoListener> mDetailAddressInfoCaptor;

    private DetailViewModel mDetailViewModel;

    private Address mAddress;

    private RemoteAddressInfo remoteAddressInfo;

    private List<Token> tokens_not_aligned = Lists.newArrayList(new Token(new TokenInfo("zrx")),
            new Token(new TokenInfo("gup")), new Token(new TokenInfo("icn")));

    private List<Token> tokens_aligned = Lists.newArrayList(new Token(new TokenInfo("Ethereum")),
            new Token(new TokenInfo("gup")), new Token(new TokenInfo("icn")),
            new Token(new TokenInfo("zrx")));


    @Before
    public void setupDetailViewModel() {
        // Init mock objects
        MockitoAnnotations.initMocks(this);

        setupContext();

        // Setting up the address
        mAddress = new Address(NAME_TEST, ADDR_VALUE_TEST, new Date());
        mAddress.set_id(ID_TEST);

        remoteAddressInfo = new RemoteAddressInfo();

        // Init test object
        mDetailViewModel = new DetailViewModel(mContext, mLocalRepository, mRemoteRepository);
        mDetailViewModel.setAddressId(ID_TEST);
    }

    private void setupContext() {
        when(mContext.getApplicationContext()).thenReturn(mContext);
    }

    @Test
    public void getAddressFromRepositoryASndLoadIntoView() {
        // Trigger address loading
        setupViewModelRepositoryCallback();

        assertEquals(mDetailViewModel.mAddress.get().getName(), mAddress.getName());
        assertEquals(mDetailViewModel.mAddress.get().getAddrValue(), mAddress.getAddrValue());
    }

    @Test
    public void updateAddressNewName_validName() {
        setupViewModelRepositoryCallback();
        setupNewAddressNameUpdateCallback(NEW_NAME_TEST);

        assertEquals(mDetailViewModel.mAddress.get().getName(), mAddress.getName());
    }

    @Test
    public void updatedAddressNewName_emptyName() {
        // When the new name input is empty
        setupViewModelRepositoryCallback();
        setupNewAddressNameUpdateCallback("");

        // The empty name should be converted to the address value as a default name
        // e.g.) "" -> "0x123..."
        assertEquals(mDetailViewModel.mAddress.get().getName(), mAddress.getName());
    }

    @Test
    public void deleteAddressTest() {
        setupViewModelRepositoryCallback();

        // Setup deletion state observer
        Observer<DeletionStateNavigator> observer = mock(Observer.class);
        mDetailViewModel.getDeletionState().observe(TestUtils.TEST_OBSERVER, observer);

        // Trigger deletion call
        mDetailViewModel.deleteAddress();

        // Show progress bar
        verify(observer).onChanged(DeletionStateNavigator.DELETION_IN_PROGRESS);

        // Is the repository method called
        verify(mLocalRepository).deleteAddress(eq(mAddress.get_id()), mAddressDeletedCallbackCaptor.capture());

        // Set callback
        mAddressDeletedCallbackCaptor.getValue().onAddressDeleted();

        // Hide progress bar
        verify(observer).onChanged(DeletionStateNavigator.DELETION_SUCCESSFUL);
    }

    @Test
    public void tokenClicked_toTxActivity() {
        // Setup observer
        Observer<TxExtraWrapper> observer = mock(Observer.class);
        mDetailViewModel.getOpenTokenTransactions().observe(TestUtils.TEST_OBSERVER, observer);

        // Trigger call
        mDetailViewModel.toTxActivity(TOKEN_NAME_TEST, TOKEN_ADDR_VALUE_TEST);

        // Is the observer called
        verify(observer).onChanged(any());
    }


    @Test
    public void updateViewsTest_NormalAddress() {
        setupViewModelRepositoryCallback();

        remoteAddressInfo.setTokens(new ArrayList<>());
        remoteAddressInfo.setEthBalanceInfo(new ETH());
        remoteAddressInfo.getEthBalanceInfo().setBalance(0d);
        remoteAddressInfo.setTokens(tokens_not_aligned);
        mDetailViewModel.updateViews(remoteAddressInfo);

        // Is contract address
        assertFalse(mDetailViewModel.isContractAddress.get());

        // Hide progress bar
        assertFalse(mDetailViewModel.isLoading.get());

        // Is the first element 'Ethereum' (checking realigned list)
        assertEquals("Ethereum", mDetailViewModel.mTokens.get(0).getTokenInfo().getName());

        // Is the realinged list's size +1 of the original list? (Ethereum is added in the front of the list)
        assertTrue(mDetailViewModel.mTokens.size() == 4);

        ArrayList<String> actualNames = new ArrayList<>();
        for (Token token : mDetailViewModel.mTokens) {
            actualNames.add(token.getTokenInfo().getName());
        }

        ArrayList<String> expectedNames = new ArrayList<>();
        for (Token token: tokens_aligned) {
            expectedNames.add(token.getTokenInfo().getName());
        }

        // Is the list sorted alphabetically?
        assertEquals(actualNames, expectedNames);

        // Is the new address info set
        assertEquals(mAddress.getRemoteAddressInfo(), mDetailViewModel.mAddress.get().getRemoteAddressInfo());
    }

    @Test
    public void updateViewsTest_ContractAddress() {
        setupViewModelRepositoryCallback();

        remoteAddressInfo.setContractInfo(new ContractInfo());
        mDetailViewModel.updateViews(remoteAddressInfo);

        // Is contract address? Show placeholder screen
        assertTrue(mDetailViewModel.isContractAddress.get());

        // Hide progress bar
        assertFalse(mDetailViewModel.isLoading.get());

        // Is the new address info set
        assertEquals(mAddress.getRemoteAddressInfo(), mDetailViewModel.mAddress.get().getRemoteAddressInfo());
    }

    @Test
    public void remoteApiCallTest_onSuccess() {
        setupViewModelRepositoryCallback();

        verify(mRemoteRepository).fetchDetailedAddressInfo(any(), any(), any(), mDetailAddressInfoCaptor.capture());

        // rest is same as 'updateViewsTest'.
    }

    @Test
    public void remoteApiCallTest_onError() {
        // setup observer
        Observer<Integer> observer = mock(Observer.class);
        mDetailViewModel.getSnackbarTextResource().observe(TestUtils.TEST_OBSERVER, observer);

        setupViewModelRepositoryCallback();

        verify(mRemoteRepository).fetchDetailedAddressInfo(any(), any(), any(), mDetailAddressInfoCaptor.capture());

        mDetailAddressInfoCaptor.getValue().onDataNotAvailable(new Throwable());

        // hide progress bar
        assertFalse(mDetailViewModel.isLoading.get());

        // Is the snackbar shown
        verify(observer, times(2)).onChanged(any());

    }


    private void setupViewModelRepositoryCallback() {
        mViewModelCallback = mock(AddressLocalDataSource.OnAddressLoadedListener.class);

        // Load address
        mDetailViewModel.loadAddress(mAddress.get_id());

        // Is the repository call triggered
        verify(mLocalRepository).getAddress(eq(mAddress.get_id()), mAddressLoadCallbackCaptor.capture());

        // Trigger callback
        mAddressLoadCallbackCaptor.getValue().onAddressLoaded(mAddress);
    }

    private void setupNewAddressNameUpdateCallback(String newName) {
        // Update address
        mDetailViewModel.updateAddressNewName(newName);

        // Prepare the expected value
        mAddress.setName(mAddress.getAddrValue());

        // Is the repository call triggered
        verify(mLocalRepository).updateAddress(eq(mAddress), mAddressUpdateCallbackCaptor.capture());

        // Trigger callback
        mAddressUpdateCallbackCaptor.getValue().onAddressUpdated(mAddress);
    }





}
