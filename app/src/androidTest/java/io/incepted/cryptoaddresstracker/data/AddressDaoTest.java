package io.incepted.cryptoaddresstracker.data;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.incepted.cryptoaddresstracker.Data.DBCompat.AppDatabase;
import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.reactivex.functions.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AddressDaoTest {

    private static final String TAG = AddressDaoTest.class.getSimpleName();

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static final Address ADDRESS = new Address("title", "0x123", null);
    private AppDatabase mDatabase;

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        ADDRESS.set_id(12);
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void insertAddressAndGetById() {
        mDatabase.addressDao().insert(ADDRESS);

        mDatabase.addressDao()
                .getAddressById(ADDRESS.get_id())
                .test()
                .assertValue(address -> (address.get_id() == ADDRESS.get_id()));
    }


    @Test
    public void updateAndGetUser() {
        mDatabase.addressDao().insert(ADDRESS);

        Address updatedAddress = new Address("NEW_NAME", "0x12354", null);
        updatedAddress.set_id(12);

        mDatabase.addressDao().update(updatedAddress);

        mDatabase.addressDao().getAddressById(ADDRESS.get_id())
                .test()
                .assertValue(address -> address != null && address.getName().equals(updatedAddress.getName()));
    }

    @Test
    public void getAllAddresses() {
        mDatabase.addressDao().insert(ADDRESS);

        mDatabase.addressDao().getAllAddresses()
                .test()
                .assertValue(addresses -> addresses.size() != 0);

    }

    @Test
    public void getAddressCount() throws InterruptedException {
        mDatabase.addressDao().insert(ADDRESS);

        mDatabase.addressDao().getAddressCount()
                .test()
                .await()
                .assertValue(count -> count == 1);
    }

    @Test
    public void deleteAndGetUser() {
        mDatabase.addressDao().insert(ADDRESS);

        mDatabase.addressDao().delete(ADDRESS.get_id());

        mDatabase.addressDao().getAllAddresses()
                .test()
                .assertValue(addresses -> addresses.size() == 0);
    }



}
