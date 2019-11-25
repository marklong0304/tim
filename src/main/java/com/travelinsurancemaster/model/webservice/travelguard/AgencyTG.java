package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"arc", "email"})
public class AgencyTG {
    @XmlElement(name = "ARC", required = true)
    private int arc;
    @XmlElement(name = "Email", required = true)
    private String email;

    public AgencyTG() {
    }

    public AgencyTG(int arc, String email) {
        this.arc = arc;
        this.email = email;
    }

    public int getArc() {
        return arc;
    }

    public void setArc(int arc) {
        this.arc = arc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
