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
public class DestinationsTG {
    @XmlElement(name = "Destination", required = true)
    private List<DestinationTG> destinations;

    public List<DestinationTG> getDestinations() {
        if (destinations == null) {
            destinations = new ArrayList<>();
        }
        return this.destinations;
    }
}
