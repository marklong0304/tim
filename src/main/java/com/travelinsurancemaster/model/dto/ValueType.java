package com.travelinsurancemaster.model.dto;

/**
 * @author Alexander.Isaenco
 */
public enum ValueType {
    FIX("$", "Dollars"), PERCENT("%", "Percent"), NAN("", "Other");

    private String shortCaption;
    private String caption;

    ValueType(String shortCaption, String caption) {
        this.shortCaption = shortCaption;
        this.caption = caption;
    }

    public String getShortCaption() {
        return shortCaption;
    }

    public String getCaption() {
        return caption;
    }
}
