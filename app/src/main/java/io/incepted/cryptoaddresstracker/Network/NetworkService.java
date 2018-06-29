package io.incepted.cryptoaddresstracker.Network;

import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.TransactionListInfo;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkService {
    //http://api.ethplorer.io/getAddressInfo/0x32Be343B94f860124dC4fEe278FDCBD38C102D88?apiKey=freekey&showETHTotals=true
    //http://api.ethplorer.io/getAddressHistory/0x1f5006dff7e123d550abc8a4c46792518401fcaf?apiKey=freekey&token=0xc66ea802717bfb9833400264dd12c2bceaa34a6d&type=transfer

    @GET("getAddressInfo/{address}")
    Observable<RemoteAddressInfo> getSimpleAddressInfo(@Path("address") String address,
                                                       @Query("apiKey") String apiKey,
                                                       @Query("showETHTotals") boolean showEthTotals);

    @GET("getAddressInfo/{address}")
    Observable<RemoteAddressInfo> getDetailedAddressInfo(@Path("address") String address,
                                                         @Query("apiKey") String apiKey,
                                                         @Query("showETHTotals") boolean showEthTotals);

    @GET("getAddressHistory/{address}")
    Single<TransactionListInfo> getTransactionListInfo(@Path("address") String address,
                                                       @Query("apiKey") String apiKey,
                                                       @Query("token") String tokenAddress);



}
