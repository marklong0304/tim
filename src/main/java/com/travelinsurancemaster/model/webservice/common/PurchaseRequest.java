package com.travelinsurancemaster.model.webservice.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.util.CountryCodesUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseRequest {

    @JsonIgnore
    private QuoteRequest quoteRequest;

    // home address
    @NotEmpty
    private String city;
    @NotEmpty
    private String address;
    private String addressLine2;
    @NotEmpty
    private String postalCode;
    @NotEmpty
    @Email
    private String email;
    // home address

    @Valid
    @NotNull
    private CreditCard creditCard = new CreditCard();

    @NotEmpty
    private List<GenericTraveler> travelers = new ArrayList<>();

    @JsonIgnore
    private boolean billingSameAsHome;

    @JsonIgnore
    private String creditCardCountryStatePair;

    private String countryStatePair;

    private Product product;

    private String phone;

    private Set<String> tripTypes = new HashSet<>();

    private List<Flight> flights;

    public PurchaseRequest() {
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<GenericTraveler> getTravelers() {
        return travelers;
    }

    public void setTravelers(List<GenericTraveler> travelers) {
        this.travelers = travelers;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public QuoteRequest getQuoteRequest() {
        return quoteRequest;
    }

    public void setQuoteRequest(QuoteRequest quoteRequest) {
        this.quoteRequest = quoteRequest;
    }

    public boolean isBillingSameAsHome() {
        return billingSameAsHome;
    }

    public void setBillingSameAsHome(boolean billingSameAsHome) {
        this.billingSameAsHome = billingSameAsHome;
    }

    public void copyBillingFromHome() {
        if (billingSameAsHome) {
            creditCard.setCcAddress(getAddress());
            creditCard.setCcCountry(getQuoteRequest().getResidentCountry());
            creditCard.setCcStateCode(getQuoteRequest().getResidentState());
            creditCard.setCcCity(getCity());
            creditCard.setCcZipCode(getPostalCode());
            creditCardCountryStatePair = CountryCodesUtils.getCountryStatePair(creditCard.getCcCountry(), creditCard.getCcStateCode());
            creditCard.setCcPhone(getPhone());
            creditCard.setCcEmail(getEmail());
        }
    }

    public String getCreditCardCountryStatePair() {
        return creditCardCountryStatePair;
    }

    public void setCreditCardCountryStatePair(String creditCardCountryStatePair) {
        this.creditCardCountryStatePair = creditCardCountryStatePair;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @Deprecated
    public String getCountryStatePair() {
        return countryStatePair;
    }

    @Deprecated
    public void setCountryStatePair(String countryStatePair) {
        this.countryStatePair = countryStatePair;
    }

    public Set<String> getTripTypes() {
        return tripTypes;
    }

    public void setTripTypes(Set<String> tripTypes) {
        this.tripTypes = tripTypes;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}
