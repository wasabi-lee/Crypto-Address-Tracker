package io.incepted.cryptoaddresstracker.Network;

import java.util.List;

import io.incepted.cryptoaddresstracker.Network.NetworkModel.CurrentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.Operation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.TransactionListInfo;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkService {
    // http://api.ethplorer.io/getAddressInfo/0x32Be343B94f860124dC4fEe278FDCBD38C102D88?apiKey=freekey&showETHTotals=true
    // http://api.ethplorer.io/getAddressHistory/0x1f5006dff7e123d550abc8a4c46792518401fcaf?apiKey=freekey&token=0xc66ea802717bfb9833400264dd12c2bceaa34a6d&type=transfer
    // http://api.ethplorer.io/getAddressTransactions/0xb297cacf0f91c86dd9d2fb47c6d12783121ab780?apiKey=freekey
    // https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=USD

    @GET("getAddressInfo/{address}")
    Observable<RemoteAddressInfo> getSimpleAddressInfo(@Path("address") String address,
                                                       @Query("apiKey") String apiKey,
                                                       @Query("showETHTotals") boolean showEthTotals);

    @GET("getAddressInfo/{address}")
    Single<RemoteAddressInfo> getDetailedAddressInfo(@Path("address") String address,
                                                         @Query("apiKey") String apiKey,
                                                         @Query("showETHTotals") boolean showEthTotals);

    @GET("getTokenHistory/{address}")
    Single<TransactionListInfo> getContractTokenTransactionListInfo(@Path("address") String address,
                                                                        @Query("apiKey") String apiKey);

    @GET("getAddressHistory/{address}")
    Single<TransactionListInfo> getTokenTransactionListInfo(@Path("address") String address,
                                                            @Query("apiKey") String apiKey,
                                                            @Query("token") String tokenAddress);

    @GET("getAddressTransactions/{address}")
    Single<List<EthOperation>> getEthTransactionListInfo(@Path("address") String address,
                                                      @Query("apiKey") String apiKey);


    @GET("getTxInfo/{txHash}")
    Single<TransactionInfo> getTransactionDetail(@Path("txHash") String txHash,
                                                 @Query("apiKey") String apiKey);


    @GET("data/price")
    Single<CurrentPrice> getCurrentPrice(@Query("fsym") String fsym,
                                         @Query("tsyms") String tsyms);
}
