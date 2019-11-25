package com.travelinsurancemaster.model.webservice.common;

/**
 * Created by ritchie on 1/18/16.
 */
public enum BeneficiaryType {
    NOT_SUPPORTED ("Not supported"),
    SINGLE_BENEFICIARY ("Single beneficiary"),
    BENEFICIARY_PER_TRAVELER ("Beneficiary per traveler"),
    SINGLE_BENEFICIARY_OPTIONAL ("Single beneficiary (Optional)"),
    BENEFICIARY_PER_TRAVELER_OPTIONAL ("Beneficiary per traveler (Optional)");

    private final String caption;

    BeneficiaryType(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
}
