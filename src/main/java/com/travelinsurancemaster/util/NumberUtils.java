package com.travelinsurancemaster.util;

import com.ibm.icu.text.RuleBasedNumberFormat;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Created by Chernov Artur on 28.07.15.
 */
public class NumberUtils {

    private static final RuleBasedNumberFormat ordinalFormat = new RuleBasedNumberFormat(Locale.US, RuleBasedNumberFormat.SPELLOUT);

    public static Long parseLong(String arg, Long defaultValue) {
        Long val = defaultValue;
        try {
            val = org.apache.commons.lang3.math.NumberUtils.createLong(arg);
        } catch (NumberFormatException e) {
            val = null;
        }
        return val;
    }

    public static Long parseLong(String arg) {
        return parseLong(arg, null);
    }

    public static BigDecimal parseBigDecimal(String arg) {
        BigDecimal result;
        try {
            result = new BigDecimal(arg);
        } catch (NumberFormatException e) {
            return null;
        }
        return result;
    }

    public static BigDecimal getNotNullValue(BigDecimal balance) {
        return balance != null ? balance : BigDecimal.ZERO.setScale(2);
    }

    public static String getOrdinal(Integer num) {
        return ordinalFormat.format(num, "%spellout-ordinal");
    }
}