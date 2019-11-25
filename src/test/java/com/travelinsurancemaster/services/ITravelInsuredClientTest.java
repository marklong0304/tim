package com.travelinsurancemaster.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.clients.itravelinsured.*;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.services.clients.ITravelInsuredClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;

;
;

/**
 * @author Alexander.Isaenco
 */
@Ignore
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class ITravelInsuredClientTest {
    private static final Logger log = LoggerFactory.getLogger(ITravelInsuredClientTest.class);

    private static final String DATE_FORMAT = "MM/dd/yyyy";

    @Autowired
    private InsuranceMasterApiProperties apiProperties;

    @Autowired
    private ITravelInsuredClient iTravelInsuredClient;

    @Test
    public void hello() {
        Hello helloRequest = new Hello();
        HelloResponse helloResponse = iTravelInsuredClient.hello(helloRequest);
        String result = helloResponse.getHelloResult();

        Assert.assertEquals("I am here", result);

    }

    @Test
    public void quote() {
        Quote quoteRequest = new Quote();
        quoteRequest.setUserId(apiProperties.getiTravelInsured().getUser());
        quoteRequest.setPassword(apiProperties.getiTravelInsured().getPassword());
        quoteRequest.setState(StateCode.AA.name());
        quoteRequest.setDepartDate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        quoteRequest.setReturnDate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(7), DATE_FORMAT));
        quoteRequest.setPolicyType(ITravelInsuredClient.TRAVEL_LITE_CODE);
        Traveler traveler1 = ITravelInsuredClient.populateTraveler(null/*firstName*/, null/*lastName*/, BigDecimal.valueOf(1200.50), 1975, apiProperties.getiTravelInsured().getReferralFeePercentage(), apiProperties.getiTravelInsured().getReferralFeeDollarAmt(), false);
        Traveler traveler2 = ITravelInsuredClient.populateTraveler(null/*firstName*/, null/*lastName*/, BigDecimal.valueOf(1200.50), 1978, apiProperties.getiTravelInsured().getReferralFeePercentage(), apiProperties.getiTravelInsured().getReferralFeeDollarAmt(), false);
        ArrayList<Traveler> travelersList = new ArrayList<>();
        travelersList.add(traveler1);
        travelersList.add(traveler2);
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        // Traveler for Quote contains only 4 filled values, others are null
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(travelersList);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        quoteRequest.setTravelers(jsonString);
        QuoteResponse quoteResponse = iTravelInsuredClient.quote(quoteRequest);
        String errorMessage = null;
        if (!quoteResponse.getQuoteResult().getErrors().getNotice().isEmpty()) {
            errorMessage = quoteResponse.getQuoteResult().getErrors().getNotice().get(0).getMessage();
        }
        Assert.assertTrue(errorMessage, quoteResponse.getQuoteResult().isSuccess());
    }

    @Test
    public void purchase() {
        Purchase purchaseRequest = new Purchase();
        purchaseRequest.setUserId(apiProperties.getiTravelInsured().getUser());
        purchaseRequest.setPassword(apiProperties.getiTravelInsured().getPassword());
        purchaseRequest.setState(StateCode.MT.name());
        purchaseRequest.setDepartDate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        purchaseRequest.setReturnDate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(7), DATE_FORMAT));
        purchaseRequest.setPolicyType(iTravelInsuredClient.TRAVEL_LITE_CODE);
        Traveler traveler = ITravelInsuredClient.populateTraveler("John", "Smith", BigDecimal.valueOf(1200.50), 1975, apiProperties.getiTravelInsured().getReferralFeePercentage(), apiProperties.getiTravelInsured().getReferralFeeDollarAmt(), false);
        ArrayList<Traveler> travelersPurchase = new ArrayList<>();
        travelersPurchase.add(traveler);
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        // Traveler for Quote contains only 4 filled values
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(travelersPurchase);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        purchaseRequest.setTravelers(jsonString);
        purchaseRequest.setProcessor(""); // optional
        purchaseRequest.setEmail("test@gmail.com");
        purchaseRequest.setGroupName(""); // optional
        purchaseRequest.setAddress1("53rd St.");
        purchaseRequest.setAddress2(""); // optional
        purchaseRequest.setCity("Gulfport");
        purchaseRequest.setPostalCode("60029");
        purchaseRequest.setCountry(CountryCode.US.name());
        purchaseRequest.setPhone("123-456-789");
        purchaseRequest.setTravelerEmail("test@gmail.com"); // optional
        purchaseRequest.setSendCvlViaPostal("false");
        purchaseRequest.setTripType("1");
        purchaseRequest.setTripDestination("3");
        purchaseRequest.setOutsideAgentPin(""); // optional
        purchaseRequest.setAgentInvoice(""); // optional
        purchaseRequest.setPaymentMethod("0");
        purchaseRequest.setPayeeFirstName("Ritchie");
        purchaseRequest.setPayeeLastName("Zet");
        purchaseRequest.setPayeeAddress("53rd St.");
        purchaseRequest.setPayeeCity("Gulfport");
        purchaseRequest.setPayeeState(StateCode.CA.name());
        purchaseRequest.setPayeePostalCode("60029");
        purchaseRequest.setPayeeCountry(CountryCode.US.name());
        purchaseRequest.setCardNumber("4112344112344113");
        purchaseRequest.setCardBrand("VI");
        purchaseRequest.setExpMonth("10");
        purchaseRequest.setExpYear("2017");
        purchaseRequest.setBankName(""); // optional
        purchaseRequest.setBankAcctNum(""); // optional
        purchaseRequest.setBankABACode(""); // optional
        PurchaseResponse purchaseResponse = iTravelInsuredClient.purchase(purchaseRequest);
        String errorMessage = null;
        if (!purchaseResponse.getPurchaseResult().getErrors().getNotice().isEmpty()) {
            errorMessage = purchaseResponse.getPurchaseResult().getErrors().getNotice().get(0).getMessage();
        }
        Assert.assertTrue(errorMessage, purchaseResponse.getPurchaseResult().isSuccess());
    }
}
