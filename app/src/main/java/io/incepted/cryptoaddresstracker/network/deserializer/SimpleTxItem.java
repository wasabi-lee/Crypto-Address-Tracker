package io.incepted.cryptoaddresstracker.network.deserializer;

import io.incepted.cryptoaddresstracker.network.networkModel.ErrorResponse;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.TokenInfo;

public class SimpleTxItem {


    private String hash;
    private Long timestamp;
    private String from;
    private String to;
    private String value;
    private TokenInfo tokenInfo;
    private String symbol;
    private String decimals;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    // If the TokenInfo is not provided, it means that this tx is a token transaction.
    public boolean isEthTx() {
        return tokenInfo == null;
    }

}
