package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"planId", "productCode", "transactionType", "submissionType", "optionalPackagesTG"})
public class ProductRequestTG {
    @XmlElement(name = "PlanId", required = true)
    private String planId;
    @XmlElement(name = "ProductCode", required = true)
    private String productCode;
    @XmlElement(name = "TransactionType", required = true)
    private String transactionType;
    @XmlElement(name = "SubmissionType", required = true)
    private String submissionType;
    @XmlElement(name = "OptionalPackages", required = true)
    private OptionalPackagesTG optionalPackagesTG;

    public ProductRequestTG() {
    }

    public ProductRequestTG(String planId, String productCode, String transactionType, String submissionType, OptionalPackagesTG optionalPackagesTG) {
        this.planId = planId;
        this.productCode = productCode;
        this.transactionType = transactionType;
        this.submissionType = submissionType;
        this.optionalPackagesTG = optionalPackagesTG;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
    }

    public void setOptionalPackagesTG(OptionalPackagesTG optionalPackagesTG) {
        this.optionalPackagesTG = optionalPackagesTG;
    }
}
