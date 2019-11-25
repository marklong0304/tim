package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SCPurchaseRequest {

    private String quoteIdentifier;
    private String quoteVersion;
    private boolean agreedToDisclaimerText;
    private boolean canSendPromotionalMaterials;
    private String agentNumber;
    private PaymentInformation paymentInformation;

    public String getQuoteIdentifier() { return quoteIdentifier; }
    public void setQuoteIdentifier(String quoteIdentifier) { this.quoteIdentifier = quoteIdentifier; }

    public String getQuoteVersion() { return quoteVersion; }
    public void setQuoteVersion(String quoteVersion) { this.quoteVersion = quoteVersion; }

    public boolean isAgreedToDisclaimerText() { return agreedToDisclaimerText; }
    public void setAgreedToDisclaimerText(boolean agreedToDisclaimerText) { this.agreedToDisclaimerText = agreedToDisclaimerText; }

    public boolean isCanSendPromotionalMaterials() { return canSendPromotionalMaterials; }
    public void setCanSendPromotionalMaterials(boolean canSendPromotionalMaterials) { this.canSendPromotionalMaterials = canSendPromotionalMaterials; }

    public String getAgentNumber() { return agentNumber; }
    public void setAgentNumber(String agentNumber) { this.agentNumber = agentNumber; }

    public PaymentInformation getPaymentInformation() { return paymentInformation; }
    public void setPaymentInformation(PaymentInformation paymentInformation) { this.paymentInformation = paymentInformation; }
}