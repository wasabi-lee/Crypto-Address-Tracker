package io.incepted.cryptoaddresstracker.data.source.txlist;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.repository.TxListRepository;

public class TxListDataSourceFactory extends DataSource.Factory<Long, SimpleTxItem> {

    private MutableLiveData<TxListDataSource> dataSourceLiveData;
    private TxListRepository.Type type;
    private String address;
    private String tokenAddress;

    public TxListDataSourceFactory(TxListRepository.Type type, String address, String tokenAddress) {
        this.dataSourceLiveData = new MutableLiveData<>();
        this.type = type;
        this.address = address;
        this.tokenAddress = tokenAddress;
    }

    @Override
    public DataSource<Long, SimpleTxItem> create() {
        TxListDataSource ethDataSource = new TxListDataSource(type, address, tokenAddress);
        dataSourceLiveData.postValue(ethDataSource);
        return ethDataSource;
    }

    public MutableLiveData<TxListDataSource> getDataSourceLiveData() {
        return dataSourceLiveData;
    }
}
