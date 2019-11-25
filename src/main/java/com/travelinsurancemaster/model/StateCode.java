package com.travelinsurancemaster.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander.Isaenco
 */
public enum StateCode {

    AA("Armed Forces Americas", CountryCode.US),
    AB("Alberta", CountryCode.CA),
    AE("Armed Forces Other", CountryCode.US),
    AK("Alaska", CountryCode.US),
    AL("Alabama", CountryCode.US),
    AP("Armed Forces Pacific", CountryCode.US),
    AR("Arkansas", CountryCode.US),
    AZ("Arizona", CountryCode.US),
    BC("British Columbia", CountryCode.CA),
    CA("California", CountryCode.US),
    CO("Colorado", CountryCode.US),
    CT("Connecticut", CountryCode.US),
    DC("District of Columbia", CountryCode.US),
    DE("Delaware", CountryCode.US),
    FL("Florida", CountryCode.US),
    GA("Georgia", CountryCode.US),
    HI("Hawaii", CountryCode.US),
    IA("Iowa", CountryCode.US),
    ID("Idaho", CountryCode.US),
    IL("Illinois", CountryCode.US),
    IN("Indiana", CountryCode.US),
    IT("International", CountryCode.US),
    KS("Kansas", CountryCode.US),
    KY("Kentucky", CountryCode.US),
    LA("Louisiana", CountryCode.US),
    MA("Massachusetts", CountryCode.US),
    MB("Manitoba", CountryCode.CA),
    MD("Maryland", CountryCode.US),
    ME("Maine", CountryCode.US),
    MI("Michigan", CountryCode.US),
    MN("Minnesota", CountryCode.US),
    MO("Missouri", CountryCode.US),
    MS("Mississippi", CountryCode.US),
    MT("Montana", CountryCode.US),
    NB("New Brunswick", CountryCode.CA),
    NC("North Carolina", CountryCode.US),
    ND("North Dakota", CountryCode.US),
    NE("Nebraska", CountryCode.US),
    NH("New Hampshire", CountryCode.US),
    NJ("New Jersey", CountryCode.US),
    NL("Newfoundland And Labrador", CountryCode.CA),
    NM("New Mexico", CountryCode.US),
    NS("Nova Scotia", CountryCode.CA),
    NT("Northwest Territories", CountryCode.CA),
    NU("Nunavut", CountryCode.CA),
    NV("Nevada", CountryCode.US),
    NY("New York", CountryCode.US),
    OH("Ohio", CountryCode.US),
    OK("Oklahoma", CountryCode.US),
    ON("Ontario", CountryCode.CA),
    OR("Oregon", CountryCode.US),
    PA("Pennsylvania", CountryCode.US),
    PE("Prince Edward Island", CountryCode.CA),
    QC("Québec", CountryCode.CA),
    RI("Rhode Island", CountryCode.US),
    SC("South Carolina", CountryCode.US),
    SD("South Dakota", CountryCode.US),
    SK("Saskatchewan", CountryCode.CA),
    TN("Tennessee", CountryCode.US),
    TX("Texas", CountryCode.US),
    UT("Utah", CountryCode.US),
    VA("Virginia", CountryCode.US),
    VT("Vermont", CountryCode.US),
    WA("Washington", CountryCode.US),
    WI("Wisconsin", CountryCode.US),
    WV("West Virginia", CountryCode.US),
    WY("Wyoming", CountryCode.US),
    YT("Yukon", CountryCode.CA),
    PR("Puerto Rico", CountryCode.US);

    private String caption;
    private CountryCode countryCode;

    StateCode(String caption, CountryCode countryCode) {
        this.caption = caption;
        this.countryCode = countryCode;
    }

    public String getCaption() {
        return caption;
    }

    public CountryCode getCountryCode() {
        return countryCode;
    }

    private static List<StateCode> stateCodesUS;
    private static List<StateCode> stateCodesCA;

    static {
        stateCodesUS = new ArrayList<>();
        stateCodesCA = new ArrayList<>();
        for (StateCode stateCode : values()) {
            switch (stateCode.countryCode) {
                case US:
                    stateCodesUS.add(stateCode);
                    break;
                case CA:
                    stateCodesCA.add(stateCode);
                    break;
            }
        }
    }

    public static List<StateCode> getStatesUS() {
        return stateCodesUS;
    }

    public static List<StateCode> getStatesCanada() {
        return stateCodesCA;
    }

}
