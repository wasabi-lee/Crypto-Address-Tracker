package io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo;

import java.util.List;

public class TransactionListInfo {

    private List<TokenOperation> operations = null;

    public List<TokenOperation> getOperations() {
        return operations;
    }

    public void setOperations(List<TokenOperation> operations) {
        this.operations = operations;
    }
}