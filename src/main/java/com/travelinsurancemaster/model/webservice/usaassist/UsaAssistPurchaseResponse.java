package com.travelinsurancemaster.model.webservice.usaassist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Created by ritchie on 10/1/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsaAssistPurchaseResponse {

    private String receipt;
    private String certificate;
    private BigDecimal total;
    private ErrorObject error;
    private int delay;

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public ErrorObject getError() {
        return error;
    }

    public void setError(ErrorObject error) {
        this.error = error;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class ErrorObject {
        private String action;
        private String key;
        private String value;
        private String traveler;
        private String text;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTraveler() {
            return traveler;
        }

        public void setTraveler(String traveler) {
            this.traveler = traveler;
        }
    }
}
