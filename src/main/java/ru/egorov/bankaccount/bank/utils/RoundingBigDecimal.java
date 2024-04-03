package ru.egorov.bankaccount.bank.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundingBigDecimal {
    private static final int ROUNDING_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static BigDecimal roundBigDecimal(BigDecimal value) {
        return value.setScale(ROUNDING_SCALE, ROUNDING_MODE);
    }

    public static BigDecimal roundBigDecimal(long value) {
        return BigDecimal.valueOf(value).setScale(ROUNDING_SCALE, ROUNDING_MODE);
    }

    public static BigDecimal roundBigDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(ROUNDING_SCALE, ROUNDING_MODE);
    }
}
