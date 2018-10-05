package io.incepted.cryptoaddresstracker.utils;

public class CurrencyUtils {

    private static final int USD_INT_VALUE = 0;
    private static final int BTC_INT_VALUE = 1;
    private static final int CNY_INT_VALUE = 2;
    private static final int EUR_INT_VALUE = 3;
    private static final int CAD_INT_VALUE = 4;
    private static final int JPY_INT_VALUE = 5;
    private static final int KRW_INT_VALUE = 6;
    private static final int GBP_INT_VALUE = 7;
    private static final int CHF_INT_VALUE = 8;
    private static final int AUD_INT_VALUE = 9;

    private static final String USD_STR_VALUE = "USD";
    private static final String BTC_STR_VALUE = "BTC";
    private static final String CNY_STR_VALUE = "CNY";
    private static final String EUR_STR_VALUE = "EUR";
    private static final String CAD_STR_VALUE = "CAD";
    private static final String JPY_STR_VALUE = "JPY";
    private static final String KRW_STR_VALUE = "KRW";
    private static final String GBP_STR_VALUE = "GBP";
    private static final String CHF_STR_VALUE = "CHF";
    private static final String AUD_STR_VALUE = "AUD";

    public static String getBaseCurrencyString(int value) {
        switch (value) {
            case USD_INT_VALUE:
                return USD_STR_VALUE;
            case BTC_INT_VALUE:
                return BTC_STR_VALUE;
            case CNY_INT_VALUE:
                return CNY_STR_VALUE;
            case EUR_INT_VALUE:
                return EUR_STR_VALUE;
            case CAD_INT_VALUE:
                return CAD_STR_VALUE;
            case JPY_INT_VALUE:
                return JPY_STR_VALUE;
            case KRW_INT_VALUE:
                return KRW_STR_VALUE;
            case GBP_INT_VALUE:
                return GBP_STR_VALUE;
            case CHF_INT_VALUE:
                return CHF_STR_VALUE;
            case AUD_INT_VALUE:
                return AUD_STR_VALUE;
            default:
                return USD_STR_VALUE;
        }
    }

    public static int getBaseCurrencyInt(String value) {
        switch (value) {
            case USD_STR_VALUE:
                return USD_INT_VALUE;
            case BTC_STR_VALUE:
                return BTC_INT_VALUE;
            case CNY_STR_VALUE:
                return CNY_INT_VALUE;
            case EUR_STR_VALUE:
                return EUR_INT_VALUE;
            case CAD_STR_VALUE:
                return CAD_INT_VALUE;
            case JPY_STR_VALUE:
                return JPY_INT_VALUE;
            case KRW_STR_VALUE:
                return KRW_INT_VALUE;
            case GBP_STR_VALUE:
                return GBP_INT_VALUE;
            case CHF_STR_VALUE:
                return CHF_INT_VALUE;
            case AUD_STR_VALUE:
                return AUD_INT_VALUE;
            default:
                return USD_INT_VALUE;
        }
    }
}
