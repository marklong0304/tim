package com.travelinsurancemaster.model.webservice.bhtravelprotection;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by maleev on 12.05.2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BHTPCoverage {

    private String ratingId;
    private Integer coverageLimit;
    private Integer daySpan;

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public Integer getCoverageLimit() {
        return coverageLimit;
    }

    public void setCoverageLimit(Integer coverageLimit) {
        this.coverageLimit = coverageLimit;
    }

    public Integer getDaySpan() {
        return daySpan;
    }

    public void setDaySpan(Integer daySpan) {
        this.daySpan = daySpan;
    }
}
