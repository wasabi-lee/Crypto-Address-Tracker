package io.incepted.cryptoaddresstracker.Data.TxExtraWrapper;

public class TxExtraWrapper {

    /**
     * A wrapper class for activity transition extras. (for DetailActivity -> TxActivity)
     * Contains address id (from db), user selected token name and token address.
     * This wrapper allows us to use only one MutableLiveData to let DetailActivity change to
     * TxActivity, without having multiple fragmented MutableLiveData for extras.
     */

    private int addressId;
    private String tokenName;
    private String tokenAddress;

    public TxExtraWrapper(int addressId, String tokenName, String tokenAddress) {
        this.addressId = addressId;
        this.tokenName = tokenName;
        this.tokenAddress = tokenAddress;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }
}
