package com.travelinsurancemaster.model.webservice.bhtravelprotection;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by maleev on 01.05.2016.
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BHTPPolicy {

    private String agencyCode;
    private String agentCode;
    private String productId;
//    String packageId;
    private String destinationCountry;
    private String departureDate;
    private String returnDate;
    private String tripDepositDate;
    private boolean isQuickQuote = true;
    private boolean acceptedTerms = true;

    private String fulfillmentMethod;

    private String rentalCarPickupDate;
    private String rentalCarReturnDate;

    private Boolean overrideRequiresFlights;
    private Boolean futureFlightsExpected;

    public String getRentalCarPickupDate() {
        return rentalCarPickupDate;
    }

    public void setRentalCarPickupDate(String rentalCarPickupDate) {
        this.rentalCarPickupDate = rentalCarPickupDate;
    }

    public String getRentalCarReturnDate() {
        return rentalCarReturnDate;
    }

    public void setRentalCarReturnDate(String rentalCarReturnDate) {
        this.rentalCarReturnDate = rentalCarReturnDate;
    }

    public String getFulfillmentMethod() {
        return fulfillmentMethod;
    }

    public void setFulfillmentMethod(String fulfillmentMethod) {
        this.fulfillmentMethod = fulfillmentMethod;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getTripDepositDate() {
        return tripDepositDate;
    }

    public void setTripDepositDate(String tripDepositDate) {
        this.tripDepositDate = tripDepositDate;
    }

    public boolean getIsQuickQuote() {
        return isQuickQuote;
    }

    public void setIsQuickQuote(boolean isQuickQuote) {
        this.isQuickQuote = isQuickQuote;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public Boolean getOverrideRequiresFlights() {
        return overrideRequiresFlights;
    }

    public void setOverrideRequiresFlights(Boolean overrideRequiresFlights) {
        this.overrideRequiresFlights = overrideRequiresFlights;
    }

    public Boolean getFutureFlightsExpected() {
        return futureFlightsExpected;
    }

    public void setFutureFlightsExpected(Boolean futureFlightsExpected) {
        this.futureFlightsExpected = futureFlightsExpected;
    }
}
