package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PackagePremiumDistributionTG {
    @XmlElement(name = "Packages")
    private PackagesTG packages;

    public PackagesTG getPackages() {
        return packages;
    }

    public void setPackages(PackagesTG packages) {
        this.packages = packages;
    }
}
