package com.travelinsurancemaster.model.dto.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Chernov Artur on 19.08.15.
 */
@Embeddable
public class PurchaseTraveler implements Serializable, Cloneable {
    private static final long serialVersionUID = -1642661111030536999L;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String middleInitials;

    @Column
    private Integer age;

    @Column
    @DateTimeFormat(pattern = GenericTraveler.BIRTHDAY_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GenericTraveler.BIRTHDAY_PATTERN)
    private LocalDate birthday;

    @Column(nullable = false)
    private boolean primary = false;

    public PurchaseTraveler() {
    }

    public PurchaseTraveler(GenericTraveler traveler) {
        this.age = traveler.getAge();
        this.firstName = traveler.getFirstName();
        this.lastName = traveler.getLastName();
        this.middleInitials = traveler.getMiddleInitials();
        this.birthday = traveler.getBirthday();
        this.primary = traveler.isPrimary();
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

    public String getMiddleInitials() {
        return middleInitials;
    }

    public void setMiddleInitials(String middleInitials) {
        this.middleInitials = middleInitials;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public boolean isPrimary() { return primary; }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(firstName).append(" ");
        if (middleInitials != null) {
            sb.append(middleInitials).append(" ");
        }
        sb.append(lastName).append(" ");
        sb.append(age);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PurchaseTraveler)) return false;
        PurchaseTraveler traveler = (PurchaseTraveler) o;
        return age == traveler.age
                && StringUtils.equals(firstName, traveler.firstName)
                && StringUtils.equals(lastName, traveler.lastName)
                && StringUtils.equals(middleInitials, traveler.middleInitials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(age, firstName, lastName, middleInitials);
    }

    @Override
    public PurchaseTraveler clone() {
        PurchaseTraveler traveler = new PurchaseTraveler();
        traveler.setFirstName(firstName);
        traveler.setLastName(lastName);
        traveler.setMiddleInitials(middleInitials);
        traveler.setAge(age);
        traveler.setBirthday(birthday);
        traveler.setPrimary(primary);
        return traveler;
    }
}