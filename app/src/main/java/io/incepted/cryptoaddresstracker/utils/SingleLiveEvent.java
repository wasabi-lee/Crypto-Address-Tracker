package io.incepted.cryptoaddresstracker.utils;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import timber.log.Timber;

public class SingleLiveEvent<T> extends MutableLiveData<T> {

    private AtomicBoolean mPending = new AtomicBoolean(false);

    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, t -> {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t);
            }
        });
    }

    @MainThread
    public void setValue(T value) {
        mPending.set(true);
        super.setValue(value);
    }
}
