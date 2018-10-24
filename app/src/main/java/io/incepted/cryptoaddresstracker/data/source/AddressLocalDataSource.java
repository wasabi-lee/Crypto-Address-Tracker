package io.incepted.cryptoaddresstracker.data.source;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import io.incepted.cryptoaddresstracker.data.dbCompat.AddressDao;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddressLocalDataSource {
    // Prevent direct instantiation.


    private static volatile AddressLocalDataSource INSTANCE;

    private AddressDao mAddressDao;

    private Scheduler backgroundScheduler = Schedulers.io();

    private Scheduler mainScheduler = AndroidSchedulers.mainThread();

    private AddressLocalDataSource(
                                 @NonNull AddressDao dao) {
        mAddressDao = dao;
    }

    public static AddressLocalDataSource getInstance(@NonNull AddressDao tasksDao) {
        if (INSTANCE == null) {
            synchronized (AddressLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AddressLocalDataSource(tasksDao);
                }
            }
        }
        return INSTANCE;
    }


    @SuppressLint("CheckResult")
    public void getAddressCount(@NonNull AddressLocalCallbacks.OnAddressCountListener callback) {
        try {
            mAddressDao.getAddressCount()
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
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
    public void getAddresses(@NonNull AddressLocalCallbacks.OnAddressesLoadedListener callback) {
        try {
            mAddressDao.getAllAddresses()
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
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
    public void getAddress(@NonNull int addressId, @NonNull AddressLocalCallbacks.OnAddressLoadedListener callback) {
        try {
            mAddressDao.getAddressById(addressId)
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
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
    public void saveAddress(@NonNull Address address, @NonNull AddressLocalCallbacks.OnAddressSavedListener callback) {
        try {
            Completable.fromAction(() -> mAddressDao.insert(address))
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
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
    public void updateAddress(@NonNull Address address, @NonNull AddressLocalCallbacks.OnAddressUpdatedListener callback) {
        try {
            Completable.fromAction(() -> mAddressDao.update(address))
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe(callback::onAddressUpdated,
                            throwable -> {
                                throwable.printStackTrace();
                                callback.onUpdateNotAvailable();
                            });
        } catch (Exception e) {
            callback.onUpdateNotAvailable();
        }
    }


    @SuppressLint("CheckResult")
    public void deleteAddress(@NonNull int addressId, @NonNull AddressLocalCallbacks.OnAddressDeletedListener callback) {
        try {
            Completable.fromAction(() -> mAddressDao.delete(addressId))
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe(callback::onAddressDeleted,
                            throwable -> {
                                throwable.printStackTrace();
                                callback.onDeletionNotAvailable();
                            });
        } catch (Exception e) {
            callback.onDeletionNotAvailable();
        }
    }


    @SuppressLint("CheckResult")
    public void deleteAllAddresses(@NonNull AddressLocalCallbacks.OnAllAddressDeletedListener callback) {
        try {
            Completable.fromAction(() -> mAddressDao.deleteAllAddresses())
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe(callback::onAddressesDeleted,
                            throwable -> {
                                throwable.printStackTrace();
                                callback.onDeletionNotAvailable();
                            });
        } catch (Exception e) {
            callback.onDeletionNotAvailable();
        }
    }


    public void refreshAddresses() {

    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    public Scheduler getBackgroundScheduler() {
        return backgroundScheduler;
    }

    public void setBackgroundScheduler(Scheduler backgroundScheduler) {
        this.backgroundScheduler = backgroundScheduler;
    }

    public Scheduler getMainScheduler() {
        return mainScheduler;
    }

    public void setMainScheduler(Scheduler mainScheduler) {
        this.mainScheduler = mainScheduler;
    }

}
