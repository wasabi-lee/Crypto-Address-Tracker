package io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.network.networkModel.ErrorResponse;

public class SimpleTxItemResult {

    private LiveData<PagedList<SimpleTxItem>> itemLiveDataList;
    private List<SimpleTxItem> items;
    private LiveData<String> error;
    private LiveData<Boolean> isLoading;
    private LiveData<Boolean> itemExists;

    public SimpleTxItemResult() {
    }

    public static SimpleTxItemResult getEmptyInstance() {
        return new SimpleTxItemResult(new MutableLiveData<>(),
                new MutableLiveData<>(),
                new MutableLiveData<>(),
                new MutableLiveData<>());
    }

    public SimpleTxItemResult(LiveData<PagedList<SimpleTxItem>> itemLiveDataList) {
        this.itemLiveDataList = itemLiveDataList;
    }

    public SimpleTxItemResult(LiveData<PagedList<SimpleTxItem>> itemLiveDataList,
                              LiveData<String> error,
                              LiveData<Boolean> isLoading,
                              LiveData<Boolean> itemExists) {
        this.itemLiveDataList = itemLiveDataList;
        this.error = error;
        this.isLoading = isLoading;
        this.itemExists = itemExists;
    }



    public LiveData<String> getError() {
        return error;
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

    public void setError(LiveData<String> error) {
        this.error = error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(LiveData<Boolean> isLoading) {
        this.isLoading = isLoading;
    }

    public LiveData<Boolean> getItemExists() {
        return itemExists;
    }

    public void setItemExists(LiveData<Boolean> itemExists) {
        this.itemExists = itemExists;
    }
}
