package io.incepted.cryptoaddresstracker.Network.NetworkModel.CurrentPrice;

import io.incepted.cryptoaddresstracker.Utils.NumberUtils;

public class CurrentPrice {

    private String tsym; // toSymbol: The symbol that the ETH price is converted to.
    private Double price;

    private String formattedPrice;
    private String formattedPriceAndSymbol;

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
