package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by ritchie on 3/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CreditCardDetailsTG {
    @XmlElement(name = "CreditCard", required = true)
    private CreditCardTG creditCard;

    public CreditCardDetailsTG() {
    }

    public CreditCardDetailsTG(CreditCardTG creditCard) {
        this.creditCard = creditCard;
    }

    public CreditCardTG getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCardTG creditCard) {
        this.creditCard = creditCard;
    }
}
