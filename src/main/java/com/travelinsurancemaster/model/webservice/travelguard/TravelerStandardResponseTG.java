package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"travelerName", "travelerPremium", "travelerTax"})
public class TravelerStandardResponseTG {
    @XmlElement(name = "TravelerName")
    private TravelerNameTG travelerName;
    @XmlElement(name = "TravelerPremium")
    private int travelerPremium;
    @XmlElement(name = "TravelerTax")
    private int travelerTax;

    public TravelerNameTG getTravelerName() {
        return travelerName;
    }

    public void setTravelerName(TravelerNameTG travelerName) {
        this.travelerName = travelerName;
    }

    public int getTravelerTax() {
        return travelerTax;
    }

    public void setTravelerTax(int travelerTax) {
        this.travelerTax = travelerTax;
    }

    public int getTravelerPremium() {
        return travelerPremium;
    }

    public void setTravelerPremium(int travelerPremium) {
        this.travelerPremium = travelerPremium;
    }
}
