package com.travelinsurancemaster.model.dto.report.sales;

/**
 * Created by Chernov Artur on 01.10.15.
 */
public enum ReportInterval {
    DAILY("Daily"),
    MONTHLY("Monthly"),
    ANNUALLY("Annually");

    private final String caption;

    ReportInterval(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
}
