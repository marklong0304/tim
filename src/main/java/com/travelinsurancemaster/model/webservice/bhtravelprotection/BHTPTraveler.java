package com.travelinsurancemaster.model.webservice.bhtravelprotection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by maleev on 01.05.2016.
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BHTPTraveler {

    private Integer age;
    private BHTPAddress address;
    @JsonProperty("isPrimary")
    private boolean isPrimary;
    private Integer tripCost;

    private String firstName;
    private String lastName;
    private String birthDate;
    private String email;
    private String phoneNumber;
    private String gender;


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BHTPAddress getAddress() {
        return address;
    }

    public void setAddress(BHTPAddress address) {
        this.address = address;
    }

    public boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Integer getTripCost() {
        return tripCost;
    }

    public void setTripCost(Integer tripCost) {
        this.tripCost = tripCost;
    }
}
