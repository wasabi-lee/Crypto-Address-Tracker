package io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo;

public class ETH {

    private Double balance;
    private Double totalIn;
    private Double totalOut;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getTotalIn() {
        return totalIn;
    }

    public void setTotalIn(Double totalIn) {
        this.totalIn = totalIn;
    }

    public Double getTotalOut() {
        return totalOut;
    }

    public void setTotalOut(Double totalOut) {
        this.totalOut = totalOut;
    }
}
