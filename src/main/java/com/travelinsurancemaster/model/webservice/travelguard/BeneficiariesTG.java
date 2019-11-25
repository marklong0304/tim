package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritchie on 1/19/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BeneficiariesTG {
    @XmlElement(name = "Beneficiary", required = true)
    private List<BeneficiaryTG> beneficiaries;

    public List<BeneficiaryTG> getBeneficiaries() {
        if (beneficiaries == null) {
            beneficiaries = new ArrayList<>();
        }
        return this.beneficiaries;
    }
}
