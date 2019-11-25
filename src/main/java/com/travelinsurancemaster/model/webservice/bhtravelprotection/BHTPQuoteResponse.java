package com.travelinsurancemaster.model.webservice.bhtravelprotection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maleev on 01.05.2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BHTPQuoteResponse {

    private double baseQuoteAmount;
    private Integer fees;
    private double optionalCoveragesQuoteAmount;
    private String packageName;
    private long policyNumber;

    private BHTPErrorMessage[] messages;

    public BHTPErrorMessage[] getMessages() {
        return messages;
    }

    public void setMessages(BHTPErrorMessage[] messages) {
        this.messages = messages;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public double getBaseQuoteAmount() {
        return baseQuoteAmount;
    }

    public void setBaseQuoteAmount(double baseQuoteAmount) {
        this.baseQuoteAmount = baseQuoteAmount;
    }

    public Integer getFees() {
        return fees;
    }

    public void setFees(Integer fees) {
        this.fees = fees;
    }

    public double getOptionalCoveragesQuoteAmount() {
        return optionalCoveragesQuoteAmount;
    }

    public void setOptionalCoveragesQuoteAmount(double optionalCoveragesQuoteAmount) {
        this.optionalCoveragesQuoteAmount = optionalCoveragesQuoteAmount;
    }

    public long getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(long policyNumber) {
        this.policyNumber = policyNumber;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BHTPErrorMessage {

        private String text;
        private String code;
        private int type;
        private int severity;

        public BHTPErrorMessage(){}


        public BHTPErrorMessage(String text, String code, int type, int severity){
            this.text = text;
            this.code = code;
            this.type = type;
            this.severity = severity;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getSeverity() {
            return severity;
        }

        public void setSeverity(int severity) {
            this.severity = severity;
        }
    }
}
