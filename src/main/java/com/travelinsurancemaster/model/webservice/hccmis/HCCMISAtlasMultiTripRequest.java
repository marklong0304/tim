package com.travelinsurancemaster.model.webservice.hccmis;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;

import java.math.BigDecimal;

/**
 * Created by ritchie on 3/12/15.
 */
public class HCCMISAtlasMultiTripRequest {
    private static final String FIELD_SEPARATOR = "|";

    private String primaryFirstName;
    private String primaryMiddleInitial;
    private String primaryLastName;
    private String dateOfBirth;
    private String primaryPassportNumber;
    private String spouseFullName;
    private String spouseDateOfBirth;
    private String child1FullName;
    private String child1DateOfBirth;
    private String child2FullName;
    private String child2DateOfBirth;
    private String child3FullName;
    private String child3DateOfBirth;
    private String child4FullName;
    private String child4DateOfBirth;
    private String child5FullName;
    private String child5DateOfBirth;
    private String child6FullName;
    private String child6DateOfBirth;
    private String child7FullName;
    private String child7DateOfBirth;
    private String child8FullName;
    private String child8DateOfBirth;
    private String sendInsuranceCertificateTo;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String telephone;
    private String email;
    private String countryOfCitizenship;
    private String requestedEffectiveDate;
    private String departureDate;
    private String returnDate;
    private String countriesToBeVisited;
    private String beneficiaryName;
    private String policySelected;
    private BigDecimal policyCost;
    private String deductible;
    private String medicalLimit;
    private String sportsRider;
    private String cardNumber;
    private String expMonthYear;
    private String cardName;
    private String billingAddress1;
    private String billingAddress2;
    private String billingCity;
    private String billingState;
    private String billingZip;
    private String billingCountry;
    private String daytimeTelephone;
    private String producerNumber;
    private int quotedTerm;
    private String fulfillment;
    private String bankCode;
    private String orderId;
    private String terrorismRider;
    private String homeCountry;
    private String spousePassportNo;
    private String child1PassportNo;
    private String child2PassportNo;
    private String child3PassportNo;
    private String child4PassportNo;
    private String child5PassportNo;
    private String child6PassportNo;
    private String child7PassportNo;
    private String child8PassportNo;
    private String physicallyInStateNyWaMd;
    private String physicallyInCanada;
    private String maxTripDuration;

    public static String getFieldSeparator() {
        return FIELD_SEPARATOR;
    }

    public String getPrimaryFirstName() {
        return primaryFirstName;
    }

    public void setPrimaryFirstName(String primaryFirstName) {
        this.primaryFirstName = primaryFirstName;
    }

    public String getPrimaryMiddleInitial() {
        return primaryMiddleInitial;
    }

    public void setPrimaryMiddleInitial(String primaryMiddleInitial) {
        this.primaryMiddleInitial = primaryMiddleInitial;
    }

    public String getPrimaryLastName() {
        return primaryLastName;
    }

    public void setPrimaryLastName(String primaryLastName) {
        this.primaryLastName = primaryLastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPrimaryPassportNumber() {
        return primaryPassportNumber;
    }

    public void setPrimaryPassportNumber(String primaryPassportNumber) {
        this.primaryPassportNumber = primaryPassportNumber;
    }

    public String getSpouseFullName() {
        return spouseFullName;
    }

    public void setSpouseFullName(String spouseFullName) {
        this.spouseFullName = spouseFullName;
    }

    public String getSpouseDateOfBirth() {
        return spouseDateOfBirth;
    }

    public void setSpouseDateOfBirth(String spouseDateOfBirth) {
        this.spouseDateOfBirth = spouseDateOfBirth;
    }

    public String getChild1FullName() {
        return child1FullName;
    }

    public void setChild1FullName(String child1FullName) {
        this.child1FullName = child1FullName;
    }

    public String getChild1DateOfBirth() {
        return child1DateOfBirth;
    }

    public void setChild1DateOfBirth(String child1DateOfBirth) {
        this.child1DateOfBirth = child1DateOfBirth;
    }

    public String getChild2FullName() {
        return child2FullName;
    }

    public void setChild2FullName(String child2FullName) {
        this.child2FullName = child2FullName;
    }

    public String getChild2DateOfBirth() {
        return child2DateOfBirth;
    }

    public void setChild2DateOfBirth(String child2DateOfBirth) {
        this.child2DateOfBirth = child2DateOfBirth;
    }

    public String getChild3FullName() {
        return child3FullName;
    }

    public void setChild3FullName(String child3FullName) {
        this.child3FullName = child3FullName;
    }

    public String getChild3DateOfBirth() {
        return child3DateOfBirth;
    }

    public void setChild3DateOfBirth(String child3DateOfBirth) {
        this.child3DateOfBirth = child3DateOfBirth;
    }

    public String getChild4FullName() {
        return child4FullName;
    }

    public void setChild4FullName(String child4FullName) {
        this.child4FullName = child4FullName;
    }

    public String getChild4DateOfBirth() {
        return child4DateOfBirth;
    }

    public void setChild4DateOfBirth(String child4DateOfBirth) {
        this.child4DateOfBirth = child4DateOfBirth;
    }

    public String getChild5FullName() {
        return child5FullName;
    }

    public void setChild5FullName(String child5FullName) {
        this.child5FullName = child5FullName;
    }

    public String getChild5DateOfBirth() {
        return child5DateOfBirth;
    }

    public void setChild5DateOfBirth(String child5DateOfBirth) {
        this.child5DateOfBirth = child5DateOfBirth;
    }

    public String getChild6FullName() {
        return child6FullName;
    }

    public void setChild6FullName(String child6FullName) {
        this.child6FullName = child6FullName;
    }

    public String getChild6DateOfBirth() {
        return child6DateOfBirth;
    }

    public void setChild6DateOfBirth(String child6DateOfBirth) {
        this.child6DateOfBirth = child6DateOfBirth;
    }

    public String getChild7FullName() {
        return child7FullName;
    }

    public void setChild7FullName(String child7FullName) {
        this.child7FullName = child7FullName;
    }

    public String getChild7DateOfBirth() {
        return child7DateOfBirth;
    }

    public void setChild7DateOfBirth(String child7DateOfBirth) {
        this.child7DateOfBirth = child7DateOfBirth;
    }

    public String getChild8FullName() {
        return child8FullName;
    }

    public void setChild8FullName(String child8FullName) {
        this.child8FullName = child8FullName;
    }

    public String getChild8DateOfBirth() {
        return child8DateOfBirth;
    }

    public void setChild8DateOfBirth(String child8DateOfBirth) {
        this.child8DateOfBirth = child8DateOfBirth;
    }

    public String getSendInsuranceCertificateTo() {
        return sendInsuranceCertificateTo;
    }

    public void setSendInsuranceCertificateTo(String sendInsuranceCertificateTo) {
        this.sendInsuranceCertificateTo = sendInsuranceCertificateTo;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryOfCitizenship() {
        return countryOfCitizenship;
    }

    public void setCountryOfCitizenship(String countryOfCitizenship) {
        this.countryOfCitizenship = countryOfCitizenship;
    }

    public String getRequestedEffectiveDate() {
        return requestedEffectiveDate;
    }

    public void setRequestedEffectiveDate(String requestedEffectiveDate) {
        this.requestedEffectiveDate = requestedEffectiveDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getCountriesToBeVisited() {
        return countriesToBeVisited;
    }

    public void setCountriesToBeVisited(String countriesToBeVisited) {
        this.countriesToBeVisited = countriesToBeVisited;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getPolicySelected() {
        return policySelected;
    }

    public void setPolicySelected(String policySelected) {
        this.policySelected = policySelected;
    }

    public BigDecimal getPolicyCost() {
        return policyCost;
    }

    public void setPolicyCost(BigDecimal policyCost) {
        this.policyCost = policyCost;
    }

    public String getDeductible() {
        return deductible;
    }

    public void setDeductible(String deductible) {
        this.deductible = deductible;
    }

    public String getMedicalLimit() {
        return medicalLimit;
    }

    public void setMedicalLimit(String medicalLimit) {
        this.medicalLimit = medicalLimit;
    }

    public String getSportsRider() {
        return sportsRider;
    }

    public void setSportsRider(String sportsRider) {
        this.sportsRider = sportsRider;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpMonthYear() {
        return expMonthYear;
    }

    public void setExpMonthYear(String expMonthYear) {
        this.expMonthYear = expMonthYear;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getBillingAddress1() {
        return billingAddress1;
    }

    public void setBillingAddress1(String billingAddress1) {
        this.billingAddress1 = billingAddress1;
    }

    public String getBillingAddress2() {
        return billingAddress2;
    }

    public void setBillingAddress2(String billingAddress2) {
        this.billingAddress2 = billingAddress2;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingState() {
        return billingState;
    }

    public void setBillingState(String billingState) {
        this.billingState = billingState;
    }

    public String getBillingZip() {
        return billingZip;
    }

    public void setBillingZip(String billingZip) {
        this.billingZip = billingZip;
    }

    public String getBillingCountry() {
        return billingCountry;
    }

    public void setBillingCountry(String billingCountry) {
        this.billingCountry = billingCountry;
    }

    public String getDaytimeTelephone() {
        return daytimeTelephone;
    }

    public void setDaytimeTelephone(String daytimeTelephone) {
        this.daytimeTelephone = daytimeTelephone;
    }

    public String getProducerNumber() {
        return producerNumber;
    }

    public void setProducerNumber(String producerNumber) {
        this.producerNumber = producerNumber;
    }

    public int getQuotedTerm() {
        return quotedTerm;
    }

    public void setQuotedTerm(int quotedTerm) {
        this.quotedTerm = quotedTerm;
    }

    public String getFulfillment() {
        return fulfillment;
    }

    public void setFulfillment(String fulfillment) {
        this.fulfillment = fulfillment;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTerrorismRider() {
        return terrorismRider;
    }

    public void setTerrorismRider(String terrorismRider) {
        this.terrorismRider = terrorismRider;
    }

    public String getHomeCountry() {
        return homeCountry;
    }

    public void setHomeCountry(String homeCountry) {
        this.homeCountry = homeCountry;
    }

    public String getSpousePassportNo() {
        return spousePassportNo;
    }

    public void setSpousePassportNo(String spousePassportNo) {
        this.spousePassportNo = spousePassportNo;
    }

    public String getChild1PassportNo() {
        return child1PassportNo;
    }

    public void setChild1PassportNo(String child1PassportNo) {
        this.child1PassportNo = child1PassportNo;
    }

    public String getChild2PassportNo() {
        return child2PassportNo;
    }

    public void setChild2PassportNo(String child2PassportNo) {
        this.child2PassportNo = child2PassportNo;
    }

    public String getChild3PassportNo() {
        return child3PassportNo;
    }

    public void setChild3PassportNo(String child3PassportNo) {
        this.child3PassportNo = child3PassportNo;
    }

    public String getChild4PassportNo() {
        return child4PassportNo;
    }

    public void setChild4PassportNo(String child4PassportNo) {
        this.child4PassportNo = child4PassportNo;
    }

    public String getChild5PassportNo() {
        return child5PassportNo;
    }

    public void setChild5PassportNo(String child5PassportNo) {
        this.child5PassportNo = child5PassportNo;
    }

    public String getChild6PassportNo() {
        return child6PassportNo;
    }

    public void setChild6PassportNo(String child6PassportNo) {
        this.child6PassportNo = child6PassportNo;
    }

    public String getChild7PassportNo() {
        return child7PassportNo;
    }

    public void setChild7PassportNo(String child7PassportNo) {
        this.child7PassportNo = child7PassportNo;
    }

    public String getChild8PassportNo() {
        return child8PassportNo;
    }

    public void setChild8PassportNo(String child8PassportNo) {
        this.child8PassportNo = child8PassportNo;
    }

    public String getPhysicallyInStateNyWaMd() {
        return physicallyInStateNyWaMd;
    }

    public void setPhysicallyInStateNyWaMd(String physicallyInStateNyWaMd) {
        this.physicallyInStateNyWaMd = physicallyInStateNyWaMd;
    }

    public String getPhysicallyInCanada() {
        return physicallyInCanada;
    }

    public void setPhysicallyInCanada(String physicallyInCanada) {
        this.physicallyInCanada = physicallyInCanada;
    }

    public String getMaxTripDuration() {
        return maxTripDuration;
    }

    public void setMaxTripDuration(String maxTripDuration) {
        this.maxTripDuration = maxTripDuration;
    }

    public String buildStringRequest() {
        StandardToStringStyle stringStyle = new StandardToStringStyle();
        stringStyle.setFieldSeparator(FIELD_SEPARATOR);
        stringStyle.setUseClassName(false);
        stringStyle.setUseFieldNames(false);
        stringStyle.setUseIdentityHashCode(false);
        stringStyle.setNullText("");
        stringStyle.setContentStart("");
        stringStyle.setContentEnd("");
        return new ReflectionToStringBuilder(this, stringStyle).toString();
    }
}
