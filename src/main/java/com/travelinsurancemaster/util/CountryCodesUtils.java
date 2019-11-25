package com.travelinsurancemaster.util;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Alexander.Isaenco
 */
public class CountryCodesUtils {

    private CountryCodesUtils() {
    }

    public static String getCountryStatePair(CountryCode countryCode, StateCode stateCode) {
        if (countryCode == null) {
            return null;
        }
        String countryStatePair = countryCode.toString();
        if (stateCode != null) {
            countryStatePair += "-".concat(stateCode.toString());
        }
        return countryStatePair;
    }

    public static CountryCode getCountryCode(String pair) {
        if (StringUtils.isEmpty(pair)) {
            return null;
        }
        String[] codes = pair.split("-");

        return CountryCode.valueOf(codes[0]);

    }

    public static StateCode getStateCode(String pair) {
        if (StringUtils.isEmpty(pair)) {
            return null;
        }
        String[] codes = pair.split("-");
        return codes.length == 2 ? StateCode.valueOf(codes[1]) : null;
    }

    public static String getText(String pair) {
        if (StringUtils.isEmpty(pair)) {
            return StringUtils.EMPTY;
        }
        String[] codes = pair.split("-");
        if (codes.length == 0) {
            return StringUtils.EMPTY;
        }
        if (codes.length == 1) {
            return CountryCodes.getCountryByCode(codes[0]);
        }
        return StateCode.valueOf(codes[1]).getCaption();
    }

    public static CountryCode[] getPublicVisibleCountryCodesSorted(){
        CountryCode[] destinationCountries = CountryCode.values();
        destinationCountries = ArrayUtils.removeElement(destinationCountries, CountryCode.CRI);
        Arrays.sort(destinationCountries, Comparator.comparing(CountryCode::getCaption));
        return destinationCountries;
    }
}
