package io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo;

import java.util.List;

import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.network.networkModel.ErrorResponse;

public class SimpleTxItemResult {
    private ErrorResponse error;
    private List<SimpleTxItem> simpleTxItems;

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

    public List<SimpleTxItem> getSimpleTxItems() {
        return simpleTxItems;
    }

    public void setSimpleTxItems(List<SimpleTxItem> simpleTxItems) {
        this.simpleTxItems = simpleTxItems;
    }
}
