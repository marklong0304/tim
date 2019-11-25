package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.usaassist.UsaAssistMapper;
import com.travelinsurancemaster.model.webservice.usaassist.UsaAssistPurchaseResponse;
import com.travelinsurancemaster.model.webservice.usaassist.UsaAssistQuoteResponse;
import com.travelinsurancemaster.services.clients.UsaAssistClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

;
;

/**
 * Created by ritchie on 10/1/15.
 */
@Ignore
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class UsaAssistClientTest {

    @Autowired
    private UsaAssistClient usaAssistClient;

    @Autowired
    private InsuranceMasterApiProperties apiProperties;

    @Autowired
    private TestCreditCards creditCards;

    private MultiValueMap<String, String> purchaseParams = new LinkedMultiValueMap<>();
    private MultiValueMap<String, String> quoteParams = new LinkedMultiValueMap<>();
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Before
    public void populateParams() {
        populateQuoteParams();
        populatePurchaseParams();
    }

    private void populateQuoteParams() {
        quoteParams.add(UsaAssistMapper.AFFILIATE, apiProperties.getUsaAssist().getUser());
        quoteParams.add(UsaAssistMapper.PASSWORD, apiProperties.getUsaAssist().getPassword());
        quoteParams.add(UsaAssistMapper.COUNTRY_CODE, CountryCode.US.name());
        quoteParams.add(UsaAssistMapper.START, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(3), DATE_FORMAT));
        quoteParams.add(UsaAssistMapper.END, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(7), DATE_FORMAT));
        quoteParams.add(UsaAssistMapper.DESTINATION_CODE, CountryCode.AL.name());
        quoteParams.add(UsaAssistMapper.TRAVELERS, "1");
        quoteParams.add(UsaAssistMapper.COINSURANCE, "");
        quoteParams.add(UsaAssistMapper.FLIGHT_COVERAGE, "");
        quoteParams.add(UsaAssistMapper.PLAN_CODE, UsaAssistClient.TRAVEL_MEDICAL_STANDARD_CODE);
        quoteParams.add(UsaAssistMapper.TRAVELING, "");
        quoteParams.add(UsaAssistMapper.AGES, "25");
    }

    private void populatePurchaseParams() {
        purchaseParams.add(UsaAssistMapper.AFFILIATE, apiProperties.getUsaAssist().getUser());
        purchaseParams.add(UsaAssistMapper.PASSWORD, apiProperties.getUsaAssist().getPassword());
        purchaseParams.add(UsaAssistMapper.TEST, "true");
        purchaseParams.add(UsaAssistMapper.LANG, "");
        purchaseParams.add(UsaAssistMapper.PLAN_CODE, UsaAssistClient.TRAVEL_MEDICAL_STANDARD_CODE);
        purchaseParams.add(UsaAssistMapper.CANCELLATION, "");
        purchaseParams.add(UsaAssistMapper.COINSURANCE, "");
        purchaseParams.add(UsaAssistMapper.FLIGHT_COVERAGE, "");
        purchaseParams.add(UsaAssistMapper.COUNTRY_CODE, CountryCode.US.name());
        purchaseParams.add(UsaAssistMapper.STATE, StateCode.CA.name());
        purchaseParams.add(UsaAssistMapper.CITY, "city");
        purchaseParams.add(UsaAssistMapper.ADDRESS, "address");
        purchaseParams.add(UsaAssistMapper.ZIP_CODE, "90003");
        purchaseParams.add(UsaAssistMapper.PHONE, "1234567890");
        purchaseParams.add(UsaAssistMapper.EMAIL, "test@gmail.com");
        purchaseParams.add(UsaAssistMapper.DESTINATION_CODE, CountryCode.AL.name());
        purchaseParams.add(UsaAssistMapper.START, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(3), DATE_FORMAT));
        purchaseParams.add(UsaAssistMapper.END, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(7), DATE_FORMAT));
        purchaseParams.add(UsaAssistMapper.OPERATOR, "");
        purchaseParams.add(UsaAssistMapper.TRAVELERS, "1");
        purchaseParams.add(UsaAssistMapper.TRIP_COST, "0");
        CreditCard usaAssistCard = creditCards.getCreditCardByVendorCode(ApiVendor.UsaAssist);
        purchaseParams.add(UsaAssistMapper.CARD_NUM, String.valueOf(usaAssistCard.getCcNumber()));
        String expirationDate = usaAssistCard.getCcExpMonth() + "/" + usaAssistCard.getCcExpYear();
        purchaseParams.add(UsaAssistMapper.CARD_EXP, expirationDate);
        purchaseParams.add(UsaAssistMapper.CARD_CODE, usaAssistCard.getCcCode());
        purchaseParams.add(UsaAssistMapper.CARD_FNAME, usaAssistCard.getCcName());
        purchaseParams.add(UsaAssistMapper.CARD_LNAME, "");
        purchaseParams.add(UsaAssistMapper.CARD_ZIP, usaAssistCard.getCcZipCode());
        purchaseParams.add(UsaAssistMapper.CARD_COUNTRY, usaAssistCard.getCcCountry().name());
        purchaseParams.add(UsaAssistMapper.CUSTOMER_IP, ""); // todo: what ip?
        UsaAssistQuoteResponse quoteResponse = getQuoteResponse();
        purchaseParams.add(UsaAssistMapper.TOTAL, String.valueOf(quoteResponse.getTotal()));
        purchaseParams.add(UsaAssistMapper.LAST_NAME, "Last");
        purchaseParams.add(UsaAssistMapper.FIRST_NAME, "First");
        purchaseParams.add(UsaAssistMapper.DOB, "1990-01-01");
        purchaseParams.add(UsaAssistMapper.PASSPORT, ""); // todo: required
        purchaseParams.add(UsaAssistMapper.RELATIONSHIP, UsaAssistMapper.RELATIONSHIP_PRIMARY);
    }

    @Test
    public void quoteTravelMedicalStandardPlanTest() {
        UsaAssistQuoteResponse quoteResponse = getQuoteResponse();
        Assert.assertTrue("Plan code is not equals!", quoteResponse.getPlanCode().equals(UsaAssistClient.TRAVEL_MEDICAL_STANDARD_CODE));
        Assert.assertNotNull("Price is null!", quoteResponse.getTotal());
    }

    private UsaAssistQuoteResponse getQuoteResponse() {
        return usaAssistClient.quote(quoteParams);
    }

    @Test
    public void purchaseTravelMedicalStandardPlanTest() {
        UsaAssistPurchaseResponse purchaseResponse = usaAssistClient.purchase(purchaseParams);
        String errorText = null;
        if (purchaseResponse.getError() != null) {
            errorText = purchaseResponse.getError().getText();
        }
        Assert.assertNull(errorText, purchaseResponse.getError());
    }
}
