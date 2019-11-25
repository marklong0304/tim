package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DebugInfoTG {
    @XmlElement(name = "StoredProcedureCall")
    private StoredProcedureCallTG storedProcedureCallTG;

    public StoredProcedureCallTG getStoredProcedureCallTG() {
        return storedProcedureCallTG;
    }

    public void setStoredProcedureCallTG(StoredProcedureCallTG storedProcedureCallTG) {
        this.storedProcedureCallTG = storedProcedureCallTG;
    }
}
