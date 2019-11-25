package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.tripmate.TripMateBookResponse;
import com.travelinsurancemaster.model.webservice.tripmate.TripMateError;
import com.travelinsurancemaster.model.webservice.tripmate.TripMateQuoteResponse;
import com.travelinsurancemaster.model.webservice.tripmate.TripMateTraveler;
import com.travelinsurancemaster.model.webservice.tripmate.travelsafe.TravelSafeBookRequest;
import com.travelinsurancemaster.model.webservice.tripmate.travelsafe.TravelSafeQuoteRequest;
import com.travelinsurancemaster.services.clients.TravelSafeClient;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

;
;

/**
 * Created by Vlad on 05.02.2015.
 */
@Ignore
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class TravelSafeClientTest {

    TravelSafeBookRequest bookRequest;
    TravelSafeQuoteRequest quoteRequest;

    @Autowired
    private TravelSafeClient travelSafeClient;

    @Before
    public void setQuoteRequest() {
        quoteRequest = new TravelSafeQuoteRequest();

        quoteRequest.location = "tmtest01";
        quoteRequest.uname = "test";
        quoteRequest.pwd = "12345";
        quoteRequest.total_travs = "1";
        quoteRequest.departdate = TestUtils.getIncrementedLocalDate(2);
        quoteRequest.returndate = TestUtils.getIncrementedLocalDate(7);
        quoteRequest.individual_dates = "0";
        List<TripMateTraveler> travelers = quoteRequest.travelers;
        TripMateTraveler traveler = new TripMateTraveler();
        travelers.add(traveler);
        traveler.age = 25;
        traveler.tripcost = BigDecimal.valueOf(1000);
        traveler.departdate = TestUtils.getIncrementedLocalDate(2);
        traveler.returndate = TestUtils.getIncrementedLocalDate(7);
        traveler.depositdate = TestUtils.getIncrementedLocalDate(-2);
        quoteRequest.state = StateCode.NJ.name(); // todo: need or not?
        quoteRequest.country = CountryCode.US.name();
    }

    @Before
    public void setBookRequest() {
        TripMateTraveler traveler = new TripMateTraveler();

        traveler.prefix = "Mrs.";
        traveler.firstname = "Erica";
        traveler.lastname = "Boling";
        traveler.middlename = "";
        traveler.suffix = "Jr.";
        traveler.age = 25;
        traveler.dob = "";
        traveler.gender = "M";
        traveler.tripcost = BigDecimal.valueOf(1000);
        traveler.triplength = 11;
        traveler.departdate = TestUtils.getIncrementedLocalDate(2);
        traveler.returndate = TestUtils.getIncrementedLocalDate(7);
        traveler.depositdate = TestUtils.getIncrementedLocalDate(-2);

        bookRequest = new TravelSafeBookRequest();
        bookRequest.location = "tmtest01";
        bookRequest.uname = "test";
        bookRequest.pwd = "12345";
        bookRequest.productID = "6028";
        bookRequest.traveler_count = "1";
        bookRequest.individual_dates = "0";

        bookRequest.travelers.add(traveler);
        bookRequest.booking_number = "";
        bookRequest.beneficiary = "Alberto Artasanchez";
        bookRequest.tripcost = "1000";
        bookRequest.email = "test@tripmate.com";
        bookRequest.fax = "";
        bookRequest.phone1 = "9082272963";
        bookRequest.address1 = "83 Forest Drive";
        bookRequest.address2 = "";
        bookRequest.state = StateCode.NJ.name();
        bookRequest.zip = "08854";
        bookRequest.zip4 = "";
        bookRequest.city = "Piscataway";
        bookRequest.country = "1";
        bookRequest.departdate = TestUtils.getIncrementedLocalDate(2);
        bookRequest.returndate = TestUtils.getIncrementedLocalDate(7);
        bookRequest.trip_length = "11";
        bookRequest.depositdate = LocalDate.now();
        bookRequest.rentalcar = "4";
        bookRequest.premium = "45";
        bookRequest.destination = "520";
        bookRequest.triptype = "1";
        bookRequest.airline = "505";
        bookRequest.tourop = "505";
        bookRequest.cruiseline = "505";
        bookRequest.miscprov = "505";
        bookRequest.option249 = "0";
        bookRequest.option250 = "0";
        bookRequest.option251 = "0";
        bookRequest.option252 = "0";
        bookRequest.option253 = "0";
        bookRequest.option254 = "0";
        bookRequest.option255 = "0";
        bookRequest.option256 = "0";
        bookRequest.option257 = "0";
        bookRequest.cc_address1 = "83 Forest Drive";
        bookRequest.cc_address2 = "";
        bookRequest.cc_city = "Piscataway";
        bookRequest.cc_country = "1"; // 1 - United States,2 - Canada

        bookRequest.cc_postalcode = "67037";
        bookRequest.cc_state = StateCode.NJ.name();
        bookRequest.cc_zip4 = "";
        bookRequest.cc_email = "test@test.com";
        bookRequest.cc_phone1 = "9082272963";
        bookRequest.cc_firstname = "Erica";
        bookRequest.cc_lastname = "C.Boling";
        bookRequest.cc_middlename = "";
        bookRequest.cc_paymenttype = "v";
        bookRequest.cc_code = "123";
        bookRequest.cc_month = "02";
        bookRequest.cc_year = "19";
        bookRequest.cc_number = "4111111111111111";
    }

    @Test
    public void quoteTest() throws IOException {
        TripMateQuoteResponse quoteResponse = travelSafeClient.quote(quoteRequest);
        Assert.assertTrue("No products returned!", !CollectionUtils.isEmpty(quoteResponse.plans));
    }

    @Test
    public void bookTest() throws IOException {
        String errorMesssage = null;
        TripMateBookResponse purchaseResponse = travelSafeClient.book(bookRequest);
        List<TripMateError> errors = purchaseResponse.getErrors();
        if (!CollectionUtils.isEmpty(errors)) {
            errorMesssage = errors.get(0).getErrorText();
        }
        Assert.assertTrue(errorMesssage, CollectionUtils.isEmpty(errors));
    }

}
