package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/30/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"packageId", "description"})
public class PackageWarningTG {
    @XmlElement(name = "PackageID")
    private Integer packageId;
    @XmlElement(name = "Description")
    private String description;

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
