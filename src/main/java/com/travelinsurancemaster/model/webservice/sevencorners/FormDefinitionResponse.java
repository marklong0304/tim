package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FormDefinitionResponse {

    private Long policyId;
    private List<QuoteFieldDefinition> quoteFieldDefinitions;

    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }

    public List<QuoteFieldDefinition> getQuoteFieldDefinitions() { return quoteFieldDefinitions; }
    public void setQuoteFieldDefinitions(List<QuoteFieldDefinition> quoteFieldDefinitions) { this.quoteFieldDefinitions = quoteFieldDefinitions; }
}