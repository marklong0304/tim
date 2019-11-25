package com.travelinsurancemaster.model.webservice.bhtravelprotection;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by maleev on 07.05.2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BHTPPaymentInformation {

    private String cardNumber;
    private String expirationDate;//MM/YY
    private String cardCode;
    //as I understand it's not necassary
    private String firstName;
    //as I understand it's not necassary
    private String lastName;
    private String nameOnCard;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String duplcateWindow;
    private String paymentMethod = "CreditCard";

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getDuplcateWindow() {
        return duplcateWindow;
    }

    public void setDuplcateWindow(String duplcateWindow) {
        this.duplcateWindow = duplcateWindow;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
