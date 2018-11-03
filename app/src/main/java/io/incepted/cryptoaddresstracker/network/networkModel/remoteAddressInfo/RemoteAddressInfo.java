package io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.incepted.cryptoaddresstracker.network.networkModel.ErrorResponse;

public class RemoteAddressInfo {

    private String address;

    @SerializedName("ETH")
    private ETH ethBalanceInfo;

    private Long countTxs;

    private ContractInfo contractInfo;

    private TokenInfo tokenInfo;

    private List<Token> tokens = null;

    private ErrorResponse error;

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

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

    public boolean isError() {
        return error != null;
    }

    public boolean isContract() {
        return contractInfo != null;
    }

    public boolean isTokenAddress() {
        return tokenInfo != null;
    }
}
