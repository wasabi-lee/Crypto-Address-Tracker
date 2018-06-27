package io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo;

public class TokenInfo {
    private String address;
    private String name;
    private Integer decimals;
    private String symbol;
    private String totalSupply;
    private String owner;
    private Long lastUpdated;
    private Double totalIn;
    private Double totalOut;
    private Long issuancesCount;
    private Long holdersCount;
    private String image;
    private String description;
    private String website;
    private Long ethTransfersCount;
//    private Price price;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Double getTotalIn() {
        return totalIn;
    }

    public void setTotalIn(Double totalIn) {
        this.totalIn = totalIn;
    }

    public Double getTotalOut() {
        return totalOut;
    }

    public void setTotalOut(Double totalOut) {
        this.totalOut = totalOut;
    }

    public Long getIssuancesCount() {
        return issuancesCount;
    }

    public void setIssuancesCount(Long issuancesCount) {
        this.issuancesCount = issuancesCount;
    }

    public Long getHoldersCount() {
        return holdersCount;
    }

    public void setHoldersCount(Long holdersCount) {
        this.holdersCount = holdersCount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Long getEthTransfersCount() {
        return ethTransfersCount;
    }

    public void setEthTransfersCount(Long ethTransfersCount) {
        this.ethTransfersCount = ethTransfersCount;
    }

//    public Price getPrice() {
//        return price;
//    }
//
//    public void setPrice(Price price) {
//        this.price = price;
//    }
}
