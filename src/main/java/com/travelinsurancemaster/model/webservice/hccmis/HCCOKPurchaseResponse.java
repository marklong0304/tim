package com.travelinsurancemaster.model.webservice.hccmis;

/**
 * Created by raman on 12.06.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HCCOKPurchaseResponse {

    @JsonProperty("Cert")
    private String cert;

    @JsonProperty("AuthCode")
    private String authCode;

    @JsonProperty("TransactionId")
    private String transactionId;
}