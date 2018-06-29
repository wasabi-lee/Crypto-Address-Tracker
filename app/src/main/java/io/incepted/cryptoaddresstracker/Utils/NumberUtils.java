package io.incepted.cryptoaddresstracker.Utils;

import java.text.DecimalFormat;

public class NumberUtils {

    public static double moveDecimal(double value, int decimals) {
        double result = value;

        while (decimals > 0) {
            result /= 10;
            decimals--;
        }

        return result;
    }

    public static String moveDecimal(String value, int decimals) {

        int length = value.length();
        if (length <= decimals) {
            StringBuilder sb = new StringBuilder("0.");
            for (int i = 0; i < (decimals-length); i++) {
                sb.append("0");
            }
            return sb.append(value).toString();

        } else {
            String formattedValue = value.substring(0, length - decimals)
                    + "."
                    + value.substring(length - decimals, length - 1);
            Double doubleValue = Double.valueOf(formattedValue);

            return formatDouble(doubleValue);

        }
    }

    public static String formatDouble(Double value) {
        DecimalFormat df = new DecimalFormat(value < 10 ? "#.####" : "#.##");
        return df.format(value);
    }
}
