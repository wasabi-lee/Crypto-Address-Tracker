package io.incepted.cryptoaddresstracker.Network;

import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.RemoteAddressInfo;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkService {
    //http://api.ethplorer.io/getAddressInfo/0x32Be343B94f860124dC4fEe278FDCBD38C102D88?apiKey=freekey&showETHTotals=true

    @GET("getAddressInfo/{address}")
    Observable<RemoteAddressInfo> getSimpleAddressInfo(@Path("address") String address,
                                                       @Query("apiKey") String apiKey,
                                                       @Query("showETHTotals") boolean showEthTotals);

    @GET("getAddressInfo/{address}")
    Observable<RemoteAddressInfo> getDetailedAddressInfo(@Path("address") String address,
                                                         @Query("apiKey") String apiKey,
                                                         @Query("showETHTotals") boolean showEthTotals);


}
