package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PaymentInformation {

    private String paymentType;
    private String billingName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String paymentPhoneNumber;
    private String email;
    private String creditCardType;
    private String creditCardNumber;
    private String cvv2;
    private String creditCardExpirationDate;
    private String routingNumber;
    private String accountNumber;
    private String checkNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/YYYY")
    private LocalDate paymentDate;

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getBillingName() { return billingName; }
    public void setBillingName(String billingName) { this.billingName = billingName; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPaymentPhoneNumber() { return paymentPhoneNumber; }
    public void setPaymentPhoneNumber(String paymentPhoneNumber) { this.paymentPhoneNumber = paymentPhoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreditCardType() { return creditCardType; }
    public void setCreditCardType(String creditCardType) { this.creditCardType = creditCardType; }

    public String getCreditCardNumber() { return creditCardNumber; }
    public void setCreditCardNumber(String creditCardNumber) { this.creditCardNumber = creditCardNumber; }

    public String getCvv2() { return cvv2; }
    public void setCvv2(String cvv2) { this.cvv2 = cvv2; }

    public String getCreditCardExpirationDate() { return creditCardExpirationDate; }
    public void setCreditCardExpirationDate(String creditCardExpirationDate) { this.creditCardExpirationDate = creditCardExpirationDate; }

    public String getRoutingNumber() { return routingNumber; }
    public void setRoutingNumber(String routingNumber) { this.routingNumber = routingNumber; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getCheckNumber() { return checkNumber; }
    public void setCheckNumber(String checkNumber) { this.checkNumber = checkNumber; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
}