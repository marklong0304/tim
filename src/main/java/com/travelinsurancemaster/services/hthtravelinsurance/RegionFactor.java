package com.travelinsurancemaster.services.hthtravelinsurance;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ritchie on 7/1/15.
 */
public class RegionFactor {
    private static Map<String, Long> regionFactorMap = new HashMap<>();

    /**
     * @return region rate required by HTH vendor
     */
    public static Long getRegionRateByCountryCode(String countryCode) {
        Long rateFactor = regionFactorMap.get(countryCode);
        return rateFactor;
    }

    static {
        regionFactorMap.put("AF", 100l);
        regionFactorMap.put("AL", 96l); // USA, Canada & Europe
        regionFactorMap.put("DZ", 100l);
        regionFactorMap.put("AS", 100l);
        regionFactorMap.put("AD", 96l); // USA, Canada & Europe
        regionFactorMap.put("AO", 100l);
        regionFactorMap.put("AI", 94l); // Caribbean & Mexico
        regionFactorMap.put("AQ", 100l);
        regionFactorMap.put("AG", 94l); // Caribbean & Mexico
        regionFactorMap.put("AR", 100l);
        regionFactorMap.put("AM", 100l);
        regionFactorMap.put("AW", 94l); // Caribbean & Mexico
        regionFactorMap.put("AU", 100l);
        regionFactorMap.put("AT", 96l); // USA, Canada & Europe
        regionFactorMap.put("AZ", 100l);
        regionFactorMap.put("BS", 94l); // Caribbean & Mexico
        regionFactorMap.put("BH", 100l);
        regionFactorMap.put("BD", 100l);
        regionFactorMap.put("BB", 94l); // Caribbean & Mexico
        regionFactorMap.put("BY", 96l); // USA, Canada & Europe
        regionFactorMap.put("BE", 96l); // USA, Canada & Europe
        regionFactorMap.put("BZ", 100l);
        regionFactorMap.put("BJ", 100l);
        regionFactorMap.put("BM", 94l); // Caribbean & Mexico
        regionFactorMap.put("BT", 100l);
        regionFactorMap.put("BO", 100l);
        regionFactorMap.put("BA", 96l); // USA, Canada & Europe
        regionFactorMap.put("BW", 100l);
        regionFactorMap.put("BV", 100l);
        regionFactorMap.put("BR", 100l);
        regionFactorMap.put("IO", 100l);
        regionFactorMap.put("BN", 100l);
        regionFactorMap.put("BG", 96l); // USA, Canada & Europe
        regionFactorMap.put("BF", 100l);
        regionFactorMap.put("BI", 100l);
        regionFactorMap.put("KH", 100l);
        regionFactorMap.put("CM", 100l);
        regionFactorMap.put("CA", 96l); // USA, Canada & Europe
        regionFactorMap.put("CV", 100l);
        regionFactorMap.put("KY", 94l); // Caribbean & Mexico
        regionFactorMap.put("CF", 100l);
        regionFactorMap.put("TD", 100l);
        regionFactorMap.put("CL", 100l);
        regionFactorMap.put("CN", 100l);
        regionFactorMap.put("CX", 100l);
        regionFactorMap.put("CC", 100l);
        regionFactorMap.put("CO", 100l);
        regionFactorMap.put("KM", 100l);
        regionFactorMap.put("CG", 100l);
        regionFactorMap.put("CK", 100l);
        regionFactorMap.put("CR", 100l);
        regionFactorMap.put("CI", 100l);
        regionFactorMap.put("HR", 96l); // USA, Canada & Europe
        regionFactorMap.put("CU", 94l); // Caribbean & Mexico
        regionFactorMap.put("CY", 96l); // USA, Canada & Europe
        regionFactorMap.put("CZ", 96l); // USA, Canada & Europe
        regionFactorMap.put("DK", 96l); // USA, Canada & Europe
        regionFactorMap.put("DJ", 100l);
        regionFactorMap.put("DM", 94l); // Caribbean & Mexico
        regionFactorMap.put("DO", 94l); // Caribbean & Mexico
        regionFactorMap.put("TP", 100l);
        regionFactorMap.put("EC", 100l);
        regionFactorMap.put("EG", 100l);
        regionFactorMap.put("SV", 100l);
        regionFactorMap.put("GQ", 100l);
        regionFactorMap.put("ER", 100l);
        regionFactorMap.put("EE", 96l); // USA, Canada & Europe
        regionFactorMap.put("ET", 100l);
        regionFactorMap.put("FK", 100l);
        regionFactorMap.put("FO", 96l); // USA, Canada & Europe
        regionFactorMap.put("FJ", 100l);
        regionFactorMap.put("FI", 96l); // USA, Canada & Europe
        regionFactorMap.put("FR", 96l); // USA, Canada & Europe
        regionFactorMap.put("FX", 100l);
        regionFactorMap.put("GF", 100l);
        regionFactorMap.put("PF", 100l);
        regionFactorMap.put("TF", 100l);
        regionFactorMap.put("GA", 100l);
        regionFactorMap.put("GM", 100l);
        regionFactorMap.put("GE", 100l);
        regionFactorMap.put("DE", 96l); // USA, Canada & Europe
        regionFactorMap.put("GH", 100l);
        regionFactorMap.put("GI", 96l); // USA, Canada & Europe
        regionFactorMap.put("GR", 96l); // USA, Canada & Europe
        regionFactorMap.put("GL", 96l); // USA, Canada & Europe
        regionFactorMap.put("GD", 94l); // Caribbean & Mexico
        regionFactorMap.put("GP", 94l); // Caribbean & Mexico
        regionFactorMap.put("GU", 100l);
        regionFactorMap.put("GT", 100l);
        regionFactorMap.put("GG", 96l); // USA, Canada & Europe
        regionFactorMap.put("GN", 100l);
        regionFactorMap.put("GW", 100l);
        regionFactorMap.put("GY", 100l);
        regionFactorMap.put("HT", 94l); // Caribbean & Mexico
        regionFactorMap.put("HM", 100l);
        regionFactorMap.put("HN", 100l);
        regionFactorMap.put("HK", 100l);
        regionFactorMap.put("HU", 96l); // USA, Canada & Europe
        regionFactorMap.put("IS", 96l); // USA, Canada & Europe
        regionFactorMap.put("IN", 100l);
        regionFactorMap.put("ID", 100l);
        regionFactorMap.put("IR", 100l);
        regionFactorMap.put("IQ", 100l);
        regionFactorMap.put("IE", 96l); // USA, Canada & Europe
        regionFactorMap.put("IL", 100l);
        regionFactorMap.put("IT", 96l); // USA, Canada & Europe
        regionFactorMap.put("JM", 94l); // Caribbean & Mexico
        regionFactorMap.put("JE", 96l); // USA, Canada & Europe
        regionFactorMap.put("JP", 100l);
        regionFactorMap.put("JO", 100l);
        regionFactorMap.put("KZ", 100l);
        regionFactorMap.put("KE", 100l);
        regionFactorMap.put("KI", 100l);
        regionFactorMap.put("KP", 100l);
        regionFactorMap.put("KR", 100l);
        regionFactorMap.put("KW", 100l);
        regionFactorMap.put("KG", 100l);
        regionFactorMap.put("LA", 100l);
        regionFactorMap.put("LV", 96l); // USA, Canada & Europe
        regionFactorMap.put("LB", 100l);
        regionFactorMap.put("LS", 100l);
        regionFactorMap.put("LR", 100l);
        regionFactorMap.put("LY", 100l);
        regionFactorMap.put("LI", 96l); // USA, Canada & Europe
        regionFactorMap.put("LT", 96l); // USA, Canada & Europe
        regionFactorMap.put("LU", 96l); // USA, Canada & Europe
        regionFactorMap.put("MO", 100l);
        regionFactorMap.put("MK", 96l); // USA, Canada & Europe
        regionFactorMap.put("MG", 100l);
        regionFactorMap.put("MW", 100l);
        regionFactorMap.put("MY", 100l);
        regionFactorMap.put("MV", 100l);
        regionFactorMap.put("ML", 100l);
        regionFactorMap.put("MT", 96l); // USA, Canada & Europe
        regionFactorMap.put("MH", 100l);
        regionFactorMap.put("MQ", 94l); // Caribbean & Mexico
        regionFactorMap.put("MR", 100l);
        regionFactorMap.put("MU", 100l);
        regionFactorMap.put("YT", 100l);
        regionFactorMap.put("MX", 94l); // Caribbean & Mexico
        regionFactorMap.put("FM", 100l);
        regionFactorMap.put("MD", 96l); // USA, Canada & Europe
        regionFactorMap.put("MC", 96l); // USA, Canada & Europe
        regionFactorMap.put("MN", 100l);
        regionFactorMap.put("MS", 94l); // Caribbean & Mexico
        regionFactorMap.put("MA", 100l);
        regionFactorMap.put("MZ", 100l);
        regionFactorMap.put("MM", 100l);
        regionFactorMap.put("NA", 100l);
        regionFactorMap.put("NR", 100l);
        regionFactorMap.put("NP", 100l);
        regionFactorMap.put("NL", 96l); // USA, Canada & Europe
        regionFactorMap.put("AN", 100l);
        regionFactorMap.put("NC", 100l);
        regionFactorMap.put("NZ", 100l);
        regionFactorMap.put("NI", 100l);
        regionFactorMap.put("NE", 100l);
        regionFactorMap.put("NG", 100l);
        regionFactorMap.put("NU", 100l);
        regionFactorMap.put("NF", 100l);
        regionFactorMap.put("MP", 100l);
        regionFactorMap.put("NO", 96l); // USA, Canada & Europe
        regionFactorMap.put("OM", 100l);
        regionFactorMap.put("PK", 100l);
        regionFactorMap.put("PW", 100l);
        regionFactorMap.put("PA", 100l);
        regionFactorMap.put("PG", 100l);
        regionFactorMap.put("PY", 100l);
        regionFactorMap.put("PE", 100l);
        regionFactorMap.put("PH", 100l);
        regionFactorMap.put("PN", 100l);
        regionFactorMap.put("PL", 96l); // USA, Canada & Europe
        regionFactorMap.put("PT", 96l); // USA, Canada & Europe
        regionFactorMap.put("PR", 94l); // Caribbean & Mexico
        regionFactorMap.put("QA", 100l);
        regionFactorMap.put("RE", 100l);
        regionFactorMap.put("RO", 96l); // USA, Canada & Europe
        regionFactorMap.put("RU", 96l); // USA, Canada & Europe
        regionFactorMap.put("RW", 100l);
        regionFactorMap.put("KN", 94l); // Caribbean & Mexico
        regionFactorMap.put("LC", 94l); // Caribbean & Mexico
        regionFactorMap.put("VC", 94l); // Caribbean & Mexico
        regionFactorMap.put("WS", 100l);
        regionFactorMap.put("SM", 96l); // USA, Canada & Europe
        regionFactorMap.put("ST", 100l);
        regionFactorMap.put("SA", 100l);
        regionFactorMap.put("SN", 100l);
        regionFactorMap.put("SC", 100l);
        regionFactorMap.put("SL", 100l);
        regionFactorMap.put("SG", 100l);
        regionFactorMap.put("SK", 96l); // USA, Canada & Europe
        regionFactorMap.put("SI", 96l); // USA, Canada & Europe
        regionFactorMap.put("SB", 100l);
        regionFactorMap.put("SO", 100l);
        regionFactorMap.put("ZA", 100l);
        regionFactorMap.put("GS", 100l);
        regionFactorMap.put("ES", 96l); // USA, Canada & Europe
        regionFactorMap.put("LK", 100l);
        regionFactorMap.put("SH", 100l);
        regionFactorMap.put("PM", 96l); // USA, Canada & Europe
        regionFactorMap.put("SD", 100l);
        regionFactorMap.put("SR", 100l);
        regionFactorMap.put("SJ", 96l); // USA, Canada & Europe
        regionFactorMap.put("SZ", 100l);
        regionFactorMap.put("SE", 96l); // USA, Canada & Europe
        regionFactorMap.put("CH", 96l); // USA, Canada & Europe
        regionFactorMap.put("SY", 100l);
        regionFactorMap.put("TW", 100l);
        regionFactorMap.put("TJ", 100l);
        regionFactorMap.put("TZ", 100l);
        regionFactorMap.put("TH", 100l);
        regionFactorMap.put("TG", 100l);
        regionFactorMap.put("TK", 100l);
        regionFactorMap.put("TO", 100l);
        regionFactorMap.put("TT", 94l); // Caribbean & Mexico
        regionFactorMap.put("TN", 100l);
        regionFactorMap.put("TR", 96l); // USA, Canada & Europe
        regionFactorMap.put("TM", 100l);
        regionFactorMap.put("TC", 94l); // Caribbean & Mexico
        regionFactorMap.put("TV", 100l);
        regionFactorMap.put("UG", 100l);
        regionFactorMap.put("UA", 96l); // USA, Canada & Europe
        regionFactorMap.put("AE", 100l);
        regionFactorMap.put("GB", 96l); // USA, Canada & Europe
        regionFactorMap.put("US", 96l); // USA, Canada & Europe
        regionFactorMap.put("UM", 94l); // Caribbean & Mexico
        regionFactorMap.put("UY", 100l);
        regionFactorMap.put("UZ", 100l);
        regionFactorMap.put("VU", 100l);
        regionFactorMap.put("VA", 96l); // USA, Canada & Europe
        regionFactorMap.put("VE", 100l);
        regionFactorMap.put("VN", 100l);
        regionFactorMap.put("VG", 94l); // Caribbean & Mexico
        regionFactorMap.put("VI", 94l); // Caribbean & Mexico
        regionFactorMap.put("WF", 100l);
        regionFactorMap.put("EH", 100l);
        regionFactorMap.put("YE", 100l);
        regionFactorMap.put("ZR", 100l);
        regionFactorMap.put("ZM", 100l);
        regionFactorMap.put("ZW", 100l);
        regionFactorMap.put("PS", 100l);
        regionFactorMap.put("RS", 96l); // USA, Canada & Europe
        regionFactorMap.put("IM", 96l); // USA, Canada & Europe
        regionFactorMap.put("ME", 96l); // USA, Canada & Europe
        regionFactorMap.put("CW", 94l); // Caribbean & Mexico
        regionFactorMap.put("BL", 94l); // Caribbean & Mexico
    }
}
