package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OptionalPackages")
public class OptionalPackagesTG {
    @XmlElement(name = "OptionalPackage", required = true)
    private List<OptionalPackageTG> optionalPackages;

    public List<OptionalPackageTG> getOptionalPackages() {
        if (optionalPackages == null) {
            optionalPackages = new ArrayList<>();
        }
        return this.optionalPackages;
    }
}
