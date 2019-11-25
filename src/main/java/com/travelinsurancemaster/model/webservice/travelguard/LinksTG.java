package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by ritchie on 3/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LinksTG {
    @XmlElement(name = "PolicyLookup")
    private String policyLookup;

    public String getPolicyLookup() {
        return policyLookup;
    }

    public void setPolicyLookup(String policyLookup) {
        this.policyLookup = policyLookup;
    }
}
