package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"code", "description", "travelerIndex"})
public class ErrorTG {
    @XmlElement(name = "Code")
    private String code;
    @XmlElement(name = "Description")
    private String description;
    @XmlElement(name = "TravelerIndex")
    private int travelerIndex;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTravelerIndex() {
        return travelerIndex;
    }

    public void setTravelerIndex(int travelerIndex) {
        this.travelerIndex = travelerIndex;
    }
}
