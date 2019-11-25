package com.travelinsurancemaster.model.webservice.bhtravelprotection;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by maleev on 01.05.2016.
*/

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BHTPQuoteRequest {

    private BHTPPolicy policy;
    private List<BHTPTraveler> travelers;
    private BHTPPaymentInformation paymentInformation;
    private List<BHTPCoverage> coverages = new LinkedList<>();

    private List<BHTPFlight> flights = new LinkedList<>();

    public List<BHTPCoverage> getCoverages() {
        return coverages;
    }

    public void setCoverages(List<BHTPCoverage> coverages) {
        this.coverages = coverages;
    }

    public BHTPPaymentInformation getPaymentInformation() {
        return paymentInformation;
    }

    public void setPaymentInformation(BHTPPaymentInformation paymentInformation) {
        this.paymentInformation = paymentInformation;
    }

    public BHTPPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(BHTPPolicy policy) {
        this.policy = policy;
    }

    public List<BHTPTraveler> getTravelers() {
        return travelers;
    }

    public void setTravelers(List<BHTPTraveler> travelers) {
        this.travelers = travelers;
    }

    public List<BHTPFlight> getFlights() {
        return flights;
    }

    public void setFlights(List<BHTPFlight> flights) {
        this.flights = flights;
    }
}
