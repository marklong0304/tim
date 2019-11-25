package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TravelersQuotePackageResponseTG {
    @XmlElement(name = "Traveler")
    private List<TravelerQuotePackageResponseTG> travelers;

    public List<TravelerQuotePackageResponseTG> getTravelers() {
        if (travelers == null) {
            travelers = new ArrayList<>();
        }
        return this.travelers;
    }
}
