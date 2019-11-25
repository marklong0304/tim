package com.travelinsurancemaster.model.webservice.hccmis;

/**
 * Created by raman on 12.06.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Transaction {

    private Double amount;

    public Transaction(Double amount) {
        this.amount = amount;
    }
}
