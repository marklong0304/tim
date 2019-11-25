package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class StandardPremiumDistributionTG {
    @XmlElement(name = "Travelers")
    private TravelersStandardResponseTG travelersResponse;

    public TravelersStandardResponseTG getTravelersResponse() {
        return travelersResponse;
    }

    public void setTravelersResponse(TravelersStandardResponseTG travelersResponse) {
        this.travelersResponse = travelersResponse;
    }
}
