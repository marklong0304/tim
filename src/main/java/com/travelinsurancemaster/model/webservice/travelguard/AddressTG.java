package com.travelinsurancemaster.model.webservice.travelguard;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"country", "state", "city", "street", "zipCode"})
public class AddressTG {
    @XmlElement(name = "Country", required = true)
    private CountryCode country;
    @XmlElement(name = "State", required = true)
    private StateCode state;
    @XmlElement(name = "City", required = true)
    private String city;
    @XmlElement(name = "Street", required = true)
    private String street;
    @XmlElement(name = "Zip", required = true)
    private String zipCode;

    public AddressTG() {
    }

    public AddressTG(CountryCode country, StateCode state, String city, String street, String zipCode) {
        this.country = country;
        this.state = state;
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
    }

    public AddressTG(CountryCode country, StateCode state) {
        this.country = country;
        this.state = state;
    }

    public CountryCode getCountry() {
        return country;
    }

    public void setCountry(CountryCode country) {
        this.country = country;
    }

    public StateCode getState() {
        return state;
    }

    public void setState(StateCode state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
