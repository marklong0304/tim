package com.travelinsurancemaster.model.dto;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.PaymentOption;
import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.StateCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritchie on 4/22/15.
 */
@Entity
public class UserInfo implements Serializable {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String lastName;

    @Column
    private String contactName;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    @Enumerated(EnumType.STRING)
    private CountryCode country;

    @Column
    private String phone;

    @Column
    @Enumerated(EnumType.STRING)
    private StateCode stateOrProvince;

    @Column
    private String zipCode;

    @Column
    private String website;

    @Column(nullable = false)
    private boolean autocompletePurchase = true;

    @Column(nullable = false)
    private boolean confirmNotification = false;

    @Column(nullable = false, name = "userType")
    @Enumerated(EnumType.STRING)
    private UserType type = UserType.USER;

    @Column(nullable = false)
    private boolean company = false;

    @Column(nullable = true)
    private String companyName;

    @Column
    @Enumerated(EnumType.STRING)
    private PercentType percentType = PercentType.NONE;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "affiliate_percent_info", joinColumns = @JoinColumn(name = "user_info_id"))
    private List<PercentInfo> percentInfo = new ArrayList<>();

    @Column(nullable = true)
    private String taxId;

    @Column
    private PaymentOption paymentOption;

    @Column
    private String bankName;

    @Column
    private String bankRouting;

    @Column
    private String account;

    @Column
    private String paypalEmailAddress;

    @Transient
    private String countryStatePair;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public CountryCode getCountry() {
        return country;
    }

    public void setCountry(CountryCode country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public StateCode getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(StateCode stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public List<PercentInfo> getPercentInfo() {
        return percentInfo;
    }

    public void setPercentInfo(List<PercentInfo> percentInfo) {
        this.percentInfo = percentInfo;
    }

    public PercentType getPercentType() {
        return percentType;
    }

    public void setPercentType(PercentType percentType) {
        this.percentType = percentType;
    }

    @Deprecated
    public boolean isAutocompletePurchase() {
        return autocompletePurchase;
    }

    @Deprecated
    public void setAutocompletePurchase(boolean autocompletePurchase) {
        this.autocompletePurchase = autocompletePurchase;
    }

    public boolean isConfirmNotification() {
        return confirmNotification;
    }

    public void setConfirmNotification(boolean confirmNotification) {
        this.confirmNotification = confirmNotification;
    }

    public String getCountryStatePair() {
        if(countryStatePair != null) {
            return countryStatePair;
        } else {
            return country + "-" + stateOrProvince;
        }
    }

    public void setCountryStatePair(String countryStatePair) {
        this.countryStatePair = countryStatePair;
    }

    public boolean isCompany() {
        return company;
    }

    public void setCompany(boolean company) {
        this.company = company;
    }

    public String getCompanyName() {
        return companyName != null && companyName.length() > 0 ? companyName : "-";
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public PaymentOption getPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(PaymentOption paymentOption) {
        this.paymentOption = paymentOption;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankRouting() {
        return bankRouting;
    }

    public void setBankRouting(String bankRouting) {
        this.bankRouting = bankRouting;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPaypalEmailAddress() {
        return paypalEmailAddress;
    }

    public void setPaypalEmailAddress(String paypalEmailAddress) {
        this.paypalEmailAddress = paypalEmailAddress;
    }
}