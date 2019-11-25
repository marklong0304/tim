package com.travelinsurancemaster.model.webservice.hccmis;

/**
 * Created by raman on 12.06.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HCCErrorPurchaseResponse {

    @JsonProperty("ErrorMessage")
    private String errorMessage;

    @JsonProperty("Total")
    private String total;

    @JsonProperty("Premium")
    private Double premium;

    @JsonProperty("FLT")
    private Double flt;

    @JsonProperty("Shipping")
    private Double shipping;
}