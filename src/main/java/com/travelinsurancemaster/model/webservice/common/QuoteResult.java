package com.travelinsurancemaster.model.webservice.common;

import java.util.ArrayList;
import java.util.List;

public class QuoteResult extends Result {

    public String transactionId;
    public List<Product> products = new ArrayList<>();
    public QuoteRequest quoteRequest;
    public String policyMetaUniqueCode;

}