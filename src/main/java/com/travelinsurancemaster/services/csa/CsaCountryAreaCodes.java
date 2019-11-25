package com.travelinsurancemaster.services.csa;

public enum CsaCountryAreaCodes {

    US("USA"),
    CR("Caribbean"),
    EU("Europe"),
    AS("Asia"),
    ME("Middle East"),
    CAM("Central America"),
    SA("South America"),
    AF("Africa"),
    CA("Canada"),
    MX("Mexico"),
    OT("Other"),
    AU("Australia"),
    AN("Antarctic"),
    PI("Pacific Islands");

    private String caption;

    CsaCountryAreaCodes(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }




}
