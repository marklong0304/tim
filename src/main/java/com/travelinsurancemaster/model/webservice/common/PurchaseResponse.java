package com.travelinsurancemaster.model.webservice.common;

import java.util.HashMap;
import java.util.Map;

public class PurchaseResponse extends Result {

    private String policyNumber;
    private String orderRequestId; // todo: needs more info about policy number
    private Map<String, String> params = new HashMap<>();
    private String resultPage = null;

    public String getPolicyNumber() {
        return policyNumber;
    }
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getOrderRequestId() {
        return orderRequestId;
    }
    public void setOrderRequestId(String orderRequestId) {
        this.orderRequestId = orderRequestId;
    }

    public Map<String, String> getParams() {
        return params;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getResultPage() { return resultPage; }
    public void setResultPage(String resultPage) { this.resultPage = resultPage; }
}
