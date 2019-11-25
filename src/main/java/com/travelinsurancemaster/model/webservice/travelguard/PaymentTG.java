package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"totalPremiumAmount", "creditCardDetails"})
public class PaymentTG {
    @XmlElement(name = "TotalPremiumAmount", required = true)
    private Double totalPremiumAmount;
    @XmlElement(name = "CreditCardDetails", required = true)
    private CreditCardDetailsTG creditCardDetails;

    public PaymentTG() {
    }

    public PaymentTG(Double totalPremiumAmount, CreditCardDetailsTG creditCardDetails) {
        this.totalPremiumAmount = totalPremiumAmount;
        this.creditCardDetails = creditCardDetails;
    }

    public void setTotalPremiumAmount(Double totalPremiumAmount) {
        this.totalPremiumAmount = totalPremiumAmount;
    }

    public void setCreditCardDetails(CreditCardDetailsTG creditCardDetails) {
        this.creditCardDetails = creditCardDetails;
    }
}
