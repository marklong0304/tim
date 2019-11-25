package com.travelinsurancemaster.services.itravelinsured;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ritchie on 2/27/15.
 */
public class CountryArea {
    private static Map<String, String> countryAreaMap = new HashMap<>();

    /**
     * @return countryCode, required by iTravelInsured vendor.
     */
    public static String getAreaByCode(String countryCode) {
        String areaCode = countryAreaMap.get(countryCode);
        return areaCode;
    }

    static {
        /**
         * 1 – U.S. Domestic, Hawaii, Alaska
         2 – Caribbean
         3 – Europe
         4 – Far East
         5 – Middle East
         6 – Central America
         7 – South America
         8 – Africa
         9 – India
         10 – Canada
         11 – Mexico
         12 – Other
         */
        countryAreaMap.put("AF", "12");
        countryAreaMap.put("AL", "3");
        countryAreaMap.put("DZ", "8");
        countryAreaMap.put("AS", "12");
        countryAreaMap.put("AD", "3");
        countryAreaMap.put("AO", "8");
        countryAreaMap.put("AI", "2");
        countryAreaMap.put("AQ", "12");
        countryAreaMap.put("AG", "2");
        countryAreaMap.put("AR", "7");
        countryAreaMap.put("AM", "12");
        countryAreaMap.put("AW", "2");
        countryAreaMap.put("AU", "12");
        countryAreaMap.put("AT", "3");
        countryAreaMap.put("AZ", "3");
        countryAreaMap.put("BS", "2");
        countryAreaMap.put("BH", "5");
        countryAreaMap.put("BD", "12");
        countryAreaMap.put("BB", "2");
        countryAreaMap.put("BY", "3");
        countryAreaMap.put("BE", "3");
        countryAreaMap.put("BZ", "6");
        countryAreaMap.put("BJ", "8");
        countryAreaMap.put("BM", "12");
        countryAreaMap.put("BT", "12");
        countryAreaMap.put("BO", "7");
        countryAreaMap.put("BA", "3");
        countryAreaMap.put("BW", "8");
        countryAreaMap.put("BV", "12");
        countryAreaMap.put("BR", "7");
        countryAreaMap.put("IO", "12");
        countryAreaMap.put("BN", "4");
        countryAreaMap.put("BG", "3");
        countryAreaMap.put("BF", "8");
        countryAreaMap.put("BI", "8");
        countryAreaMap.put("KH", "4");
        countryAreaMap.put("CM", "8");
        countryAreaMap.put("CA", "10");
        countryAreaMap.put("CV", "8");
        countryAreaMap.put("KY", "2");
        countryAreaMap.put("CF", "8");
        countryAreaMap.put("TD", "8");
        countryAreaMap.put("CL", "7");
        countryAreaMap.put("CN", "4");
        countryAreaMap.put("CX", "12");
        countryAreaMap.put("CC", "12");
        countryAreaMap.put("CO", "7");
        countryAreaMap.put("KM", "8");
        countryAreaMap.put("CG", "8");
        countryAreaMap.put("CK", "12");
        countryAreaMap.put("CR", "6");
        countryAreaMap.put("CI", "8");
        countryAreaMap.put("HR", "3");
        countryAreaMap.put("CU", "2");
        countryAreaMap.put("CY", "5");
        countryAreaMap.put("CZ", "3");
        countryAreaMap.put("DK", "3");
        countryAreaMap.put("DJ", "8");
        countryAreaMap.put("DM", "2");
        countryAreaMap.put("DO", "2");
        countryAreaMap.put("TP", "4");
        countryAreaMap.put("EC", "7");
        countryAreaMap.put("EG", "8"); // todo: Egypt is also located in the middle east, code 5
        countryAreaMap.put("SV", "6");
        countryAreaMap.put("GQ", "8");
        countryAreaMap.put("ER", "8");
        countryAreaMap.put("EE", "3");
        countryAreaMap.put("ET", "8");
        countryAreaMap.put("FK", "7");
        countryAreaMap.put("FO", "12");
        countryAreaMap.put("FJ", "12");
        countryAreaMap.put("FI", "3");
        countryAreaMap.put("FR", "3");
        countryAreaMap.put("FX", "3");
        countryAreaMap.put("GF", "7");
        countryAreaMap.put("PF", "12");
        countryAreaMap.put("TF", "12");
        countryAreaMap.put("GA", "8");
        countryAreaMap.put("GM", "8");
        countryAreaMap.put("GE", "3");
        countryAreaMap.put("DE", "3");
        countryAreaMap.put("GH", "8");
        countryAreaMap.put("GI", "12");
        countryAreaMap.put("GR", "3");
        countryAreaMap.put("GL", "12");
        countryAreaMap.put("GD", "2");
        countryAreaMap.put("GP", "2");
        countryAreaMap.put("GU", "12");
        countryAreaMap.put("GT", "6");
        countryAreaMap.put("GN", "8");
        countryAreaMap.put("GW", "8");
        countryAreaMap.put("GY", "7");
        countryAreaMap.put("HT", "2");
        countryAreaMap.put("HM", "12");
        countryAreaMap.put("HN", "6");
        countryAreaMap.put("HK", "4");
        countryAreaMap.put("HU", "3");
        countryAreaMap.put("IS", "3");
        countryAreaMap.put("IN", "9");
        countryAreaMap.put("ID", "4");
        countryAreaMap.put("IR", "5");
        countryAreaMap.put("IQ", "5");
        countryAreaMap.put("IE", "3");
        countryAreaMap.put("IL", "5");
        countryAreaMap.put("IT", "3");
        countryAreaMap.put("JM", "2");
        countryAreaMap.put("JP", "4");
        countryAreaMap.put("JO", "5");
        countryAreaMap.put("KZ", "3");
        countryAreaMap.put("KE", "8");
        countryAreaMap.put("KI", "12");
        countryAreaMap.put("KP", "4");
        countryAreaMap.put("KR", "4");
        countryAreaMap.put("KW", "5");
        countryAreaMap.put("KG", "12");
        countryAreaMap.put("LA", "4");
        countryAreaMap.put("LV", "3");
        countryAreaMap.put("LB", "5");
        countryAreaMap.put("LS", "8");
        countryAreaMap.put("LR", "8");
        countryAreaMap.put("LY", "8");
        countryAreaMap.put("LI", "3");
        countryAreaMap.put("LT", "3");
        countryAreaMap.put("LU", "3");
        countryAreaMap.put("MO", "4");
        countryAreaMap.put("MK", "3");
        countryAreaMap.put("MG", "8");
        countryAreaMap.put("MW", "8");
        countryAreaMap.put("MY", "4");
        countryAreaMap.put("MV", "12");
        countryAreaMap.put("ML", "8");
        countryAreaMap.put("MT", "3");
        countryAreaMap.put("MH", "12");
        countryAreaMap.put("MQ", "2");
        countryAreaMap.put("MR", "8");
        countryAreaMap.put("MU", "8");
        countryAreaMap.put("YT", "8");
        countryAreaMap.put("MX", "11");
        countryAreaMap.put("FM", "12");
        countryAreaMap.put("MD", "3");
        countryAreaMap.put("MC", "3");
        countryAreaMap.put("MN", "4");
        countryAreaMap.put("MS", "2");
        countryAreaMap.put("MA", "8");
        countryAreaMap.put("MZ", "8");
        countryAreaMap.put("MM", "4");
        countryAreaMap.put("NA", "8");
        countryAreaMap.put("NR", "12");
        countryAreaMap.put("NP", "12");
        countryAreaMap.put("NL", "3");
        countryAreaMap.put("AN", "12");
        countryAreaMap.put("NC", "12");
        countryAreaMap.put("NZ", "12");
        countryAreaMap.put("NI", "6");
        countryAreaMap.put("NE", "8");
        countryAreaMap.put("NG", "8");
        countryAreaMap.put("NU", "12");
        countryAreaMap.put("NF", "12");
        countryAreaMap.put("MP", "12");
        countryAreaMap.put("NO", "3");
        countryAreaMap.put("OM", "5");
        countryAreaMap.put("PK", "12");
        countryAreaMap.put("PW", "12");
        countryAreaMap.put("PA", "6");
        countryAreaMap.put("PG", "4");
        countryAreaMap.put("PY", "7");
        countryAreaMap.put("PE", "7");
        countryAreaMap.put("PH", "4");
        countryAreaMap.put("PN", "12");
        countryAreaMap.put("PL", "3");
        countryAreaMap.put("PT", "3");
        countryAreaMap.put("PR", "2");
        countryAreaMap.put("QA", "5");
        countryAreaMap.put("RE", "8");
        countryAreaMap.put("RO", "3");
        countryAreaMap.put("RU", "3");
        countryAreaMap.put("RW", "8");
        countryAreaMap.put("KN", "2");
        countryAreaMap.put("LC", "2");
        countryAreaMap.put("VC", "2");
        countryAreaMap.put("WS", "12");
        countryAreaMap.put("SM", "3");
        countryAreaMap.put("ST", "8");
        countryAreaMap.put("SA", "5");
        countryAreaMap.put("SN", "8");
        countryAreaMap.put("SC", "8");
        countryAreaMap.put("SL", "8");
        countryAreaMap.put("SG", "4");
        countryAreaMap.put("SK", "3");
        countryAreaMap.put("SI", "3");
        countryAreaMap.put("SB", "12");
        countryAreaMap.put("SO", "8");
        countryAreaMap.put("ZA", "8");
        countryAreaMap.put("GS", "7");
        countryAreaMap.put("ES", "3");
        countryAreaMap.put("LK", "12");
        countryAreaMap.put("SH", "8");
        countryAreaMap.put("PM", "12");
        countryAreaMap.put("SD", "8");
        countryAreaMap.put("SR", "7");
        countryAreaMap.put("SJ", "12");
        countryAreaMap.put("SZ", "8");
        countryAreaMap.put("SE", "3");
        countryAreaMap.put("CH", "3");
        countryAreaMap.put("SY", "5");
        countryAreaMap.put("TW", "4");
        countryAreaMap.put("TJ", "12");
        countryAreaMap.put("TZ", "8");
        countryAreaMap.put("TH", "4");
        countryAreaMap.put("TG", "8");
        countryAreaMap.put("TK", "12");
        countryAreaMap.put("TO", "12");
        countryAreaMap.put("TT", "2");
        countryAreaMap.put("TN", "8");
        countryAreaMap.put("TR", "5"); // todo: Turkey also located in european part, code 3
        countryAreaMap.put("TM", "12");
        countryAreaMap.put("TC", "2");
        countryAreaMap.put("TV", "12");
        countryAreaMap.put("UG", "8");
        countryAreaMap.put("UA", "3");
        countryAreaMap.put("AE", "5");
        countryAreaMap.put("GB", "3");
        countryAreaMap.put("US", "1");
        countryAreaMap.put("UM", "1");
        countryAreaMap.put("UY", "7");
        countryAreaMap.put("UZ", "12");
        countryAreaMap.put("VU", "12");
        countryAreaMap.put("VA", "3");
        countryAreaMap.put("VE", "7");
        countryAreaMap.put("VN", "4");
        countryAreaMap.put("VG", "2");
        countryAreaMap.put("VI", "2");
        countryAreaMap.put("WF", "12");
        countryAreaMap.put("EH", "8");
        countryAreaMap.put("YE", "5");
        countryAreaMap.put("ZR", "12");
        countryAreaMap.put("ZM", "8");
        countryAreaMap.put("ZW", "8");
        countryAreaMap.put("PS", "5");
        countryAreaMap.put("RS", "3");
        countryAreaMap.put("IM", "12");
        countryAreaMap.put("ME", "3");
        countryAreaMap.put("GG", "3");
        countryAreaMap.put("JE", "3");
        countryAreaMap.put("CW", "2");
        countryAreaMap.put("BL", "2");
    }
}
