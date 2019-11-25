package com.travelinsurancemaster.model.webservice.travelinsure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TIQuoteResponse {

    private String purchaseId;
    private Integer errorNumber;
    private List<String> resultMessages;
    private RaterResponse raterResponse;
    private SettlementResponse settlementResponse; 

    public String getPurchaseId() {
        return purchaseId;
    }
    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Integer getErrorNumber() {
        return errorNumber;
    }
    public void setErrorNumber(Integer errorNumber) {
        this.errorNumber = errorNumber;
    }

    public List<String> getResultMessages() { return resultMessages; }
    public String getResultMessage() { return resultMessages != null && resultMessages.size() > 0 ? resultMessages.stream().collect(Collectors.joining(" ")) : ""; }
    public void setResultMessages(List<String> resultMessages) { this.resultMessages = resultMessages; }

    public RaterResponse getRaterResponse() {
        return raterResponse;
    }
    public void setRaterResponse(RaterResponse raterResponse) {
        this.raterResponse = raterResponse;
    }

    public SettlementResponse getSettlementResponse() { return settlementResponse; }
    public void setSettlementResponse(SettlementResponse settlementResponse) { this.settlementResponse = settlementResponse; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RaterResponse {
        private String quoteId;
        private RaterPlan raterPlan;

        public String getQuoteId() {
            return quoteId;
        }
        public void setQuoteId(String quoteId) {
            this.quoteId = quoteId;
        }

        public RaterPlan getRaterPlan() {
            return raterPlan;
        }
        public void setRaterPlan(RaterPlan raterPlan) {
            this.raterPlan = raterPlan;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RaterPlan {
        private String planName;
        private String tier;
        private Double premium;

        public String getPlanName() {
            return planName;
        }
        public void setPlanName(String planName) {
            this.planName = planName;
        }

        public String getTier() {
            return tier;
        }
        public void setTier(String tier) {
            this.tier = tier;
        }

        public Double getPremium() { return premium; }
        public void setPremium(Double premium) {
            this.premium = premium;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SettlementResponse {

        private String settlementId;
        private String authCode;
        private String transactionId;
        private String message;
        private Double amount;

        public String getSettlementId() {
            return settlementId;
        }
        public void setSettlementId(String settlementId) {
            this.settlementId = settlementId;
        }

        public String getAuthCode() {
            return authCode;
        }
        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }

        public String getTransactionId() {
            return transactionId;
        }
        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }

        public Double getAmount() {
            return amount;
        }
        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }
}