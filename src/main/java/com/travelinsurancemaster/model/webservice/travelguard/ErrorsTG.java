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
public class ErrorsTG {
    @XmlElement(name = "Error")
    private List<ErrorTG> errors;

    public List<ErrorTG> getErrorsList() {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        return this.errors;
    }
}
