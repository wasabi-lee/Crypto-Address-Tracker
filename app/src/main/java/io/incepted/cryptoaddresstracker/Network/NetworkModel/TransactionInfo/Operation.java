package io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo;

import java.util.List;

import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.TokenInfo;

public class Operation {
    private Long timestamp;
    private String transactionHash;
    private String value;
    private Double intValue;
    private String type;
    private Integer priority;
    private String from;
    private String to;
    private List<String> addresses = null;
    private Boolean isEth;
    private TokenInfo tokenInfo;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Double getIntValue() {
        return intValue;
    }

    public void setIntValue(Double intValue) {
        this.intValue = intValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public Boolean getEth() {
        return isEth;
    }

    public void setEth(Boolean eth) {
        isEth = eth;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }
}
