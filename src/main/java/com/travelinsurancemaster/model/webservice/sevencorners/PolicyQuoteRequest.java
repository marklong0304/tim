package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PolicyQuoteRequest {
    
    private Long policyId;
    private Long productGroupId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/YYYY")
    private LocalDate effectiveDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/YYYY")
    private LocalDate expirationDate;
    private List<Field> fields;
    private List<PersonField> personFields;

    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }

    public Long getProductGroupId() { return productGroupId; }
    public void setProductGroupId(Long productGroupId) { this.productGroupId = productGroupId; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public List<Field> getFields() { return fields; }
    public void setFields(List<Field> fields) { this.fields = fields; }

    public List<PersonField> getPersonFields() { return personFields; }
    public void setPersonFields(List<PersonField> personFields) { this.personFields = personFields; }
}
