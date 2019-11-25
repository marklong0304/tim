package com.travelinsurancemaster.model.webservice.travelinsure;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TIRatePerson {

    private String personType;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String postal;
    private String country;
    private String gender;
    private String dateOfBirth;
    private String phone;
    private String secondaryPhone;
    private String email;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private List<TIExtraAttribute> extraAttributes = new LinkedList<TIExtraAttribute>();
    private List<TIRateFactor> rateFactors = new LinkedList<TIRateFactor>();

    public TIRatePerson() {}

    public String getPersonType() {
        return personType;
    }
    public void setPersonType(String personType) {
        this.personType = personType;
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

    public String getPostal() {
        return postal;
    }
    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }
    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public List<TIExtraAttribute> getExtraAttributes() {
        return extraAttributes;
    }
    public void setExtraAttributes(List<TIExtraAttribute> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }

    public List<TIRateFactor> getRateFactors() {
        return rateFactors;
    }
    public void setRateFactors(List<TIRateFactor> rateFactors) {
        this.rateFactors = rateFactors;
    }
}