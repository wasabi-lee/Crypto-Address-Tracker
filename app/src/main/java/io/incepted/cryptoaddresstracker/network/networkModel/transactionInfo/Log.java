package io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo;

import java.util.List;

public class Log {
    private String address;
    private List<String> topics = null;
    private String data;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
