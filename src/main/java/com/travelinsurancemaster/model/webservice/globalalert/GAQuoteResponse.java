package com.travelinsurancemaster.model.webservice.globalalert;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by maleev on 02.06.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GAQuoteResponse {

    @JsonProperty("total_premium")
    private Integer totalPremium;
    @JsonProperty("processing_fee")
    private Double processingFee;
    private List<Error> errors;

    public Integer getTotalPremium() {
        return totalPremium;
    }

    public void setTotalPremium(Integer totalPremium) {
        this.totalPremium = totalPremium;
    }

    public Double getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(Double processingFee) {
        this.processingFee = processingFee;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
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
