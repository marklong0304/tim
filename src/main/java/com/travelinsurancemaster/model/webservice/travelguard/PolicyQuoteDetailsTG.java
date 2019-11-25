package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.*;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlRootElement(name = "PolicyQuoteDetails")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"status", "policyInformation"})
public class PolicyQuoteDetailsTG {
    @XmlElement(name = "Status")
    private StatusTG status;
    @XmlElement(name = "PolicyInformation")
    private PolicyInformationQuoteTG policyInformation;

    public StatusTG getStatus() {
        return status;
    }

    public void setStatus(StatusTG status) {
        this.status = status;
    }

    public PolicyInformationQuoteTG getPolicyInformation() {
        return policyInformation;
    }

    public void setPolicyInformation(PolicyInformationQuoteTG policyInformation) {
        this.policyInformation = policyInformation;
    }
}
