package com.travelinsurancemaster.model.webservice.travelinsure;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TIRaterRequest {

    private TIRaterPlan raterPlan;
    private String finalPaymentDate;
    private String initialDepositDate;
    private List<TIRatePerson> ratePersons = new LinkedList<TIRatePerson>();

    public TIRaterRequest() {}

    public TIRaterPlan getRaterPlan() {
        return raterPlan;
    }
    public void setRaterPlan(TIRaterPlan raterPlan) {
        this.raterPlan = raterPlan;
    }

    public String getFinalPaymentDate() {
        return finalPaymentDate;
    }
    public void setFinalPaymentDate(String finalPaymentDate) {
        this.finalPaymentDate = finalPaymentDate;
    }

    public String getInitialDepositDate() {
        return initialDepositDate;
    }
    public void setInitialDepositDate(String initialDepositDate) {
        this.initialDepositDate = initialDepositDate;
    }

    public List<TIRatePerson> getRatePersons() {
        return ratePersons;
    }
    public void setRatePersons(List<TIRatePerson> ratePersons) {
        this.ratePersons = ratePersons;
    }
}