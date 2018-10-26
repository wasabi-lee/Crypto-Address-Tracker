package io.incepted.cryptoaddresstracker.data.source.txlist;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.EthOperation;

public class TxListEthDataSourceFactory extends DataSource.Factory<Long, EthOperation> {

    private MutableLiveData<TxListEthDataSource> dataSourceLiveData;
    private TxListEthDataSource ethDataSource;

    public TxListEthDataSourceFactory() {
        this.dataSourceLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Long, EthOperation> create() {
        ethDataSource = new TxListEthDataSource();
        dataSourceLiveData.postValue(ethDataSource);
        return ethDataSource;
    }

    public MutableLiveData<TxListEthDataSource> getDataSourceLiveData() {
        return dataSourceLiveData;
    }
}
