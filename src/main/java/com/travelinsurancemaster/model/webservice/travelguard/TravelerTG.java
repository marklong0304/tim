package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"tripCost", "birthDate", "email", "travelerName", "address", "beneficiaries"})
public class TravelerTG {
    @XmlElement(name = "TripCost", required = true)
    private BigDecimal tripCost;
    @XmlElement(name = "BirthDate", required = true)
    private String birthDate;
    @XmlElement(name = "Email", required = true)
    private String email;
    @XmlElement(name = "TravelerName", required = true)
    private TravelerNameTG travelerName;
    @XmlElement(name = "Address", required = true)
    private AddressTG address;
    @XmlElement(name = "Beneficiaries", required = true)
    private BeneficiariesTG beneficiaries;

    public TravelerTG() {
    }

    public TravelerTG(BigDecimal tripCost, String birthDate, String email, TravelerNameTG travelerName, AddressTG address, BeneficiariesTG beneficiaries) {
        this.tripCost = tripCost;
        this.birthDate = birthDate;
        this.email = email;
        this.travelerName = travelerName;
        this.address = address;
        this.beneficiaries = beneficiaries;
    }

    public TravelerTG(BigDecimal tripCost, String birthDate) {
        this.tripCost = tripCost;
        this.birthDate = birthDate;
    }

    public TravelerTG(BigDecimal tripCost, String birthDate, String email, TravelerNameTG travelerName, AddressTG address) {
        this.tripCost = tripCost;
        this.birthDate = birthDate;
        this.email = email;
        this.travelerName = travelerName;
        this.address = address;
    }

    public BigDecimal getTripCost() {
        return tripCost;
    }

    public void setTripCost(BigDecimal tripCost) {
        this.tripCost = tripCost;
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

    public void setTravelerName(TravelerNameTG travelerName) {
        this.travelerName = travelerName;
    }

    public AddressTG getAddress() {
        return address;
    }

    public void setAddress(AddressTG address) {
        this.address = address;
    }
}
