package com.travelinsurancemaster.model.webservice.bhtravelprotection;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by maleev on 01.05.2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BHTPAddress {
    private String country;
    private String stateOrProvince;

    private String address1;
    private String address2;
    private String city;
    private String postalCode;

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }
}
