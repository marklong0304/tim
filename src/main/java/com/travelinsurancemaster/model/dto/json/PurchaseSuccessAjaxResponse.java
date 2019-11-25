package com.travelinsurancemaster.model.dto.json;

import java.io.Serializable;

/**
 * @author Artur Chernov
 */
public class PurchaseSuccessAjaxResponse implements AjaxResponse, Serializable {
    private static final long serialVersionUID = 1L;

    private String quoteId;
    private String requestId;
    private String policyUniqueCode;
    private Double price;

    public PurchaseSuccessAjaxResponse(String quoteId, String requestId, String policyUniqueCode, Double price) {
        this.requestId = requestId;
        this.quoteId = quoteId;
        this.policyUniqueCode = policyUniqueCode;
        this.price = price;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPolicyUniqueCode() {
        return policyUniqueCode;
    }

    public void setPolicyUniqueCode(String policyUniqueCode) {
        this.policyUniqueCode = policyUniqueCode;
    }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }
}