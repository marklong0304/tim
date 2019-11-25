package com.travelinsurancemaster.model.webservice.globalalert;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by maleev on 09.06.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GAPurchaseResponse {
    @JsonProperty("enrollment_id")
    private Integer enrollmentId;
    @JsonProperty("enc_enrollment_id")
    private String encEnrollmentId;
    @JsonProperty("payment_response")
    private Integer paymentResponse;
    @JsonProperty("calculated_premium")
    private String calculatedPremium;
    @JsonProperty("posted_premium")
    private String postedPremium;

    private List<Error> errors;
    @JsonProperty("payment_id")
    private Integer pyamentId;

    public Integer getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Integer enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getEncEnrollmentId() {
        return encEnrollmentId;
    }

    public void setEncEnrollmentId(String encEnrollmentId) {
        this.encEnrollmentId = encEnrollmentId;
    }

    public Integer getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(Integer paymentResponse) {
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

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public Integer getPyamentId() {
        return pyamentId;
    }

    public void setPyamentId(Integer pyamentId) {
        this.pyamentId = pyamentId;
    }

    public static class Error{

        @JsonProperty("error_code")
        private String errorCode;
        @JsonProperty("error_text")
        private String errorText;

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorText() {
            return errorText;
        }

        public void setErrorText(String errorText) {
            this.errorText = errorText;
        }
    }
}
