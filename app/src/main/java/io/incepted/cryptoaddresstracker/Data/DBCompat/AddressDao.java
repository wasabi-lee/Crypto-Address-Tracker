package io.incepted.cryptoaddresstracker.Data.DBCompat;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface AddressDao {

    @Query("SELECT * FROM addresses ORDER BY timestamp")
    Flowable<List<Address>> getAllAddresses();

    @Query("SELECT * FROM addresses WHERE _id = (:addressId)")
    Maybe<Address> getAddressById(Integer addressId);

    @Query("SELECT count(*) FROM addresses")
    Maybe<Integer> getAddressCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Address address);

    @Update
    void update(Address address);

    @Query("DELETE FROM addresses WHERE _id = (:addressId)")
    void delete(Integer addressId);

    @Query("DELETE FROM addresses")
    void deleteAllAddresses();

}
