package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.tripmate.TripMateBookResponse;
import com.travelinsurancemaster.model.webservice.tripmate.TripMateQuoteResponse;
import com.travelinsurancemaster.model.webservice.tripmate.TripMateRequestConstants;
import com.travelinsurancemaster.services.clients.MHRossClient;
import com.travelinsurancemaster.services.tripmate.DestinationCodes;
import org.apache.commons.collections4.CollectionUtils;
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

import java.text.SimpleDateFormat;
import java.util.Date;

;
;

/**
 * Created by ritchie on 8/4/15.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@Ignore
public class MHRossClientTest {

    @Autowired
    private MHRossClient mhRossClient;

    @Autowired
    private InsuranceMasterApiProperties apiProperties;

    @Autowired
    private TestCreditCards creditCards;

    private MultiValueMap<String, String> quoteParams = new LinkedMultiValueMap<>();
    private MultiValueMap<String, String> purchaseParams = new LinkedMultiValueMap<>();
    private String departDate;
    private String returnDate;

    @Before
    public void setDates() {
        departDate = TestUtils.getFormattedDate(TestUtils.getIncrementedDate(3), TripMateRequestConstants.DATE_FORMAT);
        returnDate = TestUtils.getFormattedDate(TestUtils.getIncrementedDate(8), TripMateRequestConstants.DATE_FORMAT);
    }

    @Before
    public void populateQuoteParams() {
        quoteParams.add(TripMateRequestConstants.LOCATION, MHRossClient.RCONSUMER);
        quoteParams.add(TripMateRequestConstants.UNAME, apiProperties.getmHRoss().getUser());
        quoteParams.add(TripMateRequestConstants.PWD, apiProperties.getmHRoss().getPassword());
        quoteParams.add(TripMateRequestConstants.TOTAL_TRAVS, "1");
        quoteParams.add(TripMateRequestConstants.DEPARTDATE, departDate);
        quoteParams.add(TripMateRequestConstants.RETURNDATE, returnDate);
        quoteParams.add(TripMateRequestConstants.INDIVIDUAL_DATES, TripMateRequestConstants.INDIVIDUAL_DATES_VALUE);
        quoteParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.AGE, "25");
        quoteParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.TRIPCOST, "1200");
        quoteParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.DEPARTDATE, departDate);
        quoteParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.RETURNDATE, returnDate);
        quoteParams.add(TripMateRequestConstants.STATE, StateCode.NJ.name());
        quoteParams.add(TripMateRequestConstants.COUNTRY, CountryCode.US.name());
    }

    @Before
    public void populatePurchaseParams() {
        purchaseParams.add(TripMateRequestConstants.LOCATION, MHRossClient.RCONSUMER);
        purchaseParams.add(TripMateRequestConstants.UNAME, apiProperties.getmHRoss().getUser());
        purchaseParams.add(TripMateRequestConstants.PWD, apiProperties.getmHRoss().getPassword());
        purchaseParams.add(TripMateRequestConstants.PRODUCTID, MHRossClient.ASSET_PRODUCT_CODE);
        purchaseParams.add(TripMateRequestConstants.TRAVELER_COUNT, "1");
        purchaseParams.add(TripMateRequestConstants.INDIVIDUAL_DATES, TripMateRequestConstants.INDIVIDUAL_DATES_VALUE);
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.PREFIX, TripMateRequestConstants.PREFIX_VALUE);
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.FIRSTNAME, "Erica");
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.LASTNAME, "Boling");
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.MIDDLENAME, "");
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.SUFFIX, TripMateRequestConstants.SUFFIX_VALUE);
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.AGE, "25");
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.DOB, "01/01/1980");
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.GENDER, TripMateRequestConstants.GENDER_VALUE);
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.TRIPCOST, "1000");
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.TRIPLENGTH, "11");
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.DEPARTDATE, departDate);
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.RETURNDATE, returnDate);
        purchaseParams.add(TripMateRequestConstants.TRAVELER_PREFIX + "1_" + TripMateRequestConstants.DEPOSITDATE, new SimpleDateFormat(TripMateRequestConstants.DATE_FORMAT).format(new Date()));
        purchaseParams.add(TripMateRequestConstants.BOOKING_NUMBER, "");
        purchaseParams.add(TripMateRequestConstants.BENEFICIARY, "Erica Boling");
        purchaseParams.add(TripMateRequestConstants.TRIPCOST, "1000");
        purchaseParams.add(TripMateRequestConstants.EMAIL, "test@tripmate.com");
        purchaseParams.add(TripMateRequestConstants.FAX, "");
        purchaseParams.add(TripMateRequestConstants.PHONE1, "9082272963");
        purchaseParams.add(TripMateRequestConstants.ADDRESS1, "83 Forest Drive");
        purchaseParams.add(TripMateRequestConstants.ADDRESS2, "");
        purchaseParams.add(TripMateRequestConstants.STATE, StateCode.NJ.name());
        purchaseParams.add(TripMateRequestConstants.ZIP, "08854");
        purchaseParams.add(TripMateRequestConstants.ZIP4, "");
        purchaseParams.add(TripMateRequestConstants.CITY, "Piscataway");
        purchaseParams.add(TripMateRequestConstants.COUNTRY, TripMateRequestConstants.COUNTRY_US);
        purchaseParams.add(TripMateRequestConstants.DEPARTDATE, departDate);
        purchaseParams.add(TripMateRequestConstants.RETURNDATE, returnDate);
        purchaseParams.add(TripMateRequestConstants.TRIP_LENGTH, "11");
        purchaseParams.add(TripMateRequestConstants.DEPOSITDATE, new SimpleDateFormat(TripMateRequestConstants.DATE_FORMAT).format(new Date()));
        purchaseParams.add(TripMateRequestConstants.RENTALCAR, "4");
        purchaseParams.add(TripMateRequestConstants.PREMIUM, "51");
        purchaseParams.add(TripMateRequestConstants.DESTINATION, String.valueOf(DestinationCodes.getDestinationByCode(StateCode.AL.name())));
        purchaseParams.add(TripMateRequestConstants.TRIPTYPE, TripMateRequestConstants.TRIP_TYPE_AIR);
        purchaseParams.add(TripMateRequestConstants.AIRLINE, TripMateRequestConstants.AIRLINE_VALUE);
        CreditCard mhRossCard = creditCards.getCreditCardByVendorCode(ApiVendor.MHRoss);
        purchaseParams.add(TripMateRequestConstants.CC_ADDRESS1, mhRossCard.getCcAddress());
        purchaseParams.add(TripMateRequestConstants.CC_ADDRESS2, "");
        purchaseParams.add(TripMateRequestConstants.CC_CITY, mhRossCard.getCcCity());
        purchaseParams.add(TripMateRequestConstants.CC_COUNTRY, TripMateRequestConstants.COUNTRY_US);
        purchaseParams.add(TripMateRequestConstants.CC_POSTALCODE, mhRossCard.getCcZipCode());
        purchaseParams.add(TripMateRequestConstants.CC_STATE, mhRossCard.getCcStateCode().name());
        purchaseParams.add(TripMateRequestConstants.CC_ZIP4, "");
        purchaseParams.add(TripMateRequestConstants.CC_EMAIL, "test@test.com");
        purchaseParams.add(TripMateRequestConstants.CC_PHONE1, mhRossCard.getCcPhone());
        purchaseParams.add(TripMateRequestConstants.CC_FIRSTNAME, mhRossCard.getCcName());
        purchaseParams.add(TripMateRequestConstants.CC_LASTNAME, " ");
        purchaseParams.add(TripMateRequestConstants.CC_MIDDLENAME, "");
        purchaseParams.add(TripMateRequestConstants.CC_PAYMENTTYPE, "v");
        purchaseParams.add(TripMateRequestConstants.CC_CODE, mhRossCard.getCcCode());
        purchaseParams.add(TripMateRequestConstants.CC_MONTH, mhRossCard.getCcExpMonth());
        purchaseParams.add(TripMateRequestConstants.CC_YEAR, mhRossCard.getCcExpYear().substring(2));
        purchaseParams.add(TripMateRequestConstants.CC_NUMBER, String.valueOf(mhRossCard.getCcNumber()));
    }

    @Test
    public void quoteTest() {
        TripMateQuoteResponse quoteResponse = mhRossClient.quote(quoteParams);
        Assert.assertTrue("No products returned!", !CollectionUtils.isEmpty(quoteResponse.plans));
    }

    @Test
    public void purchaseAssetProductTest() {
        String errorMesssage = null;
        TripMateBookResponse purchaseResponse = mhRossClient.purchase(purchaseParams);
        if (!CollectionUtils.isEmpty(purchaseResponse.getErrors())) {
            errorMesssage = purchaseResponse.getErrors().get(0).getErrorText();
        }
        Assert.assertTrue(errorMesssage, CollectionUtils.isEmpty(purchaseResponse.getErrors()));
    }

}
