package com.travelinsurancemaster.model.webservice.travelinsure;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TIRateFactor {

    private String rateFactorName;
    private BigDecimal amount;

    public TIRateFactor() {}

    public TIRateFactor(String rateFactorName, BigDecimal amount) {
        this.rateFactorName = rateFactorName;
        this.amount = amount;
    }

    public String getRateFactorName() {
        return rateFactorName;
    }
    public void setRateFactorName(String rateFactorName) {
        this.rateFactorName = rateFactorName;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void BigDecimal(BigDecimal amount) {
        this.amount = amount;
    }
}
