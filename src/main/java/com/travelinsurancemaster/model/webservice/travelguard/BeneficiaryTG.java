package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 1/19/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"beneficiaryName", "beneficiaryAddress", "beneficiaryPhone", "beneficiaryEmail", "beneficiaryBenefit"})
public class BeneficiaryTG {
    @XmlElement(name = "BeneficiaryName", required = true)
    private String beneficiaryName;
    @XmlElement(name = "BeneficiaryAddress")
    private String beneficiaryAddress;
    @XmlElement(name = "BeneficiaryPhone")
    private BeneficiaryPhoneTG beneficiaryPhone;
    @XmlElement(name = "BeneficiaryEmail")
    private String beneficiaryEmail;
    @XmlElement(name = "BeneficiaryBenefit")
    private Double beneficiaryBenefit;

    public BeneficiaryTG() {
    }

    public BeneficiaryTG(String beneficiaryName, Double beneficiaryBenefit) {
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryBenefit = beneficiaryBenefit;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BeneficiaryPhoneTG {
        @XmlElement(name = "Home")
        private String home;

        public String getHome() {
            return home;
        }

        public void setHome(String home) {
            this.home = home;
        }
    }

}
