package com.travelinsurancemaster.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vlad on 13.02.2015.
 */
//TODO move to CSA Util
public final class CountryCodes {

    private static Map<String, String> countryMap = new HashMap<>();

    private CountryCodes() {
    }

    public static String getCountryByCode(String countryName) {
        String code = countryMap.get(countryName);
        return code != null ? code : "";
    }

    static {
        countryMap.put("AF", "AFGHANISTAN");
        countryMap.put("AL", "ALBANIA");
        countryMap.put("DZ", "ALGERIA");
        countryMap.put("AS", "AMERICAN SAMOA");
        countryMap.put("AD", "ANDORRA");
        countryMap.put("AO", "ANGOLA");
        countryMap.put("AI", "ANGUILLA");
        countryMap.put("AQ", "ANTARCTICA");
        countryMap.put("AG", "ANTIGUA AND BARBUDA");
        countryMap.put("AR", "ARGENTINA");
        countryMap.put("AM", "ARMENIA");
        countryMap.put("AW", "ARUBA");
        countryMap.put("AU", "AUSTRALIA");
        countryMap.put("AT", "AUSTRIA");
        countryMap.put("AZ", "AZERBAIJAN");
        countryMap.put("BS", "BAHAMAS");
        countryMap.put("BH", "BAHRAIN");
        countryMap.put("BD", "BANGLADESH");
        countryMap.put("BB", "BARBADOS");
        countryMap.put("BY", "BELARUS");
        countryMap.put("BE", "BELGIUM");
        countryMap.put("BZ", "BELIZE");
        countryMap.put("BJ", "BENIN");
        countryMap.put("BM", "BERMUDA");
        countryMap.put("BT", "BHUTAN");
        countryMap.put("BO", "BOLIVIA");
        countryMap.put("BA", "BOSNIA AND HERZEGOWINA");
        countryMap.put("BW", "BOTSWANA");
        countryMap.put("BV", "BOUVET ISLAND");
        countryMap.put("BR", "BRAZIL");
        countryMap.put("IO", "BRITISH INDIAN OCEAN TERRITORY");
        countryMap.put("BN", "BRUNEI DARUSSALAM");
        countryMap.put("BG", "BULGARIA");
        countryMap.put("BF", "BURKINA FASO");
        countryMap.put("BI", "BURUNDI");
        countryMap.put("KH", "CAMBODIA");
        countryMap.put("CM", "CAMEROON");
        countryMap.put("CA", "CANADA");
        countryMap.put("CV", "CAPE VERDE");
        countryMap.put("KY", "CAYMAN ISLANDS");
        countryMap.put("CF", "CENTRAL AFRICAN REPUBLIC");
        countryMap.put("TD", "CHAD");
        countryMap.put("CL", "CHILE");
        countryMap.put("CN", "CHINA");
        countryMap.put("CX", "CHRISTMAS ISLAND");
        countryMap.put("CC", "COCOS (KEELING) ISLANDS");
        countryMap.put("CO", "COLOMBIA");
        countryMap.put("KM", "COMOROS");
        countryMap.put("CG", "CONGO");
        countryMap.put("CK", "COOK ISLANDS");
        countryMap.put("CR", "COSTA RICA");
        countryMap.put("CI", "COTE D'IVOIRE");
        countryMap.put("HR", "CROATIA (local name: Hrvatska)");
        countryMap.put("CU", "CUBA");
        countryMap.put("CY", "CYPRUS");
        countryMap.put("CZ", "CZECH REPUBLIC");
        countryMap.put("DK", "DENMARK");
        countryMap.put("DJ", "DJIBOUTI");
        countryMap.put("DM", "DOMINICA");
        countryMap.put("DO", "DOMINICAN REPUBLIC");
        countryMap.put("TP", "EAST TIMOR");
        countryMap.put("EC", "ECUADOR");
        countryMap.put("EG", "EGYPT");
        countryMap.put("SV", "EL SALVADOR");
        countryMap.put("GQ", "EQUATORIAL GUINEA");
        countryMap.put("ER", "ERITREA");
        countryMap.put("EE", "ESTONIA");
        countryMap.put("ET", "ETHIOPIA");
        countryMap.put("FK", "FALKLAND ISLANDS (MALVINAS)");
        countryMap.put("FO", "FAROE ISLANDS");
        countryMap.put("FJ", "FIJI");
        countryMap.put("FI", "FINLAND");
        countryMap.put("FR", "FRANCE");
        countryMap.put("FX", "FRANCE, METROPOLITAN");
        countryMap.put("GF", "FRENCH GUIANA");
        countryMap.put("PF", "FRENCH POLYNESIA");
        countryMap.put("TF", "FRENCH SOUTHERN TERRITORIES");
        countryMap.put("GA", "GABON");
        countryMap.put("GM", "GAMBIA");
        countryMap.put("GE", "GEORGIA");
        countryMap.put("DE", "GERMANY");
        countryMap.put("GH", "GHANA");
        countryMap.put("GI", "GIBRALTAR");
        countryMap.put("GR", "GREECE");
        countryMap.put("GL", "GREENLAND");
        countryMap.put("GD", "GRENADA");
        countryMap.put("GP", "GUADELOUPE");
        countryMap.put("GU", "GUAM");
        countryMap.put("GT", "GUATEMALA");
        countryMap.put("GN", "GUINEA");
        countryMap.put("GW", "GUINEA-BISSAU");
        countryMap.put("GY", "GUYANA");
        countryMap.put("HT", "HAITI");
        countryMap.put("HM", "HEARD AND MC DONALD ISLANDS");
        countryMap.put("HN", "HONDURAS");
        countryMap.put("HK", "HONG KONG");
        countryMap.put("HU", "HUNGARY");
        countryMap.put("IS", "ICELAND");
        countryMap.put("IN", "INDIA");
        countryMap.put("ID", "INDONESIA");
        countryMap.put("IR", "IRAN (ISLAMIC REPUBLIC OF)");
        countryMap.put("IQ", "IRAQ");
        countryMap.put("IE", "IRELAND");
        countryMap.put("IL", "ISRAEL");
        countryMap.put("IT", "ITALY");
        countryMap.put("JM", "JAMAICA");
        countryMap.put("JP", "JAPAN");
        countryMap.put("JO", "JORDAN");
        countryMap.put("KZ", "KAZAKHSTAN");
        countryMap.put("KE", "KENYA");
        countryMap.put("KI", "KIRIBATI");
        countryMap.put("KP", "KOREA, DEMOCRATIC PEOPLES REPUBLIC OF");
        countryMap.put("KR", "KOREA, REPUBLIC OF");
        countryMap.put("KW", "KUWAIT");
        countryMap.put("KG", "KYRGYZSTAN");
        countryMap.put("LA", "LAO PEOPLE'S DEMOCRATIC REPUBLIC");
        countryMap.put("LV", "LATVIA");
        countryMap.put("LB", "LEBANON");
        countryMap.put("LS", "LESOTHO");
        countryMap.put("LR", "LIBERIA");
        countryMap.put("LY", "LIBYAN ARAB JAMAHIRIYA");
        countryMap.put("LI", "LIECHTENSTEIN");
        countryMap.put("LT", "LITHUANIA");
        countryMap.put("LU", "LUXEMBOURG");
        countryMap.put("MO", "MACAU");
        countryMap.put("MK", "MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF");
        countryMap.put("MG", "MADAGASCAR");
        countryMap.put("MW", "MALAWI");
        countryMap.put("MY", "MALAYSIA");
        countryMap.put("MV", "MALDIVES");
        countryMap.put("ML", "MALI");
        countryMap.put("MT", "MALTA");
        countryMap.put("MH", "MARSHALL ISLANDS");
        countryMap.put("MQ", "MARTINIQUE");
        countryMap.put("MR", "MAURITANIA");
        countryMap.put("MU", "MAURITIUS");
        countryMap.put("YT", "MAYOTTE");
        countryMap.put("MX", "MEXICO");
        countryMap.put("FM", "MICRONESIA, FEDERATED STATES OF");
        countryMap.put("MD", "MOLDOVA, REPUBLIC OF");
        countryMap.put("MC", "MONACO");
        countryMap.put("MN", "MONGOLIA");
        countryMap.put("MS", "MONTSERRAT");
        countryMap.put("MA", "MOROCCO");
        countryMap.put("MZ", "MOZAMBIQUE");
        countryMap.put("MM", "MYANMAR");
        countryMap.put("NA", "NAMIBIA");
        countryMap.put("NR", "NAURU");
        countryMap.put("NP", "NEPAL");
        countryMap.put("NL", "NETHERLANDS");
        countryMap.put("AN", "NETHERLANDS ANTILLES");
        countryMap.put("NC", "NEW CALEDONIA");
        countryMap.put("NZ", "NEW ZEALAND");
        countryMap.put("NI", "NICARAGUA");
        countryMap.put("NE", "NIGER");
        countryMap.put("NG", "NIGERIA");
        countryMap.put("NU", "NIUE");
        countryMap.put("NF", "NORFOLK ISLAND");
        countryMap.put("MP", "NORTHERN MARIANA ISLANDS");
        countryMap.put("NO", "NORWAY");
        countryMap.put("OM", "OMAN");
        countryMap.put("PK", "PAKISTAN");
        countryMap.put("PW", "PALAU");
        countryMap.put("PA", "PANAMA");
        countryMap.put("PG", "PAPUA NEW GUINEA");
        countryMap.put("PY", "PARAGUAY");
        countryMap.put("PE", "PERU");
        countryMap.put("PH", "PHILIPPINES");
        countryMap.put("PN", "PITCAIRN");
        countryMap.put("PL", "POLAND");
        countryMap.put("PT", "PORTUGAL");
        countryMap.put("PR", "PUERTO RICO");
        countryMap.put("QA", "QATAR");
        countryMap.put("RE", "REUNION");
        countryMap.put("RO", "ROMANIA");
        countryMap.put("RU", "RUSSIAN FEDERATION");
        countryMap.put("RW", "RWANDA");
        countryMap.put("KN", "SAINT KITTS AND NEVIS");
        countryMap.put("LC", "SAINT LUCIA");
        countryMap.put("VC", "SAINT VINCENT AND THE GRENADINES");
        countryMap.put("WS", "SAMOA");
        countryMap.put("SM", "SAN MARINO");
        countryMap.put("ST", "SAO TOME AND PRINCIPE");
        countryMap.put("SA", "SAUDI ARABIA");
        countryMap.put("SN", "SENEGAL");
        countryMap.put("SC", "SEYCHELLES");
        countryMap.put("SL", "SIERRA LEONE");
        countryMap.put("SG", "SINGAPORE");
        countryMap.put("SK", "SLOVAKIA (Slovak Republic)");
        countryMap.put("SI", "SLOVENIA");
        countryMap.put("SB", "SOLOMON ISLANDS");
        countryMap.put("SO", "SOMALIA");
        countryMap.put("ZA", "SOUTH AFRICA");
        countryMap.put("ES", "SPAIN");
        countryMap.put("LK", "SRI LANKA");
        countryMap.put("SH", "ST. HELENA");
        countryMap.put("PM", "ST. PIERRE AND MIQUELON");
        countryMap.put("SD", "SUDAN");
        countryMap.put("SR", "SURINAME");
        countryMap.put("SJ", "SVALBARD AND JAN MAYEN ISLANDS");
        countryMap.put("SZ", "SWAZILAND");
        countryMap.put("SE", "SWEDEN");
        countryMap.put("CH", "SWITZERLAND");
        countryMap.put("SY", "SYRIAN ARAB REPUBLIC");
        countryMap.put("TW", "TAIWAN, PROVINCE OF CHINA");
        countryMap.put("TJ", "TAJIKISTAN");
        countryMap.put("TZ", "TANZANIA, UNITED REPUBLIC OF");
        countryMap.put("TH", "THAILAND");
        countryMap.put("TG", "TOGO");
        countryMap.put("TK", "TOKELAU");
        countryMap.put("TO", "TONGA");
        countryMap.put("TT", "TRINIDAD AND TOBAGO");
        countryMap.put("TN", "TUNISIA");
        countryMap.put("TR", "TURKEY");
        countryMap.put("TM", "TURKMENISTAN");
        countryMap.put("TC", "TURKS AND CAICOS ISLANDS");
        countryMap.put("TV", "TUVALU");
        countryMap.put("UG", "UGANDA");
        countryMap.put("UA", "UKRAINE");
        countryMap.put("AE", "UNITED ARAB EMIRATES");
        countryMap.put("GB", "UNITED KINGDOM");
        countryMap.put("US", "UNITED STATES");
        countryMap.put("UM", "UNITED STATES MINOR OUTLYING ISLANDS");
        countryMap.put("UY", "URUGUAY");
        countryMap.put("UZ", "UZBEKISTAN");
        countryMap.put("VU", "VANUATU");
        countryMap.put("VA", "VATICAN CITY STATE (HOLY SEE)");
        countryMap.put("VE", "VENEZUELA");
        countryMap.put("VN", "VIET NAM");
        countryMap.put("VG", "VIRGIN ISLANDS (BRITISH)");
        countryMap.put("VI", "VIRGIN ISLANDS (U.S.)");
        countryMap.put("WF", "WALLIS AND FUTUNA ISLANDS");
        countryMap.put("EH", "WESTERN SAHARA");
        countryMap.put("YE", "YEMEN");
        countryMap.put("ZR", "ZAIRE");
        countryMap.put("ZM", "ZAMBIA");
        countryMap.put("ZW", "ZIMBABWE");
        countryMap.put("GS", "SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS");
        countryMap.put("PS", "PALESTINE");
        countryMap.put("RS", "SERBIA");
        countryMap.put("IM", "ISLE OF MAN");
        countryMap.put("ME", "MONTENEGRO");
    }

}
