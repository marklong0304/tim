package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Phone {

    private String phoneNumber;
    private String phoneType;

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPhoneType() { return phoneType; }
    public void setPhoneType(String phoneType) { this.phoneType = phoneType; }
}
