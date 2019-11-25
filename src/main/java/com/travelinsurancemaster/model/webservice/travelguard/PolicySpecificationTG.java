package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlRootElement(name = "PolicySpecification")
@XmlAccessorType(XmlAccessType.FIELD)
public class PolicySpecificationTG {
    @XmlElement(name = "Policy", required = true)
    private PolicyTG policy;

    public PolicyTG getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyTG policy) {
        this.policy = policy;
    }
}
