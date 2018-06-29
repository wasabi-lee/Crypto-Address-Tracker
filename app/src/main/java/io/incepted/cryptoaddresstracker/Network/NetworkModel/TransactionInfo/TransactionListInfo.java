package io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo;

import java.util.List;

public class TransactionListInfo {

    private List<Operation> operations = null;

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}