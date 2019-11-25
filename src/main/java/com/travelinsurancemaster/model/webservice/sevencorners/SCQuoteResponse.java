package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SCQuoteResponse {

    private String quoteIdentifier;
    private String quoteVersion;
    private ValidationStatus status;
    private Double total;
    private int quoteRequestCount;
    private List<PolicyQuoteResponse> policyQuoteResponses;

    public String getQuoteIdentifier() { return quoteIdentifier; }
    public void setQuoteIdentifier(String quoteIdentifier) { this.quoteIdentifier = quoteIdentifier; }

    public String getQuoteVersion() { return quoteVersion; }
    public void setQuoteVersion(String quoteVersion) { this.quoteVersion = quoteVersion; }

    public ValidationStatus getStatus() { return status; }
    public void setStatus(ValidationStatus status) { this.status = status; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public int getQuoteRequestCount() { return quoteRequestCount; }
    public void setQuoteRequestCount(int quoteRequestCount) { this.quoteRequestCount = quoteRequestCount; }

    public List<PolicyQuoteResponse> getPolicyQuoteResponses() { return policyQuoteResponses; }
    public void setPolicyQuoteResponses(List<PolicyQuoteResponse> policyQuoteResponses) { this.policyQuoteResponses = policyQuoteResponses; }
}