package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 23.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FieldValidation {

    private String fieldCode;
    private ValidationStatus status;
    private String statusMessage;
    private String friendlyMessage;

    public String getFieldCode() { return fieldCode; }
    public void setFieldCode(String fieldCode) { this.fieldCode = fieldCode; }

    public ValidationStatus getStatus() { return status; }
    public void setStatus(ValidationStatus status) { this.status = status; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public String getFriendlyMessage() { return friendlyMessage; }
    public void setFriendlyMessage(String friendlyMessage) { this.friendlyMessage = friendlyMessage; }
}