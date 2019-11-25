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
public class PackageWarningsTG {
    @XmlElement(name = "PackageWarning")
    private List<PackageWarningTG> packageWarnings;

    public List<PackageWarningTG> getPackageWarningsList() {
        if (packageWarnings == null) {
            packageWarnings = new ArrayList<>();
        }
        return this.packageWarnings;
    }
}
