package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"packageId", "packageName", "packageTypeId", "premium", "fees", "tax", "travelersPackageResponse"})
public class PackageTG {
    @XmlElement(name = "PackageID")
    private int packageId;
    @XmlElement(name = "PackageName")
    private String packageName;
    @XmlElement(name = "PackageTypeID")
    private int packageTypeId;
    @XmlElement(name = "Premium")
    private int premium;
    @XmlElement(name = "Fees")
    private int fees;
    @XmlElement(name = "Tax")
    private int tax;
    @XmlElement(name = "Travelers")
    private TravelersQuotePackageResponseTG travelersPackageResponse;

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPackageTypeId() {
        return packageTypeId;
    }

    public void setPackageTypeId(int packageTypeId) {
        this.packageTypeId = packageTypeId;
    }

    public int getPremium() {
        return premium;
    }

    public void setPremium(int premium) {
        this.premium = premium;
    }

    public int getFees() {
        return fees;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public TravelersQuotePackageResponseTG getTravelersPackageResponse() {
        return travelersPackageResponse;
    }

    public void setTravelersPackageResponse(TravelersQuotePackageResponseTG travelersPackageResponse) {
        this.travelersPackageResponse = travelersPackageResponse;
    }
}
