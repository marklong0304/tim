package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 23.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CertificateFulfillment {

    private String certificateNumber;
    private List<FulfillmentLink> fulfillmentLinks;

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public List<FulfillmentLink> getFulfillmentLinks() { return fulfillmentLinks; }
    public void setFulfillmentLinks(List<FulfillmentLink> fulfillmentLinks) { this.fulfillmentLinks = fulfillmentLinks; }
}