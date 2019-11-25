package com.travelinsurancemaster.model.dto.json.datatable.purchase;

import com.travelinsurancemaster.model.util.IntegerRange;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by Chernov Artur on 21.08.15.
 */
public class PurchaseTravelerJson implements Serializable {
    private static final long serialVersionUID = -5226579763771164455L;

    private String firstName;
    private String lastName;
    private String middleInitials;
    private IntegerRange age;

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

    public IntegerRange getAge() {
        return age;
    }

    public void setAge(IntegerRange age) {
        this.age = age;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(firstName) && StringUtils.isBlank(lastName) && StringUtils.isBlank(middleInitials) && age.isEmpty();
    }
}
