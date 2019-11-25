package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"productName", "planDescription"})
public class ProductResponseTG {
    @XmlElement(name = "ProductName")
    private String productName;
    @XmlElement(name = "PlanDescription")
    private String planDescription;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public void setPlanDescription(String planDescription) {
        this.planDescription = planDescription;
    }
}
