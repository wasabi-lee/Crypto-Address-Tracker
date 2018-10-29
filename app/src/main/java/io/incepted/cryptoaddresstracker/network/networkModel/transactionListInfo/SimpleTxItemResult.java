package io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.network.networkModel.ErrorResponse;

public class SimpleTxItemResult {
    private ErrorResponse error;
    private LiveData<PagedList<SimpleTxItem>> itemLiveDataList;
    private List<SimpleTxItem> items;

    public SimpleTxItemResult() {
    }

    public SimpleTxItemResult(LiveData<PagedList<SimpleTxItem>> itemLiveDataList) {
        this.itemLiveDataList = itemLiveDataList;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

    public LiveData<PagedList<SimpleTxItem>> getItemLiveDataList() {
        return itemLiveDataList;
    }

    public void setItemLiveDataList(LiveData<PagedList<SimpleTxItem>> itemLiveDataList) {
        this.itemLiveDataList = itemLiveDataList;
    }

    public List<SimpleTxItem> getItems() {
        return items;
    }

    public void setItems(List<SimpleTxItem> items) {
        this.items = items;
    }
}
