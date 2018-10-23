package io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *  A model class for retrieving a list of transactions occurred
 *  in an address regarding a specific token.
 *
 *  i.e) A list of OMG token transactions in address 0x33454... .
 *
 */


public class TransactionListInfo {

    @SerializedName("operations")
    private List<TokenOperation> tokenTxList = null;

    public List<TokenOperation> getTokenTxList() {
        return tokenTxList;
    }

    public void setTokenTxList(List<TokenOperation> tokenTxList) {
        this.tokenTxList = tokenTxList;
    }
}