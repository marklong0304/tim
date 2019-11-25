package com.travelinsurancemaster.model.webservice.travelguard;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"country", "state"})
public class DestinationTG {
    @XmlElement(name = "Country", required = true)
    private CountryCode country;
    @XmlElement(name = "State")
    private StateCode state;

    public DestinationTG() {
    }

    public DestinationTG(CountryCode country) {
        this.country = country;
    }

    public DestinationTG(CountryCode country, StateCode state) {
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
}
