package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.clients.travelex.InsuranceBookRS;
import com.travelinsurancemaster.clients.travelex.PaymentConfigurationResponse;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.travelexinsurance.TravelexInsuranceMapper;
import com.travelinsurancemaster.services.clients.TravelexInsuranceClient;

import java.util.Objects;

import org.apache.commons.lang3.text.WordUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
* Created by ritchie on 4/24/15.
*/
@Ignore
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class TravelexInsuranceClientTest {

    @Autowired
    private InsuranceMasterApiProperties apiProperties;

    @Autowired
    private TravelexInsuranceClient travelexInsuranceClient;

    @Autowired
    private TestCreditCards creditCards;

    private static final Logger log = LoggerFactory.getLogger(TravelexInsuranceClientTest.class);
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String BIRTH_DATE = "04/24/1984";
    private String errorMessage;

    @Test
    public void quote() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(TravelexInsuranceMapper.STR_LOCATION, apiProperties.getTravelexInsurance().getLocationNumber());
        params.add(TravelexInsuranceMapper.STR_DEPARTURE_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        params.add(TravelexInsuranceMapper.STR_RETURN_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(7), DATE_FORMAT));
        params.add(TravelexInsuranceMapper.STR_PRODUCT, TravelexInsuranceClient.TRAVEL_BASIC_PRODUCT_CODE);
        params.add(TravelexInsuranceMapper.STR_FORM, "");
        params.add(TravelexInsuranceMapper.STR_COVERAGE_TYPE, TravelexInsuranceMapper.COVERAGE_TRIP_CANCELLATION);
        params.add(TravelexInsuranceMapper.INT_FAC_PREMIUM, "0");
        params.add(TravelexInsuranceMapper.INT_NUM_TRAVELERS, "1");
        params.add(TravelexInsuranceMapper.STR_DOB, BIRTH_DATE);
        params.add(TravelexInsuranceMapper.INT_TRIP_COST, "1200");
        params.add(TravelexInsuranceMapper.STR_STATE, StateCode.CA.name());
        params.add(TravelexInsuranceMapper.STR_COUNTRY, WordUtils.capitalizeFully(CountryCode.US.getCaption()));
        params.add(TravelexInsuranceMapper.STR_REFERENCE_ID, "");
        params.add(TravelexInsuranceMapper.STR_COLLISION_END_DATE, "");
        params.add(TravelexInsuranceMapper.STR_COLLISION_START_DATE, "");
        params.add(TravelexInsuranceMapper.STR_MED_UPGRADE, "");
        InsuranceBookRS quoteResponse = travelexInsuranceClient.quote(params);
        if (quoteResponse.getWarnings() != null && !quoteResponse.getWarnings().getWarning().isEmpty()) {
            errorMessage = quoteResponse.getWarnings().getWarning();
        }
        Assert.assertNull(errorMessage, quoteResponse.getWarnings());
    }

    @Test
    public void paymentConfiguration() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(TravelexInsuranceMapper.LOCATION_NUMBER, apiProperties.getTravelexInsurance().getLocationNumber());
        params.add(TravelexInsuranceMapper.USER_ID, apiProperties.getTravelexInsurance().getUser());
        params.add(TravelexInsuranceMapper.PASSWORD, apiProperties.getTravelexInsurance().getPassword());
        params.add(TravelexInsuranceMapper.PRODUCT_FORM_NUMBER, "TBB-1117");
        params.add(TravelexInsuranceMapper.HOST_URL, "localhost");
        PaymentConfigurationResponse configResponse = travelexInsuranceClient.paymentConfiguration(params);
        if (Objects.nonNull(configResponse)) {
            errorMessage = configResponse.getErrorMessage();
        }
        Assert.assertNull(errorMessage, errorMessage);
    }

    @Test
    public void purchase() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(TravelexInsuranceMapper.STR_USER_ID, apiProperties.getTravelexInsurance().getUser());
        params.add(TravelexInsuranceMapper.STR_PASSWORD, apiProperties.getTravelexInsurance().getPassword());
        params.add(TravelexInsuranceMapper.STR_BROKER_LOCATION, "");
        params.add(TravelexInsuranceMapper.STR_AGENCY_LOCATION, apiProperties.getTravelexInsurance().getLocationNumber());
        params.add(TravelexInsuranceMapper.STR_AGENT_CODE, "");
        params.add(TravelexInsuranceMapper.STR_GROUP_ID, "");
        params.add(TravelexInsuranceMapper.STR_INVOICE_NUMBER, "");
        params.add(TravelexInsuranceMapper.DBL_TOTAL_POLICY_COST, "0"); // Not applicable
        params.add(TravelexInsuranceMapper.STR_DEPARTURE_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        params.add(TravelexInsuranceMapper.STR_RETURN_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(7), DATE_FORMAT));
        params.add(TravelexInsuranceMapper.STR_PURCHASE_DATE, TestUtils.getFormattedDate(TestUtils.getIncrementedDate(0), DATE_FORMAT));
        params.add(TravelexInsuranceMapper.STR_PRODUCT, TravelexInsuranceClient.TRAVEL_BASIC_PRODUCT_CODE);
        params.add(TravelexInsuranceMapper.STR_FORM, "");
        params.add(TravelexInsuranceMapper.STR_COVERAGE_TYPE, TravelexInsuranceMapper.COVERAGE_TRIP_CANCELLATION);
        params.add(TravelexInsuranceMapper.INT_FAC_PREMIUM, "0");
        params.add(TravelexInsuranceMapper.INT_NUM_TRAVELERS, "1");
        params.add(TravelexInsuranceMapper.STR_FIRST_NAME, "firstName");
        params.add(TravelexInsuranceMapper.STR_LAST_NAME, "lastName");
        params.add(TravelexInsuranceMapper.STR_DOB, BIRTH_DATE);
        params.add(TravelexInsuranceMapper.INT_TRIP_COST, "1000");
        params.add(TravelexInsuranceMapper.STR_MED_UPGRADE, "");
        params.add(TravelexInsuranceMapper.STR_ADDRESS_1, "Street");
        params.add(TravelexInsuranceMapper.STR_ADDRESS_2, "");
        params.add(TravelexInsuranceMapper.STR_CITY, "City");
        params.add(TravelexInsuranceMapper.STR_STATE, StateCode.FL.name());
        params.add(TravelexInsuranceMapper.STR_COUNTRY, WordUtils.capitalizeFully(CountryCode.US.getCaption()));
        params.add(TravelexInsuranceMapper.STR_ZIP, "12345");
        params.add(TravelexInsuranceMapper.STR_PHONE, "1234567890");
        params.add(TravelexInsuranceMapper.STR_FAX, "");
        params.add(TravelexInsuranceMapper.STR_EMAIL, "test@mail.com");
        params.add(TravelexInsuranceMapper.STR_COLLISION_START_DATE, "");
        params.add(TravelexInsuranceMapper.STR_COLLISION_END_DATE, "");
        params.add(TravelexInsuranceMapper.STR_BENEFICIARY, "firstName lastName");
        params.add(TravelexInsuranceMapper.STR_PAYMENT_TYPE, TravelexInsuranceMapper.PAYMENT_TYPE_VISA);
        CreditCard travelexCreditCard = creditCards.getCreditCardByVendorCode(ApiVendor.TravelexInsurance);
        params.add(TravelexInsuranceMapper.TOKEN_CC_NUMBER, travelexCreditCard.getTokenCcNumber());
        params.add(TravelexInsuranceMapper.STR_MASKED_CARD_NUMBER, travelexCreditCard.getCcNumber().toString());
        params.add(TravelexInsuranceMapper.STR_CC_CARD_HOLDER_NAME, travelexCreditCard.getCcName());
        params.add(TravelexInsuranceMapper.STR_CC_EXPIRATION_MONTH, travelexCreditCard.getCcExpMonth());
        params.add(TravelexInsuranceMapper.STR_CC_EXPIRATION_YEAR, travelexCreditCard.getCcExpYear());
        params.add(TravelexInsuranceMapper.STR_CC_AUTHORIZATION_NUMBER, "");
        params.add(TravelexInsuranceMapper.STR_EXTERNALLY_AUTHORIZED, "");
        params.add(TravelexInsuranceMapper.STR_CHECK_NUMBER, "");
        params.add(TravelexInsuranceMapper.STR_CRUISE_LINE, "");
        params.add(TravelexInsuranceMapper.STR_TOUR_OPERATOR, "");
        params.add(TravelexInsuranceMapper.STR_AIRLINE, "");
        params.add(TravelexInsuranceMapper.STR_DESTINATION, CountryCode.US.name()); // todo: check destination
        params.add(TravelexInsuranceMapper.STR_FLIGHT_NUMBER, "");
        InsuranceBookRS purchaseResponse = travelexInsuranceClient.purchase(params);
        if (purchaseResponse.getWarnings() != null && !purchaseResponse.getWarnings().getWarning().isEmpty()) {
            errorMessage = purchaseResponse.getWarnings().getWarning();
        }
        Assert.assertNull(errorMessage, purchaseResponse.getWarnings());
    }
}
