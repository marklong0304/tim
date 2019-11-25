package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PackagesTG {
    @XmlElement(name = "Package")
    private List<PackageTG> packages;

    public List<PackageTG> getPackages() {
        if (packages == null) {
            packages = new ArrayList<>();
        }
        return this.packages;
    }
}
