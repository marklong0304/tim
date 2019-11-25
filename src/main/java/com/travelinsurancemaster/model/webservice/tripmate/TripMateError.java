package com.travelinsurancemaster.model.webservice.tripmate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vlad on 03.02.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TripMateError {

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
