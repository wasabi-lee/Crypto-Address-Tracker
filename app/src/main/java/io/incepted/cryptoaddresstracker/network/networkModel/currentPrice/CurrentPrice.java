package io.incepted.cryptoaddresstracker.network.networkModel.currentPrice;

import io.incepted.cryptoaddresstracker.utils.NumberUtils;

public class CurrentPrice {

    private String tsym; // toSymbol: The symbol that the ETH price is converted to.
    private Double price;

    private String formattedPrice;
    private String formattedPriceAndSymbol;

    public static CurrentPrice getDefaultBaseCurrencyObject() {
        // Returns a placeholder object for databinding
        // This object will be bound to layout until the network call gets delivered
        return new CurrentPrice("--", 0d);
    }

    public static CurrentPrice getEthCurrencyObject() {
        return new CurrentPrice("ETH", 1d);
    }

    public CurrentPrice(String tsym, Double price) {
        this.tsym = tsym.toUpperCase();
        this.price = price;
        this.formattedPrice = NumberUtils.formatDouble(price);
        this.formattedPriceAndSymbol = formattedPrice + " " + tsym.toUpperCase();
    }

    public String getTsym() {
        return tsym;
    }

    public void setTsym(String tsym) {
        this.tsym = tsym;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }

    public void setFormattedPrice(String formattedPrice) {
        this.formattedPrice = formattedPrice;
    }

    public String getFormattedPriceAndSymbol() {
        return formattedPriceAndSymbol;
    }

    public void setFormattedPriceAndSymbol(String formattedPriceAndSymbol) {
        this.formattedPriceAndSymbol = formattedPriceAndSymbol;
    }
}
