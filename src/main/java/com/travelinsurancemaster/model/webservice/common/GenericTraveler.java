package com.travelinsurancemaster.model.webservice.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.travelinsurancemaster.model.dto.purchase.PurchaseTraveler;
import com.travelinsurancemaster.model.dto.serialiazation.MultiFormatDateDeserializer;
import com.travelinsurancemaster.util.DateUtil;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;

/**
 * Created by Vlad on 10.02.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericTraveler implements Serializable, Cloneable {

    static final long serialVersionUID = 1L;

    public static final String BIRTHDAY_PATTERN = "MM/dd/yyyy";

    private String firstName;

    private String lastName;

    private String middleInitials;

    private Integer age;

    private Long ageInDays = 0L;

    private BigDecimal tripCost;

    @DateTimeFormat(pattern = BIRTHDAY_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = BIRTHDAY_PATTERN)
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private LocalDate birthday;

    private boolean primary;

    private String beneficiary;

    private BeneficiaryRelation beneficiaryRelation;

    private String ageOrBirthday;

    public GenericTraveler() {
    }

    public GenericTraveler(PurchaseTraveler traveler, BigDecimal tripCost, String beneficiary, BeneficiaryRelation beneficiaryRelation) {
        this.firstName = traveler.getFirstName();
        this.lastName = traveler.getLastName();
        this.middleInitials = traveler.getMiddleInitials();
        this.age = traveler.getAge();
        this.birthday = traveler.getBirthday();
        this.ageInDays = DAYS.between(this.birthday, LocalDate.now());;
        this.tripCost = tripCost;
        this.primary = traveler.isPrimary();
        this.beneficiary = beneficiary;
        this.beneficiaryRelation = beneficiaryRelation;
        if(traveler.getBirthday() != null) {
            setAgeOrBirthday(DateUtil.getLocalDateStr(traveler.getBirthday(), BIRTHDAY_PATTERN));
        } else {
            setAgeOrBirthday(String.valueOf(traveler.getAge()));
        }
    }

    public GenericTraveler(Integer age) {
        setAgeOrBirthday(String.valueOf(age));
    }
    public GenericTraveler(String ageOrBirthday){
        setAgeOrBirthday(ageOrBirthday);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @JsonIgnore
    public LocalDate getBirthdaySafe() {
        LocalDate birthday = getBirthday();
        if (birthday == null) {
            birthday = LocalDate.now().minusYears(age).minusDays(1);
        }
        return birthday;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        if (StringUtils.isNotBlank(firstName)) {
            firstName = WordUtils.capitalizeFully(firstName);
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        if (StringUtils.isNotBlank(lastName)) {
            lastName = WordUtils.capitalizeFully(lastName);
        }
        this.lastName = lastName;
    }

    public String getMiddleInitials() {
        return middleInitials;
    }
    public void setMiddleInitials(String middleInitials) {
        this.middleInitials = middleInitials;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) { setAgeOrBirthday(String.valueOf(age)); }

    public Long getAgeInDays() { return ageInDays; }
    public void setAgeInDays(Long ageInDays) { this.ageInDays = ageInDays; }

    public BigDecimal getTripCost() { return tripCost; }
    public void setTripCost(BigDecimal tripCost) { this.tripCost = tripCost; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        this.ageInDays = birthday == null ? 0L : DAYS.between(this.birthday, LocalDate.now());
    }

    @JsonIgnore
    public String getBirthdayStr() { return DateUtil.getLocalDateStr(this.birthday, BIRTHDAY_PATTERN); }
    @JsonIgnore
    public String getBirthdaySafeStr() { return DateUtil.getLocalDateStr(getBirthdaySafe(), BIRTHDAY_PATTERN); }

    public boolean isPrimary() { return primary; }
    public void setPrimary(boolean primary) { this.primary = primary; }

    public String getBeneficiary() { return beneficiary; }
    public void setBeneficiary(String beneficiary) {
        if (StringUtils.isNotBlank(beneficiary)) {
            beneficiary = WordUtils.capitalizeFully(beneficiary);
        }

        this.beneficiary = beneficiary;
    }

    public BeneficiaryRelation getBeneficiaryRelation() { return beneficiaryRelation; }
    public void setBeneficiaryRelation(BeneficiaryRelation beneficiaryRelation) { this.beneficiaryRelation = beneficiaryRelation; }

    public String getAgeOrBirthday() { return ageOrBirthday; }
    public void setAgeOrBirthday(String ageOrBirthday) {
        try {
            if (Pattern.matches("^-?\\d+$", ageOrBirthday)) {
                this.age = Integer.parseInt(ageOrBirthday);
                this.ageInDays = (long) (age * 365);
                this.ageOrBirthday = ageOrBirthday;
            } else {
                    this.birthday = DateUtil.getLocalDate(ageOrBirthday, BIRTHDAY_PATTERN);
                    this.age = (int) YEARS.between(this.birthday, LocalDate.now());
                    this.ageInDays = DAYS.between(this.birthday, LocalDate.now());
                    // reformatting to avoid incoming ageOrBirthday in M/d/yyyy format
                    this.ageOrBirthday = DateUtil.getLocalDateStr(this.birthday, BIRTHDAY_PATTERN);
            }
        } catch (Exception e) {
            this.ageOrBirthday = String.valueOf((this.age = 0));
        }
    }

    public void updateFromPurchaseTraveler(PurchaseTraveler traveler) {
        this.age = traveler.getAge();
        this.firstName = traveler.getFirstName();
        this.lastName = traveler.getLastName();
        this.middleInitials = traveler.getMiddleInitials();
        this.birthday = traveler.getBirthday();
        this.primary = traveler.isPrimary();
    }

    public String getFullName() {
        String fullName = firstName;
        if(StringUtils.isNotBlank(middleInitials)) fullName += StringUtils.isNotBlank(fullName) ? " " : "" + middleInitials;
        fullName += StringUtils.isNotBlank(fullName) ? " " : "" + lastName;
        return fullName;
    }

    @Override
    public GenericTraveler clone() {
        GenericTraveler traveler = new GenericTraveler();
        traveler.setFirstName(this.getFirstName());
        traveler.setLastName(this.getLastName());
        traveler.setMiddleInitials(this.getMiddleInitials());
        traveler.setAge(this.getAge());
        traveler.setAgeInDays(this.getAgeInDays());
        traveler.setBirthday(this.getBirthday());
        traveler.setAgeOrBirthday(this.getAgeOrBirthday());
        traveler.setPrimary(this.isPrimary());
        traveler.setTripCost(this.getTripCost());
        traveler.setBeneficiary(this.getBeneficiary());
        return traveler;
    }

    @Override
    public String toString() {
        return "GenericTraveler{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleInitials='" + middleInitials + '\'' +
                ", age=" + age +
                ", ageInDays" + ageInDays +
                ", tripCost=" + tripCost +
                ", birthday=" + birthday +
                ", primary=" + primary +
                ", beneficiary='" + beneficiary + '\'' +
                ", beneficiaryRelation=" + beneficiaryRelation +
                ", ageOrBirthday='" + ageOrBirthday + '\'' +
                '}';
    }
}
