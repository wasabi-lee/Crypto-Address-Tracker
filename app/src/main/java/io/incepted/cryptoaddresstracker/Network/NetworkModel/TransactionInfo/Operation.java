package io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo;

import java.util.List;

import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.TokenInfo;

public class Operation {
    private Integer timestamp;
    private String transactionHash;
    private String value;
    private Integer intValue;
    private String type;
    private Integer priority;
    private String from;
    private String to;
    private List<String> addresses = null;
    private Boolean isEth;
    private TokenInfo tokenInfo;
}
