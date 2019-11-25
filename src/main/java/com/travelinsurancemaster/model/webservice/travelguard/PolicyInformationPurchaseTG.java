package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"policyId", "policyPrefix", "policyNumber", "primaryInsured", "product", "premium", "links"})
public class PolicyInformationPurchaseTG {
    @XmlElement(name = "PolicyId")
    private long policyId;
    @XmlElement(name = "PolicyPrefix")
    private String policyPrefix;
    @XmlElement(name = "PolicyNumber")
    private String policyNumber;
    @XmlElement(name = "PrimaryInsured")
    private String primaryInsured;
    @XmlElement(name = "Product")
    private ProductResponseTG product;
    @XmlElement(name = "Premium")
    private PremiumTG premium;
    @XmlElement(name = "Links")
    private LinksTG links;

    public long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyPrefix() {
        return policyPrefix;
    }

    public void setPolicyPrefix(String policyPrefix) {
        this.policyPrefix = policyPrefix;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPrimaryInsured() {
        return primaryInsured;
    }

    public void setPrimaryInsured(String primaryInsured) {
        this.primaryInsured = primaryInsured;
    }

    public ProductResponseTG getProduct() {
        return product;
    }

    public void setProduct(ProductResponseTG product) {
        this.product = product;
    }

    public PremiumTG getPremium() {
        return premium;
    }

    public void setPremium(PremiumTG premium) {
        this.premium = premium;
    }

    public LinksTG getLinks() {
        return links;
    }

    public void setLinks(LinksTG links) {
        this.links = links;
    }
}
