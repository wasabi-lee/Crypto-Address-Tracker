package io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo;

public class EthOperation extends OperationWrapper {
    private Long timestamp;
    private String from;
    private String to;
    private String hash;
    private Double value;
    private String input;
    private Boolean success;


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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public int getTxType() {
        return TX_TYPE_ETHEREUM;
    }
}
