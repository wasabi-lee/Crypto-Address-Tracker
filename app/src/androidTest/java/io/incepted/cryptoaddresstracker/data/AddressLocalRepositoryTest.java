package io.incepted.cryptoaddresstracker.data;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.incepted.cryptoaddresstracker.data.dbCompat.AddressDao;
import io.incepted.cryptoaddresstracker.data.dbCompat.AppDatabase;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.data.source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.data.source.AddressLocalRepository;
import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class AddressLocalRepositoryTest {

    private static final String TAG = AddressLocalRepository.class.getSimpleName();

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final static String TITLE = "title";

    private final static String TITLE2 = "title2";

    private final static String TITLE3 = "title3";

    private AddressLocalRepository mLocalRepository;

    private AppDatabase mDatabase;


    @Mock
    private AddressLocalDataSource.OnAddressesLoadedListener mAddressesCallback;

    @Mock
    private AddressLocalDataSource.OnAddressLoadedListener mAddressCallback;

    @Mock
    private AddressLocalDataSource.OnAddressSavedListener mSaveCallback;

    @Mock
    private AddressLocalDataSource.OnAddressUpdatedListener mupdateCallback;

    @Mock
    private AddressLocalDataSource.OnAddressDeletedListener mDeleteCallback;

    @Mock
    private AddressLocalDataSource.OnAllAddressDeletedListener mDeleteAllCallback;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        AddressDao addressDao = mDatabase.addressDao();

        AddressLocalRepository.destroyInstance();
        mLocalRepository = AddressLocalRepository.getInstance(addressDao);

        mLocalRepository.setBackgroundScheduler(Schedulers.trampoline());
        mLocalRepository.setMainScheduler(Schedulers.trampoline());
    }

    @After
    public void cleanUp() {
        mDatabase.close();
        AddressLocalRepository.destroyInstance();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mLocalRepository);
    }

    @Test
    public void getAddresses() {
        final Address newAddress = new Address(TITLE, "0x123", null);
        mLocalRepository.saveAddress(newAddress, mock(AddressLocalDataSource.OnAddressSavedListener.class));

        mLocalRepository.getAddresses(mAddressesCallback);

        verify(mAddressesCallback).onAddressesLoaded(anyList());
        verify(mAddressesCallback, never()).onAddressesNotAvailable();
    }

    @Test
    public void saveAddress_retrieveAddress() {
        final Address newAddress = new Address(TITLE, "0x123", null);
        newAddress.set_id(12);

        mLocalRepository.saveAddress(newAddress, mSaveCallback);

        mLocalRepository.getAddress(newAddress.get_id(), mAddressCallback);

        verify(mAddressCallback).onAddressLoaded(any());
        verify(mAddressCallback, never()).onAddressNotAvailable();
    }

    @Test
    public void updateAddress_retrieveAddress() {
        final Address newAddress = new Address(TITLE, "0x123", null);
        newAddress.set_id(12);

        mLocalRepository.saveAddress(newAddress,
                mock(AddressLocalDataSource.OnAddressSavedListener.class));

        final Address updatedAddress = new Address(TITLE, "0x567", null);
        updatedAddress.set_id(12);

        mLocalRepository.updateAddress(updatedAddress,
                mock(AddressLocalDataSource.OnAddressUpdatedListener.class));

        mLocalRepository.getAddress(updatedAddress.get_id(), mAddressCallback);

        verify(mAddressCallback).onAddressLoaded(any());
        verify(mAddressCallback, never()).onAddressNotAvailable();
    }


    @Test
    public void deleteAddress_remainingAddressListRetrieved() {
        final Address newAddress_1 = new Address(TITLE, "0x123", null);
        final Address newAddress_2 = new Address(TITLE, "0x123", null);
        newAddress_1.set_id(12);
        newAddress_1.set_id(16);

        mLocalRepository.saveAddress(newAddress_1, mock(AddressLocalDataSource.OnAddressSavedListener.class));
        mLocalRepository.saveAddress(newAddress_2, mock(AddressLocalDataSource.OnAddressSavedListener.class));

        mLocalRepository.deleteAddress(newAddress_1.get_id(), mock(AddressLocalDataSource.OnAddressDeletedListener.class));

        mLocalRepository.getAddresses(mAddressesCallback);

        verify(mAddressesCallback).onAddressesLoaded(anyList());
        verify(mAddressesCallback, never()).onAddressesNotAvailable();
    }


    @Test
    public void deleteAllAddresses_emptyAddressListRetrieved() {
        final Address newAddress = new Address(TITLE, "0x123", null);
        mLocalRepository.saveAddress(newAddress, mock(AddressLocalDataSource.OnAddressSavedListener.class));
        mLocalRepository.deleteAllAddresses(mock(AddressLocalDataSource.OnAllAddressDeletedListener.class));

        mLocalRepository.getAddresses(mAddressesCallback);

        verify(mAddressesCallback).onAddressesLoaded(anyList());
        verify(mAddressesCallback, never()).onAddressesNotAvailable();
    }


    private void assertAddress(Address address, int id, String name, String addrValue) {
        assertThat(address, notNullValue());
        assertThat(address.get_id(), is(id));
        assertThat(address.getName(), is(name));
        assertThat(address.getAddrValue(), is(addrValue));
    }

}
