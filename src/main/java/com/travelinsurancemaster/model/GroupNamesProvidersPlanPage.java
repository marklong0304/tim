package com.travelinsurancemaster.model;

/**
 * Created by ritchie on 12/1/15.
 */
public enum GroupNamesProvidersPlanPage {
    GENERAL_PLAN_INFO("general-plan-info"),
    TRIP_CANCELLATION("trip-cancellation"),
    TRAVEL_MEDICAL("travel-medical"),
    LOSS_OR_DELAY("loss-or-delay"),
    MORE_BENEFITS("more-benefits");

    private final String code;

    GroupNamesProvidersPlanPage(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
