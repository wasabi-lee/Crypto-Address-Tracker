package io.incepted.cryptoaddresstracker.data.source.txlist;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;

public class TxListDataSourceFactory extends DataSource.Factory<Long, SimpleTxItem> {

    private MutableLiveData<TxListDataSource> dataSourceLiveData;

    public TxListDataSourceFactory() {
        this.dataSourceLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Long, SimpleTxItem> create() {
        TxListDataSource ethDataSource = new TxListDataSource();
        dataSourceLiveData.postValue(ethDataSource);
        return ethDataSource;
    }

    public MutableLiveData<TxListDataSource> getDataSourceLiveData() {
        return dataSourceLiveData;
    }
}
