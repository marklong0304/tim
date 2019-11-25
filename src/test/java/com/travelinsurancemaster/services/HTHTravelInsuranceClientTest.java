package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.hthtravelinsurance.HTHTravelInsuranceMapper;
import com.travelinsurancemaster.services.clients.HTHTravelInsuranceClient;
import org.apache.commons.lang3.text.WordUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;

;
;

/**
 * Created by ritchie on 6/11/15.
 */
@Ignore
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class HTHTravelInsuranceClientTest {

    @Autowired
    private HTHTravelInsuranceClient hthTravelInsuranceClient;

    @Autowired
    private InsuranceMasterApiProperties apiProperties;

    @Autowired
    private TestCreditCards creditCards;

    @Autowired
    private PolicyQuoteParamService policyQuoteParamService;

    @Autowired
    private PolicyMetaService policyMetaService;

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String BIRTH_DATE = "09/13/1989";

    @Test
    public void purchaseExcursionTest() {
        MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        requestParameters.add(HTHTravelInsuranceMapper.P_PRD_TYPE_ID, HTHTravelInsuranceClient.EXCURSION_PRODUCT_CODE);
        requestParameters.add(HTHTravelInsuranceMapper.P_DEDUCTIBLE_LEVEL, HTHTravelInsuranceMapper.DEFAULT_DEDUCTIBLE_LEVEL);
        requestParameters.add(HTHTravelInsuranceMapper.P_MAXIMUM_BENEFIT_LEVEL, HTHTravelInsuranceMapper.DEFAULT_MAXIMUM_BENEFIT_LEVEL);
        requestParameters.add(HTHTravelInsuranceMapper.P_FULFILLMENT_TYPE, HTHTravelInsuranceMapper.EMAIL_FULFILLMENT_TYPE);
        requestParameters.add(HTHTravelInsuranceMapper.P_INSURED_DEPENDENTS, "1");
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("HTH75");
        Long priceDaily = policyQuoteParamService.getPolicyQuoteParam(new QuoteRequest(), policyMeta,
                Long.valueOf(HTHTravelInsuranceMapper.DEFAULT_DEDUCTIBLE_LEVEL),
                Long.valueOf(HTHTravelInsuranceMapper.DEFAULT_MAXIMUM_BENEFIT_LEVEL), 26l, null, null);
        Assert.assertNotNull(priceDaily);
        BigDecimal price = BigDecimal.valueOf(priceDaily).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(7));
        requestParameters.add(HTHTravelInsuranceMapper.P_AMOUNT, String.valueOf(price));
        CreditCard hTHTravelInsuranceCard = creditCards.getCreditCardByVendorCode(ApiVendor.HTHTravelInsurance);
        requestParameters.add(HTHTravelInsuranceMapper.P_CARDTYPE, WordUtils.capitalizeFully(hTHTravelInsuranceCard.getCcType().name()));
        requestParameters.add(HTHTravelInsuranceMapper.P_NAME_ON_CREDIT_CARD, hTHTravelInsuranceCard.getCcName());
        requestParameters.add(HTHTravelInsuranceMapper.P_CARDNUM, String.valueOf(hTHTravelInsuranceCard.getCcNumber()));
        requestParameters.add(HTHTravelInsuranceMapper.P_EXP_MONTH, hTHTravelInsuranceCard.getCcExpMonth());
        requestParameters.add(HTHTravelInsuranceMapper.P_EXP_YEAR, hTHTravelInsuranceCard.getCcExpYear());
        requestParameters.add(HTHTravelInsuranceMapper.P_LINK_ID, apiProperties.gethTHTravelInsurance().getUniqueId());
        requestParameters.add(HTHTravelInsuranceMapper.P_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(3), DATE_FORMAT));
        requestParameters.add(HTHTravelInsuranceMapper.P_RETURN_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(4), DATE_FORMAT));
        requestParameters.add(HTHTravelInsuranceMapper.P_FIRSTNAME, "Test");
        requestParameters.add(HTHTravelInsuranceMapper.P_MIDNAME, "T.");
        requestParameters.add(HTHTravelInsuranceMapper.P_LASTNAME, "Testerson");
        requestParameters.add(HTHTravelInsuranceMapper.P_DOB, BIRTH_DATE);
        requestParameters.add(HTHTravelInsuranceMapper.P_ADDRESS1, "53rd Street");
        requestParameters.add(HTHTravelInsuranceMapper.P_ADDRESS2, "");
        requestParameters.add(HTHTravelInsuranceMapper.P_CITY, "City");
        requestParameters.add(HTHTravelInsuranceMapper.P_STATE, StateCode.CO.name());
        requestParameters.add(HTHTravelInsuranceMapper.P_ZIP, "32118");
        requestParameters.add(HTHTravelInsuranceMapper.P_COUNTRY, "USA");
        requestParameters.add(HTHTravelInsuranceMapper.P_PHONE, "1234567890");
        requestParameters.add(HTHTravelInsuranceMapper.P_EMAIL, "test@mail.com");
        String purchaseRequest = hthTravelInsuranceClient.getSingleTripPurchaseResponse(requestParameters);
        Assert.assertTrue(purchaseRequest, !purchaseRequest.contains(HTHTravelInsuranceClient.ERROR));
    }

    @Test
    public void purchaseProtectorTest() {
        MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        requestParameters.add(HTHTravelInsuranceMapper.PRD_TYPE_ID, HTHTravelInsuranceClient.TRIP_PROTECTOR_PREFFERED_PRODUCT_CODE);
        requestParameters.add(HTHTravelInsuranceMapper.AGENT_ID, apiProperties.gethTHTravelInsurance().getUniqueId());
        requestParameters.add(HTHTravelInsuranceMapper.TRAVEL_DESTINATION, WordUtils.capitalizeFully(CountryCode.AL.getCaption()));
        requestParameters.add(HTHTravelInsuranceMapper.INITIAL_DEPOSIT_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(-4), DATE_FORMAT));
        requestParameters.add(HTHTravelInsuranceMapper.FINAL_PAYMENT_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(-2), DATE_FORMAT));
        requestParameters.add(HTHTravelInsuranceMapper.TRIP_DEPARTURE_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        requestParameters.add(HTHTravelInsuranceMapper.TRIP_RETURN_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(4), DATE_FORMAT));
        requestParameters.add(HTHTravelInsuranceMapper.AFF_CALC_TOTAL_COST, "69");
        requestParameters.add(HTHTravelInsuranceMapper.PRIMARY_AIRLINE, "");
        requestParameters.add(HTHTravelInsuranceMapper.CRUISE_TOUR_OPERATOR, "");
        requestParameters.add(HTHTravelInsuranceMapper.AGENT_EMAIL, apiProperties.gethTHTravelInsurance().getAgentEmail());
        requestParameters.add(HTHTravelInsuranceMapper.MED_LIMIT, "1000000");
        requestParameters.add(HTHTravelInsuranceMapper.DEDUCTIBLE, HTHTravelInsuranceMapper.DEFAULT_DEDUCTIBLE_LEVEL);
        requestParameters.add(HTHTravelInsuranceMapper.FULFILLMENT_TYPE, HTHTravelInsuranceMapper.EMAIL_FULFILLMENT_TYPE);
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_FIRSTNAME, "Test");
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_MIDNAME, "T.");
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_LASTNAME, "Testerson");
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_DOB, BIRTH_DATE);
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_TRIP_COST, "1200");
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_ADDRESS1, "53rd Street");
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_ADDRESS2, "");
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_CITY, "City");
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_STATE, StateCode.FL.name());
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_ZIP, "32118");
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_COUNTRY, "USA");
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_TEL, "1234567890");
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_WORK_PHONE, "1234567890");
        requestParameters.add(HTHTravelInsuranceMapper.PRIM_MEM_EMAIL, "test@mail.com");
        requestParameters.add(HTHTravelInsuranceMapper.BENEFICIARY_FIRSTNAME, "Test");
        requestParameters.add(HTHTravelInsuranceMapper.BENEFICIARY_LASTNAME, "Testerson");
        requestParameters.add(HTHTravelInsuranceMapper.BENEFICIARY_RELATION, "");
        CreditCard hTHTravelInsuranceCard = creditCards.getCreditCardByVendorCode(ApiVendor.HTHTravelInsurance);
        requestParameters.add(HTHTravelInsuranceMapper.CREDIT_CARD_NUMBER, String.valueOf(hTHTravelInsuranceCard.getCcNumber()));
        requestParameters.add(HTHTravelInsuranceMapper.CREDIT_CARD_TYPE, WordUtils.capitalizeFully(hTHTravelInsuranceCard.getCcType().name()));
        requestParameters.add(HTHTravelInsuranceMapper.NAME_ON_CREDIT_CARD, hTHTravelInsuranceCard.getCcName());
        requestParameters.add(HTHTravelInsuranceMapper.CREDIT_CARD_MONTH, hTHTravelInsuranceCard.getCcExpMonth());
        requestParameters.add(HTHTravelInsuranceMapper.CREDIT_CARD_YEAR, hTHTravelInsuranceCard.getCcExpYear());
        String purchaseRequest = hthTravelInsuranceClient.getTripProtectorPurchaseResponse(requestParameters);
        Assert.assertTrue(purchaseRequest, !purchaseRequest.contains(HTHTravelInsuranceClient.ERROR));
    }

    @Test
    public void purchaseTravelGapTest() {
        MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        requestParameters.add(HTHTravelInsuranceMapper.P_PRD_TYPE_ID, HTHTravelInsuranceClient.TRAVEL_GAP_GOLD_PRODUCT_CODE);
        requestParameters.add(HTHTravelInsuranceMapper.P_FULFILLMENT_TYPE, HTHTravelInsuranceMapper.EMAIL_FULFILLMENT_TYPE);
        requestParameters.add(HTHTravelInsuranceMapper.P_INSURED_DEPENDENTS, "1");
        requestParameters.add(HTHTravelInsuranceMapper.P_AMOUNT, "175");
        CreditCard hTHTravelInsuranceCard = creditCards.getCreditCardByVendorCode(ApiVendor.HTHTravelInsurance);
        requestParameters.add(HTHTravelInsuranceMapper.P_CARDTYPE, WordUtils.capitalizeFully(hTHTravelInsuranceCard.getCcType().name()));
        requestParameters.add(HTHTravelInsuranceMapper.P_CARDNUM, String.valueOf(hTHTravelInsuranceCard.getCcNumber()));
        requestParameters.add(HTHTravelInsuranceMapper.P_NAME_ON_CREDIT_CARD, hTHTravelInsuranceCard.getCcName());
        requestParameters.add(HTHTravelInsuranceMapper.P_EXP_MONTH, hTHTravelInsuranceCard.getCcExpMonth());
        requestParameters.add(HTHTravelInsuranceMapper.P_EXP_YEAR, hTHTravelInsuranceCard.getCcExpYear());
        requestParameters.add(HTHTravelInsuranceMapper.P_LINK_ID, apiProperties.gethTHTravelInsurance().getUniqueId());
        requestParameters.add(HTHTravelInsuranceMapper.P_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        requestParameters.add(HTHTravelInsuranceMapper.P_FIRSTNAME, "Test");
        requestParameters.add(HTHTravelInsuranceMapper.P_MIDNAME, "T.");
        requestParameters.add(HTHTravelInsuranceMapper.P_LASTNAME, "Testerson");
        requestParameters.add(HTHTravelInsuranceMapper.P_GENDER, "MALE");
        requestParameters.add(HTHTravelInsuranceMapper.P_DOB, BIRTH_DATE);
        requestParameters.add(HTHTravelInsuranceMapper.P_ADDRESS1, "53rd Street");
        requestParameters.add(HTHTravelInsuranceMapper.P_ADDRESS2, "");
        requestParameters.add(HTHTravelInsuranceMapper.P_CITY, "City");
        requestParameters.add(HTHTravelInsuranceMapper.P_STATE, StateCode.FL.name());
        requestParameters.add(HTHTravelInsuranceMapper.P_ZIP, "32118");
        requestParameters.add(HTHTravelInsuranceMapper.P_COUNTRY, "USA");
        requestParameters.add(HTHTravelInsuranceMapper.P_PHONE, "1234567890");
        requestParameters.add(HTHTravelInsuranceMapper.P_EMAIL, "test@mail.com");
        requestParameters.add(HTHTravelInsuranceMapper.P_BENEFICIARY_FIRSTNAME, "Test");
        requestParameters.add(HTHTravelInsuranceMapper.P_BENEFICIARY_LASTNAME, "Testerson");
        String purchaseRequest = hthTravelInsuranceClient.getMultiTripPurchaseResponse(requestParameters);
        Assert.assertTrue(purchaseRequest, !purchaseRequest.contains(HTHTravelInsuranceClient.ERROR));
    }
}
