package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.*;

/**
 * Created by ritchie on 3/10/15.
 */
@XmlRootElement(name = "PolicyPurchaseDetails")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"status", "policyInformation"})
public class PolicyPurchaseDetailsTG {
    @XmlElement(name = "Status")
    private StatusTG status;
    @XmlElement(name = "PolicyInformation")
    private PolicyInformationPurchaseTG policyInformation;

    public StatusTG getStatus() {
        return status;
    }

    public void setStatus(StatusTG status) {
        this.status = status;
    }

    public PolicyInformationPurchaseTG getPolicyInformation() {
        return policyInformation;
    }

    public void setPolicyInformation(PolicyInformationPurchaseTG policyInformation) {
        this.policyInformation = policyInformation;
    }
}
