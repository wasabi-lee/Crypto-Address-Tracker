package io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo;

import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.TokenInfo;

public class TokenOperation extends OperationWrapper {
    private Long timestamp;
    private String transactionHash;
    private TokenInfo tokenInfo;
    private String type;
    private String value;
    private String from;
    private String to;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    @Override
    public int getTxType() {
        return TX_TYPE_TOKEN;
    }
}
