package io.incepted.cryptoaddresstracker.Utils;

public class NumberUtils {

    public static double moveDecimal(double value, int decimals) {
        double result = value;

        while (decimals >= 0) {
            result /= 10;
            decimals--;
        }

        return result;
    }
}
