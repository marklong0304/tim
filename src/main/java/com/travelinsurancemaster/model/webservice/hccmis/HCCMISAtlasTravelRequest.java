package com.travelinsurancemaster.model.webservice.hccmis;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;

import java.math.BigDecimal;

/**
 * Created by ritchie on 3/11/15.
 */
public class HCCMISAtlasTravelRequest {
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
    private String zip;
    private String country;
    private String telephone;
    private String email;
    private String primaryTravelerCountryCitizenship;
    private String requestedEffectiveDate;
    private String departureDate;
    private String returnDate;
    private String countriesToBeVisited;
    private String beneficiaryName;
    private String policySelected;
    private BigDecimal policyCost;
    private String deductible;
    private long medicalLimit;
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
    private String onlineFulfillment;
    private int quotedTerm;
    private String bankCode;
    private String orderId;
    private String homeCountry;
    private String primaryGender;
    private String spouseGender;
    private String spouseCountryOfCitzenship;
    private String child1Gender;
    private String child1CountryOfCitizenship;
    private String child2Gender;
    private String child2CountryOfCitizenship;
    private String child3Gender;
    private String child3CountryOfCitizenship;
    private String child4Gender;
    private String child4CountryOfCitizenship;
    private String child5Gender;
    private String child5CountryOfCitizenship;
    private String child6Gender;
    private String child6CountryOfCitizenship;
    private String child7Gender;
    private String child7CountryOfCitizenship;
    private String child8Gender;
    private String child8CountryOfCitizenship;
    private String requestedTerminationDate;
    private String workingInFlorida;
    private String creditCardSecurityCode;
    private String spousePassportNo;
    private String child1PassportNo;
    private String child2PassportNo;
    private String child3PassportNo;
    private String child4PassportNo;
    private String child5PassportNo;
    private String child6PassportNo;
    private String child7PassportNo;
    private String child8PassportNo;
    private String uuid;
    private String physicallyInStateNyWaMd;
    private String fulfillmentInSpanish;
    private String physicallyInRestrictedCountry;
    private String tokenFlag;
    private String creditCardType;
    private String pracaExempt;

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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getPrimaryTravelerCountryCitizenship() {
        return primaryTravelerCountryCitizenship;
    }

    public void setPrimaryTravelerCountryCitizenship(String primaryTravelerCountryCitizenship) {
        this.primaryTravelerCountryCitizenship = primaryTravelerCountryCitizenship;
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

    public long getMedicalLimit() {
        return medicalLimit;
    }

    public void setMedicalLimit(long medicalLimit) {
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

    public String getOnlineFulfillment() {
        return onlineFulfillment;
    }

    public void setOnlineFulfillment(String onlineFulfillment) {
        this.onlineFulfillment = onlineFulfillment;
    }

    public int getQuotedTerm() {
        return quotedTerm;
    }

    public void setQuotedTerm(int quotedTerm) {
        this.quotedTerm = quotedTerm;
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

    public String getHomeCountry() {
        return homeCountry;
    }

    public void setHomeCountry(String homeCountry) {
        this.homeCountry = homeCountry;
    }

    public String getPrimaryGender() {
        return primaryGender;
    }

    public void setPrimaryGender(String primaryGender) {
        this.primaryGender = primaryGender;
    }

    public String getSpouseGender() {
        return spouseGender;
    }

    public void setSpouseGender(String spouseGender) {
        this.spouseGender = spouseGender;
    }

    public String getSpouseCountryOfCitzenship() {
        return spouseCountryOfCitzenship;
    }

    public void setSpouseCountryOfCitzenship(String spouseCountryOfCitzenship) {
        this.spouseCountryOfCitzenship = spouseCountryOfCitzenship;
    }

    public String getChild1Gender() {
        return child1Gender;
    }

    public void setChild1Gender(String child1Gender) {
        this.child1Gender = child1Gender;
    }

    public String getChild1CountryOfCitizenship() {
        return child1CountryOfCitizenship;
    }

    public void setChild1CountryOfCitizenship(String child1CountryOfCitizenship) {
        this.child1CountryOfCitizenship = child1CountryOfCitizenship;
    }

    public String getChild2Gender() {
        return child2Gender;
    }

    public void setChild2Gender(String child2Gender) {
        this.child2Gender = child2Gender;
    }

    public String getChild2CountryOfCitizenship() {
        return child2CountryOfCitizenship;
    }

    public void setChild2CountryOfCitizenship(String child2CountryOfCitizenship) {
        this.child2CountryOfCitizenship = child2CountryOfCitizenship;
    }

    public String getChild3Gender() {
        return child3Gender;
    }

    public void setChild3Gender(String child3Gender) {
        this.child3Gender = child3Gender;
    }

    public String getChild3CountryOfCitizenship() {
        return child3CountryOfCitizenship;
    }

    public void setChild3CountryOfCitizenship(String child3CountryOfCitizenship) {
        this.child3CountryOfCitizenship = child3CountryOfCitizenship;
    }

    public String getChild4Gender() {
        return child4Gender;
    }

    public void setChild4Gender(String child4Gender) {
        this.child4Gender = child4Gender;
    }

    public String getChild4CountryOfCitizenship() {
        return child4CountryOfCitizenship;
    }

    public void setChild4CountryOfCitizenship(String child4CountryOfCitizenship) {
        this.child4CountryOfCitizenship = child4CountryOfCitizenship;
    }

    public String getChild5Gender() {
        return child5Gender;
    }

    public void setChild5Gender(String child5Gender) {
        this.child5Gender = child5Gender;
    }

    public String getChild5CountryOfCitizenship() {
        return child5CountryOfCitizenship;
    }

    public void setChild5CountryOfCitizenship(String child5CountryOfCitizenship) {
        this.child5CountryOfCitizenship = child5CountryOfCitizenship;
    }

    public String getChild6Gender() {
        return child6Gender;
    }

    public void setChild6Gender(String child6Gender) {
        this.child6Gender = child6Gender;
    }

    public String getChild6CountryOfCitizenship() {
        return child6CountryOfCitizenship;
    }

    public void setChild6CountryOfCitizenship(String child6CountryOfCitizenship) {
        this.child6CountryOfCitizenship = child6CountryOfCitizenship;
    }

    public String getChild7Gender() {
        return child7Gender;
    }

    public void setChild7Gender(String child7Gender) {
        this.child7Gender = child7Gender;
    }

    public String getChild7CountryOfCitizenship() {
        return child7CountryOfCitizenship;
    }

    public void setChild7CountryOfCitizenship(String child7CountryOfCitizenship) {
        this.child7CountryOfCitizenship = child7CountryOfCitizenship;
    }

    public String getChild8Gender() {
        return child8Gender;
    }

    public void setChild8Gender(String child8Gender) {
        this.child8Gender = child8Gender;
    }

    public String getChild8CountryOfCitizenship() {
        return child8CountryOfCitizenship;
    }

    public void setChild8CountryOfCitizenship(String child8CountryOfCitizenship) {
        this.child8CountryOfCitizenship = child8CountryOfCitizenship;
    }

    public String getRequestedTerminationDate() {
        return requestedTerminationDate;
    }

    public void setRequestedTerminationDate(String requestedTerminationDate) {
        this.requestedTerminationDate = requestedTerminationDate;
    }

    public String getWorkingInFlorida() {
        return workingInFlorida;
    }

    public void setWorkingInFlorida(String workingInFlorida) {
        this.workingInFlorida = workingInFlorida;
    }

    public String getCreditCardSecurityCode() {
        return creditCardSecurityCode;
    }

    public void setCreditCardSecurityCode(String creditCardSecurityCode) {
        this.creditCardSecurityCode = creditCardSecurityCode;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhysicallyInStateNyWaMd() {
        return physicallyInStateNyWaMd;
    }

    public void setPhysicallyInStateNyWaMd(String physicallyInStateNyWaMd) {
        this.physicallyInStateNyWaMd = physicallyInStateNyWaMd;
    }

    public String getFulfillmentInSpanish() {
        return fulfillmentInSpanish;
    }

    public void setFulfillmentInSpanish(String fulfillmentInSpanish) {
        this.fulfillmentInSpanish = fulfillmentInSpanish;
    }

    public String getPhysicallyInRestrictedCountry() {
        return physicallyInRestrictedCountry;
    }

    public void setPhysicallyInRestrictedCountry(String physicallyInRestrictedCountry) {
        this.physicallyInRestrictedCountry = physicallyInRestrictedCountry;
    }

    public String getTokenFlag() {
        return tokenFlag;
    }

    public void setTokenFlag(String tokenFlag) {
        this.tokenFlag = tokenFlag;
    }

    public String getCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(String creditCardType) {
        this.creditCardType = creditCardType;
    }

    public String getPracaExempt() {
        return pracaExempt;
    }

    public void setPracaExempt(String pracaExempt) {
        this.pracaExempt = pracaExempt;
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
