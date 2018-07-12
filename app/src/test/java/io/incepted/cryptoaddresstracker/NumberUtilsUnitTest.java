package io.incepted.cryptoaddresstracker;

import org.junit.Test;
import static org.junit.Assert.*;

import io.incepted.cryptoaddresstracker.Utils.NumberUtils;

public class NumberUtilsUnitTest {

    @Test
    public void testMoveDecimal() {
        double actual_1 = NumberUtils.moveDecimal(43345d, 2);
        double expected_1 = 433.45d;

        double actual_2 = NumberUtils.moveDecimal(4361d, 10);
        double expected_2 = 0.0000004361d;

        assertEquals("Moving decimal failed", expected_1, actual_1, 0.001d);
        assertEquals("Moving decimal failed", expected_2, actual_2, 0.001d);

    }

    @Test
    public void testMoveStringDecimal() {
        String value = "54345600000000000043245";

        String actual = NumberUtils.moveDecimal(value, 18);
        String expected = "54,345.60";

        assertEquals("Moving String decimal failed", expected, actual);
        assertEquals("Moving String decimal failed", expected, actual);
    }

    @Test
    public void testFormatDouble() {
        String actual = NumberUtils.formatDouble(3234565.8909d);
        String expected = "3,234,565.89";
        assertEquals("Formatting double failed", expected, actual);
    }
}
