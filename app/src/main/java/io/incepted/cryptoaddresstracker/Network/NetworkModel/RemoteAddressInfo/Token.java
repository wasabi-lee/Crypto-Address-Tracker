package io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo;

public class Token {
    private TokenInfo tokenInfo;
    private Double balance;
    private Double totalIn;
    private Double totalOut;

    public Token() {
    }

    public Token(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

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
