package com.travelinsurancemaster.model.webservice.hccmis;

/**
 * Created by raman on 11.06.2019.
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.travelinsurancemaster.services.clients.HCCMedicalInsuranceServicesClient;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HCCPurchaseRequest {

    private String referId = HCCMedicalInsuranceServicesClient.REFER_ID;
    private String culture = HCCMedicalInsuranceServicesClient.CULTURE;
    private String showAuthCodes = "1";
    private String USDest;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/YYYY")
    private LocalDate coverageBeginDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/YYYY")
    private LocalDate coverageEndDt;
    private String primaryHomeCountry;
    private List<String> destinationList;
    private String beneficiary;
    private String physicallyLocatedState;
    private String physicallyLocatedCountry;
    private String policyMax;
    private String deductible;
    private String appName;
    private String primaryFlWork = HCCMedicalInsuranceServicesClient.WILL_WORK_IN_FLORIDA;
    private String mailOpt = "";
    private String onlineFulFill = HCCMedicalInsuranceServicesClient.FULFILLMENT_ONLINE;
    private String shippingCost = "0.00";
    private String sendToName;
    private String mailAddress1;
    private String mailAddress2;
    private String mailCity;
    private String mailCountry;
    private String mailState;
    private String mailZip;
    private String phone;
    private String emailAddress;
    private List<Applicant> applicantList;
    private List<SecondaryCoverage> secondaryCoverageList;
    private CreditCard creditCard;
}
