package io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RemoteAddressInfo {

    private String address;

    @SerializedName("ETH")
    private ETH ethBalanceInfo;

    private Long countTxs;

    private ContractInfo contractInfo;

    private TokenInfo tokenInfo;

    private List<Token> tokens = null;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ETH getEthBalanceInfo() {
        return ethBalanceInfo;
    }

    public void setEthBalanceInfo(ETH ethBalanceInfo) {
        this.ethBalanceInfo = ethBalanceInfo;
    }

    public Long getCountTxs() {
        return countTxs;
    }

    public void setCountTxs(Long countTxs) {
        this.countTxs = countTxs;
    }

    public ContractInfo getContractInfo() {
        return contractInfo;
    }

    public void setContractInfo(ContractInfo contractInfo) {
        this.contractInfo = contractInfo;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }
}
