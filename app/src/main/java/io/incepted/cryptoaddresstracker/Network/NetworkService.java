package io.incepted.cryptoaddresstracker.Network;

import io.incepted.cryptoaddresstracker.Network.NetworkModel.SimpleAddressInfo;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkService {
    //http://api.ethplorer.io/getAddressInfo/0x32Be343B94f860124dC4fEe278FDCBD38C102D88?apiKey=freekey&showETHTotals=true

    @GET("getAddressInfo/{address}")
    Observable<SimpleAddressInfo> getSimpleAddressInfo(@Path("address") String address,
                                                       @Query("apiKey") String apiKey,
                                                       @Query("showETHTotals") boolean showEthTotals);


}
