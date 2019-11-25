package com.travelinsurancemaster.model.webservice.hccmis;

/**
 * Created by raman on 12.06.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreditCard {

    private Integer cardExpirationMonth;
    private Integer cardExpirationYear;
    private String cardHolderAddress1;
    private String cardHolderAddress2;
    private String cardHolderCity;
    private String cardHolderCountry;
    private String cardHolderDayTimePhone;
    private String cardHolderName;
    private String cardHolderState;
    private String cardHolderZip;
    private String cardNumber;
    private String cardSecurityCode;
    private String paymentMethod;
    private Transaction transaction;
}
