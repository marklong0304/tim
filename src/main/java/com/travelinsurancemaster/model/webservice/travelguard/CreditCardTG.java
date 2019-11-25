package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"amount", "cardName", "cardNumber", "expMonth", "expYear", "cardType"})
public class CreditCardTG {
    @XmlElement(name = "Amount", required = true)
    private double amount;
    @XmlElement(name = "CreditHolderName", required = true)
    private String cardName;
    @XmlElement(name = "Number", required = true)
    private long cardNumber;
    @XmlElement(name = "ExpiryMonth", required = true)
    private String expMonth;
    @XmlElement(name = "ExpiryYear", required = true)
    private String expYear;
    @XmlElement(name = "CreditCardType", required = true)
    private String cardType;

    public CreditCardTG() {
    }

    public CreditCardTG(double amount, String cardName, long cardNumber, String expMonth, String expYear, String cardType) {
        this.amount = amount;
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cardType = cardType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public void setCardNumber(long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
