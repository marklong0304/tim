package com.travelinsurancemaster.model.dto;


/**
 * Created by artur.chernov
 */
public enum TermsAndConditionsType {
    LINK("Link"),
    TEXT("Text");

    private String caption;

    TermsAndConditionsType(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
}
