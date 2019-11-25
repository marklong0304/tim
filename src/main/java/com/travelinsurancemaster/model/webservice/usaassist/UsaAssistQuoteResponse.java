package com.travelinsurancemaster.model.webservice.usaassist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by ritchie on 10/5/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsaAssistQuoteResponse {

    @JsonProperty("plan_code")
    private String planCode;
    @JsonProperty("plan_name")
    private String planName;
    private String program;
    private BigDecimal total;

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
