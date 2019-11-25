package com.travelinsurancemaster.model;

public enum PaymentOption {
    ACH("ACH"),
    PAYPAL("PayPal"),
    CHECK("Check");

    private String value;

    PaymentOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
