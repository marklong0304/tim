package com.travelinsurancemaster.services.csa;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aborkov on 4/27/19
 */
public class CsaCountryArea {
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
         *
         1 – USA
         2 – Caribbean
         3 – Europe
         4 – Asia
         5 – Middle East
         6 – Central America
         7 – South America
         8 – Africa

         10 – Canada
         11 – Mexico
         12 – Other
         13 - Australia
         14 - Antarctic
         15 - Pacific Islands
         */

        countryAreaMap.put("AF", CsaCountryAreaCodes.OT.getCaption());
        countryAreaMap.put("AL", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("DZ", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("AS", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("AD", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("AO", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("AI", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("AQ", CsaCountryAreaCodes.AN.getCaption());
        countryAreaMap.put("AG", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("AR", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("AM", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("AW", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("AU", CsaCountryAreaCodes.AU.getCaption());
        countryAreaMap.put("AT", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("AZ", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("BS", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("BH", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("BD", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("BB", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("BY", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("BE", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("BZ", CsaCountryAreaCodes.CAM.getCaption());
        countryAreaMap.put("BJ", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("BM", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("BT", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("BO", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("BA", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("BW", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("BV", CsaCountryAreaCodes.OT.getCaption());
        countryAreaMap.put("BR", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("IO", CsaCountryAreaCodes.OT.getCaption());
        countryAreaMap.put("BN", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("BG", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("BF", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("BI", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("KH", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("CM", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("CA", CsaCountryAreaCodes.CA.getCaption());
        countryAreaMap.put("CV", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("KY", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("CF", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("TD", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("CL", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("CN", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("CX", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("CC", CsaCountryAreaCodes.OT.getCaption());
        countryAreaMap.put("CO", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("KM", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("CG", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("CK", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("CR", CsaCountryAreaCodes.CAM.getCaption());
        countryAreaMap.put("CI", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("HR", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("CU", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("CY", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("CZ", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("DK", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("DJ", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("DM", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("DO", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("TP", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("EC", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("EG", CsaCountryAreaCodes.AF.getCaption()); // todo: Egypt is also located in the middle east, code 5
        countryAreaMap.put("SV", CsaCountryAreaCodes.CAM.getCaption());
        countryAreaMap.put("GQ", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("ER", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("EE", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("ET", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("FK", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("FO", CsaCountryAreaCodes.OT.getCaption());
        countryAreaMap.put("FJ", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("FI", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("FR", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("FX", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("GF", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("PF", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("TF", CsaCountryAreaCodes.OT.getCaption());
        countryAreaMap.put("GA", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("GM", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("GE", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("DE", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("GH", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("GI", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("GR", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("GL", CsaCountryAreaCodes.OT.getCaption());
        countryAreaMap.put("GD", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("GP", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("GU", CsaCountryAreaCodes.OT.getCaption());
        countryAreaMap.put("GT", CsaCountryAreaCodes.CAM.getCaption());
        countryAreaMap.put("GN", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("GW", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("GY", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("HT", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("HM", CsaCountryAreaCodes.AN.getCaption());
        countryAreaMap.put("HN", CsaCountryAreaCodes.CAM.getCaption());
        countryAreaMap.put("HK", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("HU", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("IS", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("IN", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("ID", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("IR", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("IQ", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("IE", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("IL", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("IT", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("JM", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("JP", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("JO", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("KZ", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("KE", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("KI", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("KP", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("KR", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("KW", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("KG", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("LA", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("LV", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("LB", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("LS", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("LR", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("LY", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("LI", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("LT", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("LU", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("MO", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("MK", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("MG", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("MW", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("MY", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("MV", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("ML", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("MT", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("MH", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("MQ", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("MR", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("MU", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("YT", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("MX", CsaCountryAreaCodes.MX.getCaption());
        countryAreaMap.put("FM", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("MD", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("MC", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("MN", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("MS", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("MA", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("MZ", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("MM", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("NA", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("NR", CsaCountryAreaCodes.OT.getCaption());
        countryAreaMap.put("NP", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("NL", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("AN", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("NC", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("NZ", CsaCountryAreaCodes.AU.getCaption());
        countryAreaMap.put("NI", CsaCountryAreaCodes.CAM.getCaption());
        countryAreaMap.put("NE", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("NG", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("NU", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("NF", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("MP", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("NO", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("OM", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("PK", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("PW", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("PA", CsaCountryAreaCodes.CAM.getCaption());
        countryAreaMap.put("PG", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("PY", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("PE", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("PH", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("PN", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("PL", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("PT", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("PR", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("QA", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("RE", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("RO", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("RU", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("RW", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("KN", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("LC", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("VC", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("WS", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("SM", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("ST", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("SA", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("SN", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("SC", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("SL", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("SG", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("SK", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("SI", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("SB", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("SO", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("ZA", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("GS", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("ES", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("LK", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("SH", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("PM", CsaCountryAreaCodes.OT.getCaption());
        countryAreaMap.put("SD", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("SR", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("SJ", CsaCountryAreaCodes.OT.getCaption());
        countryAreaMap.put("SZ", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("SE", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("CH", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("SY", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("TW", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("TJ", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("TZ", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("TH", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("TG", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("TK", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("TO", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("TT", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("TN", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("TR", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("TM", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("TC", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("TV", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("UG", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("UA", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("AE", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("GB", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("US", CsaCountryAreaCodes.US.getCaption());
        countryAreaMap.put("UM", CsaCountryAreaCodes.US.getCaption());
        countryAreaMap.put("UY", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("UZ", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("VU", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("VA", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("VE", CsaCountryAreaCodes.SA.getCaption());
        countryAreaMap.put("VN", CsaCountryAreaCodes.AS.getCaption());
        countryAreaMap.put("VG", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("VI", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("WF", CsaCountryAreaCodes.PI.getCaption());
        countryAreaMap.put("EH", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("YE", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("ZR", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("ZM", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("ZW", CsaCountryAreaCodes.AF.getCaption());
        countryAreaMap.put("PS", CsaCountryAreaCodes.ME.getCaption());
        countryAreaMap.put("RS", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("IM", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("ME", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("GG", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("JE", CsaCountryAreaCodes.EU.getCaption());
        countryAreaMap.put("CW", CsaCountryAreaCodes.CR.getCaption());
        countryAreaMap.put("BL", CsaCountryAreaCodes.CR.getCaption());
    }
}
