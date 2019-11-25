package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 23.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SCPurchaseResponse {

    private String confirmationCode;
    private List<PolicyCertificate> policyCertificates;
    private List<CertificateFulfillment> certificateFulfillments;
    private List<FieldValidation> fieldValidations;

    public String getConfirmationCode() { return confirmationCode; }
    public void setQuoteIdentifier(String confirmationCode) { this.confirmationCode = confirmationCode; }

    public List<PolicyCertificate> getPolicyCertificates() { return policyCertificates; }
    public void setPolicyCertificates(List<PolicyCertificate> policyCertificates) { this.policyCertificates = policyCertificates; }

    public List<CertificateFulfillment> getCertificateFulfillments() { return certificateFulfillments; }
    public void setCertificateFulfillments(List<CertificateFulfillment> certificateFulfillments) { this.certificateFulfillments = certificateFulfillments; }

    public List<FieldValidation> getFieldValidations() { return fieldValidations; }
    public void setFieldValidations(List<FieldValidation> fieldValidations) { this.fieldValidations = fieldValidations; }
}