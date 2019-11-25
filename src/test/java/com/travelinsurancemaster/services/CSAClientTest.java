package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.clients.csa.xsd.*;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.services.clients.CSAClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;

;
;

/**
 * Created by Vlad on 06.02.2015.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@Ignore
public class CSAClientTest {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    private CSAClient client;

    public static final String PRODUCT_NAME = "GGA100";

    private Purchaserequest purchaserequest;
    private Quoterequest quoterequest;

    @Before
    public void setBookRequest() {
        purchaserequest = new Purchaserequest();
        purchaserequest.setActioncode(Actioncode.NEW);
        purchaserequest.setAff("CSATEST1");
        purchaserequest.setProducer("CSATEST1");
        purchaserequest.setProductclass(PRODUCT_NAME);
        purchaserequest.setBookingreservno("123456789AB");
        purchaserequest.setNuminsured(1);
        purchaserequest.setDepartdate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        purchaserequest.setReturndate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(7), DATE_FORMAT));
        purchaserequest.setInitdate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(-2), DATE_FORMAT));
        purchaserequest.setTriptype("Cruise");
        purchaserequest.setDestination("Europe");
        purchaserequest.setSupplier("Carnival Cruise Lines");
        Travelers travelers = new Travelers();
        purchaserequest.setTravelers(travelers);

        Travelers.Traveler traveler = new Travelers.Traveler();
        travelers.getTraveler().add(traveler);

        traveler.setTravelerfirstname("George");
        traveler.setTravelerlastname("Smith");
        traveler.setAge(26);
        traveler.setTripcost(BigDecimal.valueOf(1000));

        purchaserequest.setTripcost(BigDecimal.valueOf(1000));
        purchaserequest.setAddress1("26749 Princeton Ave.");
        purchaserequest.setCity("San Diego");
        purchaserequest.setState(StateCode.CA.name());
        purchaserequest.setZipcode("921236789");
        purchaserequest.setTelephonehome(BigDecimal.valueOf(8585551212d));
        purchaserequest.setPrintpolconfltr(3);
        purchaserequest.setEmailaddress("gzsmith@cox.net");
        purchaserequest.setAgentid("JONES_L");
        purchaserequest.setAgentemail("bobstours@earthlink.net");
        purchaserequest.setBeneficiaryfirstname("Fred");
        purchaserequest.setBeneficiarylastname("Harvey");
        purchaserequest.setBeneficiaryrelationship("Brother");
//        quoterequest.setPrice(BigDecimal.valueOf(67.91)); // expected price
        purchaserequest.setPaymentmethod("Vi");
        purchaserequest.setCcorcheckno(BigDecimal.valueOf(4111111111111111d));
        purchaserequest.setCcexpiration("0816");
        purchaserequest.setCcname("George Smith");
        purchaserequest.setCczipcode("921236789");

    }

    @Before
    public void setQuoteRequest() {
        quoterequest = new Quoterequest();
        quoterequest.setAff("CSATEST1");
        quoterequest.setProducer("CSATEST1");
        quoterequest.setTripcost(BigDecimal.valueOf(12000.00));
        quoterequest.setDestination(StateCode.AK.getCaption());
        Travelers travelers = new Travelers();
        quoterequest.setTravelers(travelers);

        Travelers.Traveler traveler = new Travelers.Traveler();
        travelers.getTraveler().add(traveler);
        traveler.setAge(45);
        traveler.setTripcost(BigDecimal.valueOf(12000.00));
        quoterequest.setTriptype("CRUISE");
        quoterequest.setProductclass(PRODUCT_NAME);
        quoterequest.setNuminsured(1);
        quoterequest.setDepartdate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        quoterequest.setReturndate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(7), DATE_FORMAT));
    }

    @Test
    public void testQuotдаe() {
        Response response = client.quote(quoterequest);
        String errorMessage = null;
        if (response.getErrorresponse() != null) {
            errorMessage = response.getErrorresponse().getError().getMessage();
        }
        assertNull(errorMessage, response.getErrorresponse());
        assertNotNull(response.getQuoteresponse());
        String product = response.getQuoteresponse().getProduct();
        assertEquals(PRODUCT_NAME, product);
    }

    @Test
    public void testBook() {
        Purchaseresponse response = client.book(purchaserequest);
        String errorMessage = null;
        if (response.getError() != null) {
            errorMessage = response.getError().getMessage();
        }
        assertNull(errorMessage, response.getError());
    }
}
