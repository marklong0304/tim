package com.travelinsurancemaster.model;

import com.travelinsurancemaster.model.webservice.common.CardType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by Vlad on 20.02.2015.
 */
public class CreditCard {

    @NotEmpty
    private String ccName;
    @NotEmpty
    private String ccExpMonth;
    @NotEmpty
    private String ccExpYear;
    @NotNull
    private Long ccNumber;
    @NotNull
    private CardType ccType;
    @NotEmpty
    private String ccCode;

    //PCI-compliant CC token (eg Travelex)
    private String tokenCcNumber;

    //billing address
    private String ccZipCode;
    private String ccCity;
    private String ccAddress;
    private String ccAddressLine2;
    private StateCode ccStateCode;
    private CountryCode ccCountry;

    //not used
    private String ccPhone;
    private String ccEmail;

    public CreditCard() {
    }

    public CreditCard(String ccName, String ccExpMonth, String ccExpYear, Long ccNumber, CardType ccType, String ccZipCode, String ccCode) {
        this.ccName = ccName;
        this.ccExpMonth = ccExpMonth;
        this.ccExpYear = ccExpYear;
        this.ccNumber = ccNumber;
        this.ccType = ccType;
        this.ccZipCode = ccZipCode;
        this.ccCode = ccCode;
    }

    public String getCcPhone() {
        return ccPhone;
    }

    public void setCcPhone(String ccPhone) {
        this.ccPhone = ccPhone;
    }

    public String getCcCity() {
        return ccCity;
    }

    public void setCcCity(String ccCity) {
        this.ccCity = ccCity;
    }

    public String getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(String ccAddress) {
        this.ccAddress = ccAddress;
    }

    public StateCode getCcStateCode() {
        return ccStateCode;
    }

    public void setCcStateCode(StateCode ccStateCode) {
        this.ccStateCode = ccStateCode;
    }

    public CountryCode getCcCountry() {
        return ccCountry;
    }

    public void setCcCountry(CountryCode ccCountry) {
        this.ccCountry = ccCountry;
    }

    public String getCcName() {
        return ccName;
    }

    public void setCcName(String ccName) {
        this.ccName = ccName;
    }

    public String getCcExpMonth() {
        return ccExpMonth;
    }

    public void setCcExpMonth(String ccExpMonth) {
        this.ccExpMonth = ccExpMonth;
    }

    public String getCcExpYear() {
        return ccExpYear;
    }

    public void setCcExpYear(String ccExpYear) {
        this.ccExpYear = ccExpYear;
    }

    public Long getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(Long ccNumber) {
        this.ccNumber = ccNumber;
    }

    public CardType getCcType() {
        return ccType;
    }

    public void setCcType(CardType ccType) {
        this.ccType = ccType;
    }

    public String getCcZipCode() {
        return ccZipCode;
    }

    public void setCcZipCode(String ccZipCode) {
        this.ccZipCode = ccZipCode;
    }

    public String getCcCode() {
        return ccCode;
    }

    public void setCcCode(String ccCode) {
        this.ccCode = ccCode;
    }

    public String getTokenCcNumber() {
        return tokenCcNumber;
    }

    public void setTokenCcNumber(String tokenCcNumber) {
        this.tokenCcNumber = tokenCcNumber;
    }

    public String getCcAddressLine2() {
        return ccAddressLine2;
    }

    public void setCcAddressLine2(String ccAddressLine2) {
        this.ccAddressLine2 = ccAddressLine2;
    }

    public String getCcEmail() {
        return ccEmail;
    }

    public void setCcEmail(String ccEmail) {
        this.ccEmail = ccEmail;
    }
}
