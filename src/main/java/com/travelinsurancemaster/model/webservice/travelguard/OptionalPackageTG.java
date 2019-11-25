package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OptionalPackage", propOrder = {"packageId", "days", "unitsOfCoverage", "primaryCoverage"})
public class OptionalPackageTG {
    @XmlElement(name = "Days")
    private int days;
    @XmlElement(name = "PackageId", required = true)
    private int packageId;
    @XmlElement(name = "UnitsOfCoverage")
    private double unitsOfCoverage;
    @XmlElement(name = "PrimaryCoverage")
    private int primaryCoverage;

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public double getUnitsOfCoverage() {
        return unitsOfCoverage;
    }

    public void setUnitsOfCoverage(double unitsOfCoverage) {
        this.unitsOfCoverage = unitsOfCoverage;
    }

    public int getPrimaryCoverage() {
        return primaryCoverage;
    }

    public void setPrimaryCoverage(int primaryCoverage) {
        this.primaryCoverage = primaryCoverage;
    }
}
