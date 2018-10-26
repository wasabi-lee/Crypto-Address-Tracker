package io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

public class TxListInfoResult {
    public LiveData<PagedList<EthOperation>> data;

    public TxListInfoResult() {
    }

    public TxListInfoResult(LiveData<PagedList<EthOperation>> data) {
        this.data = data;
    }

    public LiveData<PagedList<EthOperation>> getData() {
        return data;
    }

    public void setData(LiveData<PagedList<EthOperation>> data) {
        this.data = data;
    }
}
