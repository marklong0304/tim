package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"fulfillmentType", "otherEmail", "needEmail"})
public class FulfillmentTG {
    @XmlElement(name = "FulfillmentType")
    private String fulfillmentType;
    @XmlElement(name = "OtherEmail")
    private String otherEmail;
    @XmlElement(name = "NeedEmail")
    private String needEmail;

    public FulfillmentTG() {
    }

    public FulfillmentTG(String fulfillmentType, String needEmail) {
        this.fulfillmentType = fulfillmentType;
        this.needEmail = needEmail;
    }

    public FulfillmentTG(String fulfillmentType, String otherEmail, String needEmail) {
        this.fulfillmentType = fulfillmentType;
        this.otherEmail = otherEmail;
        this.needEmail = needEmail;
    }

    public void setFulfillmentType(String fulfillmentType) {
        this.fulfillmentType = fulfillmentType;
    }

    public void setOtherEmail(String otherEmail) {
        this.otherEmail = otherEmail;
    }

    public void setNeedEmail(String needEmail) {
        this.needEmail = needEmail;
    }
}
