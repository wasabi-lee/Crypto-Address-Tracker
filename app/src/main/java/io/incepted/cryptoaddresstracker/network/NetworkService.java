package io.incepted.cryptoaddresstracker.network;

import java.util.List;

import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.SimpleTxItemResult;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.TransactionListInfo;
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
    Single<SimpleTxItemResult> getContractTokenTransactionListInfo(@Path("address") String address,
                                                                   @Query("apiKey") String apiKey,
                                                                   @Query("timestamp") long timestamp,
                                                                   @Query("limit") int pageSize);

    @GET("getAddressHistory/{address}")
    Single<SimpleTxItemResult> getSpecificTokenTransactionListInfo(@Path("address") String address,
                                                                   @Query("token") String tokenAddress,
                                                                   @Query("apiKey") String apiKey,
                                                                   @Query("timestamp") long timestamp,
                                                                   @Query("limit") int pageSize);

    @GET("getAddressHistory/{address}")
    Single<SimpleTxItemResult> getTokenTransactionListInfo(@Path("address") String address,
                                                                   @Query("apiKey") String apiKey,
                                                                   @Query("timestamp") long timestamp,
                                                                   @Query("limit") int pageSize);

    @GET("getAddressTransactions/{address}")
    Single<SimpleTxItemResult> getEthTransactionListInfo(@Path("address") String address,
                                                         @Query("apiKey") String apiKey,
                                                         @Query("timestamp") Long timestamp,
                                                         @Query("limit") int pageSize);


    @GET("getTxInfo/{txHash}")
    Single<TransactionInfo> getTransactionDetail(@Path("txHash") String txHash,
                                                 @Query("apiKey") String apiKey);


    @GET("data/price")
    Single<CurrentPrice> getCurrentPrice(@Query("fsym") String fsym,
                                         @Query("tsyms") String tsyms);
}
