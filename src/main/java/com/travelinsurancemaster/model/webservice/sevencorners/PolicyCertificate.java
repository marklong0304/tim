package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 23.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PolicyCertificate {

    private String policyId;
    private String certificateNumber;
    private String clientPersonIdentifier;

    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public String getClientPersonIdentifier() { return clientPersonIdentifier; }
    public void setClientPersonIdentifier(String clientPersonIdentifier) { this.clientPersonIdentifier = clientPersonIdentifier; }
}