package io.incepted.cryptoaddresstracker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.incepted.cryptoaddresstracker.network.NetworkService;
import io.incepted.cryptoaddresstracker.network.PriceFetcher;
import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PriceFetcherTest {

    @Mock
    private PriceFetcher.OnPriceLoadedListener mPriceLoadListener;

    @Mock
    private NetworkService mNetworkService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCurrentPrice_validCall() {

        when(mNetworkService.getCurrentPrice(any(), any()))
                .thenReturn(Single.just(new CurrentPrice("TSYM_TEST", 1.23d)));

        PriceFetcher.setApiCallObservable(mNetworkService);

        PriceFetcher.loadCurrentPrice("TSYM_TEST",
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mPriceLoadListener);

        verify(mPriceLoadListener).onPriceLoaded(any());

    }

    @Test
    public void getCurrentPrice_error() {

        Exception exception = new Exception();

        when(mNetworkService.getCurrentPrice(any(), any()))
                .thenReturn(Single.error(exception));

        PriceFetcher.setApiCallObservable(mNetworkService);

        PriceFetcher.loadCurrentPrice("TSYM_TEST",
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                mPriceLoadListener);

        verify(mPriceLoadListener).onError(any());
    }

}
