package com.travelinsurancemaster.model.webservice.travelinsure;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TIRaterPlan {

    private String planName;
    private String tier;
//    private Date tripStartDate;
//    private Date tripEndDate;
    private String tripStartDate;
    private String tripEndDate;
    private Double premium;
    private String paymentFrequency;
    private List<TIExtraAttribute> extraAttributes = new LinkedList<TIExtraAttribute>();

    public TIRaterPlan() {
    }

    public String getPlanName() {
        return planName;
    }
    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getTier() {
        return tier;
    }
    public void setTier(String tier) {
        this.tier = tier;
    }

    public void setTripStartDate(String tripStartDate) {
        this.tripStartDate = tripStartDate;
    }
    public void setTripEndDate(String tripEndDate) {
        this.tripEndDate = tripEndDate;
    }

    public String getTripStartDate() {
        return tripStartDate;
    }
    public String getTripEndDate() {
        return tripEndDate;
    }

    public Double getPremium() {
        return premium;
    }
    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }
    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public List<TIExtraAttribute> getExtraAttributes() {
        return extraAttributes;
    }
    public void setExtraAttributes(List<TIExtraAttribute> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }
}