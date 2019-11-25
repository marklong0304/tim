package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SCQuoteRequest {

    private boolean saveQuote = true;
    private String quoteIdentifier;
    private int quoteRequestCount;
    private List<PolicyQuoteRequest> policyQuoteRequests;
    private List<Person> persons;
    private List<PrimaryMemberAddress> primaryMemberAddresses;

    public boolean isSaveQuote() { return saveQuote; }
    public void setSaveQuote(boolean saveQuote) { this.saveQuote = saveQuote; }

    public String getQuoteIdentifier() { return quoteIdentifier; }
    public void setQuoteIdentifier(String quoteIdentifier) { this.quoteIdentifier = quoteIdentifier; }

    public int getQuoteRequestCount() { return quoteRequestCount; }
    public void setQuoteRequestCount(int quoteRequestCount) { this.quoteRequestCount = quoteRequestCount; }

    public List<PolicyQuoteRequest> getPolicyQuoteRequests() { return policyQuoteRequests; }
    public void setPolicyQuoteRequests(List<PolicyQuoteRequest> policyQuoteRequests) { this.policyQuoteRequests = policyQuoteRequests; }

    public List<Person> getPersons() { return persons; }
    public void setPersons(List<Person> persons) { this.persons = persons; }

    public List<PrimaryMemberAddress> getPrimaryMemberAddresses() { return primaryMemberAddresses; }

    public void setPrimaryMemberAddresses(List<PrimaryMemberAddress> primaryMemberAddresses) { this.primaryMemberAddresses = primaryMemberAddresses; }
}
