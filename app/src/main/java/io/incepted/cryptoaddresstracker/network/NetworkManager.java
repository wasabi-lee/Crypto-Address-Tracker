package io.incepted.cryptoaddresstracker.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.incepted.cryptoaddresstracker.network.deserializer.CurrentPriceDeserializer;
import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;
import retrofit2.Retrofit;

public class NetworkManager {

    private static final String TAG = NetworkManager.class.getSimpleName();

    public static final String BASE_URL_ETHPLORER = "http://api.ethplorer.io/";

    public static final String BASE_URL_CRYPTOCOMPARE = "https://min-api.cryptocompare.com/";

    public static <T> NetworkService getCustomNetworkService(String base_url, Class<?> returnType, T deserializer) {
        Retrofit retrofit = RetrofitHelper.createRetrofitWithRxConverter(base_url,
                createGsonBuilder(returnType, deserializer));
        return retrofit.create(NetworkService.class);
    }

    public static NetworkService getDefaultNetworkService(String base_url) {
        Retrofit retrofit = RetrofitHelper.createRetrofitWithRxConverter(base_url);
        return retrofit.create(NetworkService.class);
    }

    public static NetworkService getCurrentPrice(String base_url) {
        Retrofit retrofit = RetrofitHelper.createRetrofitWithRxConverter(base_url,
                createGsonBuilder(CurrentPrice.class, new CurrentPriceDeserializer()));
        return retrofit.create(NetworkService.class);
    }

    private static <T> Gson createGsonBuilder(Class<?> clazz, T deserializer) {
        return new GsonBuilder().registerTypeAdapter(clazz, deserializer).create();
    }
}