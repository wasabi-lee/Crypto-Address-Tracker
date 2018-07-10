package io.incepted.cryptoaddresstracker.Data.Source;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import io.incepted.cryptoaddresstracker.Data.DBCompat.AddressDao;
import io.incepted.cryptoaddresstracker.Data.DBCompat.AppDatabase;
import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AddressRepository implements AddressDataSource {


    private static final String TAG = AddressRepository.class.getSimpleName();

    private volatile static AddressRepository INSTANCE = null;
    private AddressDao mAddressDao;
    private Executor executor;

    Map<Integer, Address> mCachedAddresses;
    private boolean mCacheIsDirty = false;


    private AddressRepository(Context context) {
        mAddressDao = AppDatabase.getAppDatabase(context).addressDao();
    }

    public static AddressRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AddressRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AddressRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    @SuppressLint("CheckResult")
    @Override
    public void getAddressCount(@NonNull OnAddressCountListener callback) {
        try {
            mAddressDao.getAddressCount()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::onAddressCount,
                            throwable -> {
                                throwable.printStackTrace();
                                callback.onAddressCountNotAvailable();
                            });
        } catch (Exception e) {
            callback.onAddressCountNotAvailable();
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void getAddresses(@NonNull OnAddressesLoadedListener callback) {
        try {
            mAddressDao.getAllAddresses()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::onAddressesLoaded,
                            throwable -> {
                                throwable.printStackTrace();
                                callback.onAddressesNotAvailable();
                            });
        } catch (Exception e) {
            callback.onAddressesNotAvailable();
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void getAddress(@NonNull int addressId, @NonNull OnAddressLoadedListener callback) {
        try {
            mAddressDao.getAddressById(addressId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::onAddressLoaded,
                            throwable -> {
                                throwable.printStackTrace();
                                callback.onAddressNotAvailable();
                            });
        } catch (Exception e) {
            callback.onAddressNotAvailable();
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void saveAddress(@NonNull Address address, @NonNull OnAddressSavedListener callback) {
        try {

            Completable.fromAction(() -> mAddressDao.insert(address))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::onAddressSaved,
                            throwable -> {
                                throwable.printStackTrace();
                                callback.onSaveNotAvailable();
                            });
        } catch (Exception e) {
            callback.onSaveNotAvailable();
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void updateAddress(@NonNull Address address, @NonNull OnAddressUpdatedListener callback) {
        try {

            Completable.fromAction(() -> mAddressDao.update(address))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> getAddress(address.get_id(), new OnAddressLoadedListener() {
                                @Override
                                public void onAddressLoaded(Address updatedAddress) {
                                    callback.onAddressUpdated(updatedAddress);
                                }

                                @Override
                                public void onAddressNotAvailable() {
                                    callback.onUpdateNotAvailable();
                                }
                            }),
                            throwable -> {
                                throwable.printStackTrace();
                                callback.onUpdateNotAvailable();
                            });
        } catch (Exception e) {
            callback.onUpdateNotAvailable();
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void deleteAddress(@NonNull int addressId, @NonNull OnAddressDeletedListener callback) {

        try {
            Completable.fromAction(() -> mAddressDao.delete(addressId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::onAddressDeleted,
                            throwable -> {
                                throwable.printStackTrace();
                                callback.onDeletionNotAvailable();
                            });
            callback.onAddressDeleted();
        } catch (Exception e) {
            callback.onDeletionNotAvailable();
        }
    }

    @Override
    public void refreshAddresses() {

    }
}
