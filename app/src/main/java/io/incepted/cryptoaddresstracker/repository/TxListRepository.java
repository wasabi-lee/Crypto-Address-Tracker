package io.incepted.cryptoaddresstracker.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import io.incepted.cryptoaddresstracker.data.source.txlist.TxListContractDataSource;
import io.incepted.cryptoaddresstracker.data.source.txlist.TxListDataSource;
import io.incepted.cryptoaddresstracker.data.source.txlist.TxListDataSourceFactory;
import io.incepted.cryptoaddresstracker.data.source.txlist.TxListTokenDataSource;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.SimpleTxItemResult;

public class TxListRepository {


    private volatile static TxListRepository INSTANCE = null;


    public TxListRepository() {
    }


    public static TxListRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (TxListRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TxListRepository();
                }
            }
        }
        return INSTANCE;
    }


    public SimpleTxItemResult getTxs(TxListRepository.Type type, String address, String tokenAddress) {

        TxListDataSourceFactory factory = new TxListDataSourceFactory(type, address, tokenAddress);
        LiveData<String> networkError = Transformations.switchMap(factory.getDataSourceLiveData(),
                TxListDataSource::getErrorMessage);
        LiveData<Boolean> isLoading = Transformations.switchMap(factory.getDataSourceLiveData(),
                TxListDataSource::getIsLoading);

        PagedList.Config pagedListConfig = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setPageSize(TxListDataSource.PAGE_SIZE)
                .build();

        LiveData<PagedList<SimpleTxItem>> data =  new LivePagedListBuilder<>(factory, pagedListConfig)
                .build();

        return new SimpleTxItemResult(data, networkError, isLoading);
    }


    public enum Type {
        ETH_TXS,
        TOKEN_TXS,
        TOKEN_TXS_SPECIFIC,
        CONTRACT_TXS
    }
}
