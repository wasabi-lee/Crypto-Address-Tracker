package io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo;

public class Price {

    private String rate;
    private Double diff;
    private Double diff7d;
    private String ts;
    private String marketCapUsd;
    private String availableSupply;
    private Object volume24h;
    private Double diff30d;
    private String currency;

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public double getDiff() {
        return diff;
    }

    public void setDiff(double diff) {
        this.diff = diff;
    }

    public Double getDiff7d() {
        return diff7d;
    }

    public void setDiff7d(Double diff7d) {
        this.diff7d = diff7d;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getMarketCapUsd() {
        return marketCapUsd;
    }

    public void setMarketCapUsd(String marketCapUsd) {
        this.marketCapUsd = marketCapUsd;
    }

    public String getAvailableSupply() {
        return availableSupply;
    }

    public void setAvailableSupply(String availableSupply) {
        this.availableSupply = availableSupply;
    }

    public Object getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(Object volume24h) {
        this.volume24h = volume24h;
    }

    public Double getDiff30d() {
        return diff30d;
    }

    public void setDiff30d(Double diff30d) {
        this.diff30d = diff30d;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
