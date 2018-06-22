package io.incepted.cryptoaddresstracker.Data.DBCompat;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;

@Dao
public interface AddressDao {

    @Query("SELECT * FROM addresses ORDER BY timestamp")
    List<Address> getAllAddresses();

    @Query("SELECT * FROM addresses WHERE _id = (:addressId)")
    Address getAddressById(String addressId);

    @Query("SELECT count(*) FROM addresses")
    Integer getAddressCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Address address);

    @Update
    void update(Address address);

    @Query("DELETE FROM addresses WHERE _id = (:addressId)")
    void delete(String addressId);

}
