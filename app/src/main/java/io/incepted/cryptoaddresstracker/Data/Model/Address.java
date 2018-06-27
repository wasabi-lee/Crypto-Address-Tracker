package io.incepted.cryptoaddresstracker.Data.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
import java.util.List;

import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.TokenInfo;

@Entity(tableName = "addresses")
public class Address {


    @Ignore
    private static final String TAG = Address.class.getSimpleName();

    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "addr_value")
    private String addrValue;

    @ColumnInfo(name = "bookmarked")
    private boolean bookmarked;

    @ColumnInfo(name = "timestamp")
    private Date timestamp;


    // ---------------------- Temporary fields (Ignored in DB) -------------------


    /**
     * An object that contains the server response data about this address.
     */
    @Ignore
    RemoteAddressInfo remoteAddressInfo;


    /**
     * Temporary list position value. Used this to preserve the order from RxJava's flatMap() method.
     */
    @Ignore
    private int listPosition;


    // ---------------------- Constructor --------------------------

    public Address(String name, String addrValue, Date timestamp) {
        this.name = name;
        this.addrValue = addrValue;
        this.timestamp = timestamp;
    }


    // ---------------------- Getter and setters -------------------

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddrValue() {
        return addrValue;
    }

    public void setAddrValue(String addrValue) {
        this.addrValue = addrValue;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public RemoteAddressInfo getRemoteAddressInfo() {
        return remoteAddressInfo;
    }

    public void setRemoteAddressInfo(RemoteAddressInfo remoteAddressInfo) {
        this.remoteAddressInfo = remoteAddressInfo;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }
}
