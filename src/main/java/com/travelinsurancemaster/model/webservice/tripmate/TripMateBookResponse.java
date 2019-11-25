package com.travelinsurancemaster.model.webservice.tripmate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TripMateBookResponse {

    @JsonProperty("enrollment_id")
    private String enrollmentId;
    @JsonProperty("enc_enrollment_id")
    private String encEnrollmentId;
    @JsonProperty("payment_response")
    private String paymentResponse;
    @JsonProperty("calculated_premium")
    private String calculatedPremium;
    @JsonProperty("posted_premium")
    private String postedPremium;
    private List<TripMateError> errors;
    @JsonProperty("document_links")
    private TripMateDocLinks documentLinks;
    @JsonProperty("payment_id")
    private String paymentId;

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getEncEnrollmentId() {
        return encEnrollmentId;
    }

    public void setEncEnrollmentId(String encEnrollmentId) {
        this.encEnrollmentId = encEnrollmentId;
    }

    public String getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(String paymentResponse) {
        this.paymentResponse = paymentResponse;
    }

    public String getCalculatedPremium() {
        return calculatedPremium;
    }

    public void setCalculatedPremium(String calculatedPremium) {
        this.calculatedPremium = calculatedPremium;
    }

    public String getPostedPremium() {
        return postedPremium;
    }

    public void setPostedPremium(String postedPremium) {
        this.postedPremium = postedPremium;
    }

    public List<TripMateError> getErrors() {
        return errors;
    }

    public void setErrors(List<TripMateError> errors) {
        this.errors = errors;
    }

    public TripMateDocLinks getDocumentLinks() {
        return documentLinks;
    }

    public void setDocumentLinks(TripMateDocLinks documentLinks) {
        this.documentLinks = documentLinks;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
