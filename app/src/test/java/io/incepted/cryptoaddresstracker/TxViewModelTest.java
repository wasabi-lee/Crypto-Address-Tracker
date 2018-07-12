package io.incepted.cryptoaddresstracker;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;

import io.incepted.cryptoaddresstracker.Data.Source.AddressRepository;

import static org.mockito.Mockito.when;

public class TxViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();


    @Mock
    private AddressRepository mAddressRepository;

    @Mock
    private Application mContext;


    private void setupContext() {
        when(mContext.getApplicationContext()).thenReturn(mContext);
    }

}
