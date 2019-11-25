package com.travelinsurancemaster.model.webservice.travelinsure;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TIQuoteRequest {

    private String version;
    private String producerCode;
    private TIRaterRequest raterRequest;
    private TISettlementRequest settlementRequest;

    public TIQuoteRequest() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProducerCode() {
        return producerCode;
    }

    public void setProducerCode(String producerCode) {
        this.producerCode = producerCode;
    }

    public TIRaterRequest getRaterRequest() {
        return raterRequest;
    }

    public void setRaterRequest(TIRaterRequest raterRequest) {
        this.raterRequest = raterRequest;
    }

    public TISettlementRequest getSettlementRequest() {
        return settlementRequest;
    }

    public void setSettlementRequest(TISettlementRequest settlementRequest) {
        this.settlementRequest = settlementRequest;
    }
}
