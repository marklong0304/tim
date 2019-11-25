package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"travelerId", "premium", "tax"})
public class TravelerQuotePackageResponseTG {
    @XmlElement(name = "TravelerID")
    private int travelerId;
    @XmlElement(name = "Premium")
    private int premium;
    @XmlElement(name = "Tax")
    private int tax;

    public int getTravelerId() {
        return travelerId;
    }

    public void setTravelerId(int travelerId) {
        this.travelerId = travelerId;
    }

    public int getPremium() {
        return premium;
    }

    public void setPremium(int premium) {
        this.premium = premium;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }
}
