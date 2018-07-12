package io.incepted.cryptoaddresstracker;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SnackbarLiveDataTest {

    // Allows synchronous testing
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private LifecycleOwner mOwner;

    @Mock
    private Observer<Integer> mEventObserver;

    private LifecycleRegistry mLifecycle;

    private MutableLiveData<Integer> mSnackbarTextResource = new MutableLiveData<>();

    @Before
    public void setupLifecycles() throws Exception {
        MockitoAnnotations.initMocks(this);

        mLifecycle = new LifecycleRegistry(mOwner);
        when(mOwner.getLifecycle()).thenReturn(mLifecycle);

        mSnackbarTextResource.observe(mOwner, mEventObserver);
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Test
    public void valueNotSet_onFirstOnResume() {
        // OnResume
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);

        verify(mEventObserver, never()).onChanged(anyInt());
    }

    @Test
    public void singleUpdate_onSecondOnResume_updateOnce() {
        // After a value is set
        mSnackbarTextResource.setValue(R.string.app_name);
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);

        verify(mEventObserver, times(1)).onChanged(anyInt());
    }

    @Test
    public void twoUpdates_updatesTwice() {
        mSnackbarTextResource.setValue(R.string.scan);
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        mSnackbarTextResource.setValue(R.string.amount);
        verify(mEventObserver, times(2)).onChanged(anyInt());
    }

    @Test
    public void twoUpdates_noUpdateUntilActive() {
        mSnackbarTextResource.setValue(R.string.app_name);
        verify(mEventObserver, never()).onChanged(R.string.app_name);

        mSnackbarTextResource.setValue(R.string.scan);
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        verify(mEventObserver, times(1)).onChanged(anyInt());
    }
}
