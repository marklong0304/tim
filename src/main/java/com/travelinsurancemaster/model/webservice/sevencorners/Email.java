package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Email {

    private String emailAddress;
    private boolean isWorkEmail = false;
    private boolean isDefault = true;

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public boolean isWorkEmail() { return isWorkEmail; }
    public void setIsWorkEmail(boolean isWorkEmail) { this.isWorkEmail = isWorkEmail; }

    public boolean isDefault() { return isDefault; }
    public void setIsDefault(boolean workEmail) { this.isDefault = isDefault; }
}
