package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"planId", "premium"})
public class PolicyInformationQuoteTG {
    @XmlElement(name = "PlanID")
    private int planId;
    @XmlElement(name = "Premium")
    private PremiumTG premium;

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public PremiumTG getPremium() {
        return premium;
    }

    public void setPremium(PremiumTG premium) {
        this.premium = premium;
    }
}
