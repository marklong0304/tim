package com.travelinsurancemaster.model;

import com.travelinsurancemaster.InsuranceMasterApp;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequestConstants;
import com.travelinsurancemaster.model.webservice.common.validator.QuoteRequestValidator;
import com.travelinsurancemaster.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by Chernov Artur on 15.04.15.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
public class QuoteRequestValidatorTest {

    private static final Logger log = LoggerFactory.getLogger(QuoteRequestValidatorTest.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private QuoteRequestValidator validator;

    @Before
    public void setUp() {
        validator = new QuoteRequestValidator();
    }

    @Test
    public void testValidation() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithValidDepartDate() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setDepartDate(TestUtils.getIncrementedLocalDate(3));
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithInvalidDepartDate() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setDepartDate(TestUtils.getIncrementedLocalDate(-3));
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.DEPART_DATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.DEPART_DATE).getDefaultMessage(), "Departure date must be tomorrow or later");
    }

    @Test
    public void testValidationWithValidReturnDate() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setReturnDate(TestUtils.getIncrementedLocalDate(15));
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithInvalidReturnDate() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setReturnDate(TestUtils.getIncrementedLocalDate(-1));
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RETURN_DATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RETURN_DATE).getDefaultMessage(), "Return date must be later than depart date");
    }

    @Test
    public void testValidationWithValidTripCost() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setTripCost(new BigDecimal(20000.0));
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithNegativeTripCost() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setTripCost(new BigDecimal(-17000.0));
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError("tripCost"));
        assertEquals(errors.getFieldError("tripCost").getDefaultMessage(), "The cost of trip must be positive");
    }

    @Test
    public void testValidationWithExistResidentCountry() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setResidentCountry(CountryCode.AD);
        quoteRequest.setResidentState(null);
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithEmptyResidentCountry() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setResidentCountry(null);
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_COUNTRY));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_COUNTRY).getCode(), "error.residentCountry.required");
    }

    @Test
    public void testValidationWithValidResidentState() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setResidentCountry(CountryCode.US);
        quoteRequest.setResidentState(StateCode.WA);
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertFalse(errors.hasErrors());
        errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        quoteRequest.setResidentCountry(CountryCode.CA);
        quoteRequest.setResidentState(StateCode.PE);
        validator.validate(quoteRequest, errors);
        assertFalse(errors.hasErrors());
        errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        quoteRequest.setResidentCountry(CountryCode.RU);
        quoteRequest.setResidentState(null);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithInvalidResidentState() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setResidentCountry(CountryCode.US);
        quoteRequest.setResidentState(null);
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE).getCode(), "error.residentState.required");
        errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        quoteRequest.setResidentCountry(CountryCode.CA);
        quoteRequest.setResidentState(null);
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE).getCode(), "error.residentState.required");
        errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        quoteRequest.setResidentCountry(CountryCode.RU);
        quoteRequest.setResidentState(StateCode.NJ);
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE).getDefaultMessage(), "Resident state must be empty");
        errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        quoteRequest.setResidentCountry(CountryCode.CA);
        quoteRequest.setResidentState(StateCode.WA);
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE).getDefaultMessage(), "There is not this state in this country");
        errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        quoteRequest.setResidentCountry(CountryCode.US);
        quoteRequest.setResidentState(StateCode.NT);
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE).getDefaultMessage(), "There is not this state in this country");
    }

    @Test
    public void testValidationWithExistCitizenCountry() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setCitizenCountry(CountryCode.AD);
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithEmptyCitizenCountry() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setCitizenCountry(null);
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.CITIZEN_COUNTRY));
        assertEquals(errors.getFieldError(QuoteRequestConstants.CITIZEN_COUNTRY).getCode(), "error.citizenCountry.required");
    }

    @Test
    public void testValidationWithEmptyDestinationCountry() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setDestinationCountry(null);
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.DESTINATION_COUNTRY));
        assertEquals(errors.getFieldError(QuoteRequestConstants.DESTINATION_COUNTRY).getCode(), "error.destinationCountry.required");
    }

    @Test
    public void testValidationWithValidDestinationState() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.setDestinationCountry(CountryCode.US);
//        quoteRequest.setDestinationState(StateCode.WA);
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertFalse(errors.hasErrors());
        errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        quoteRequest.setDestinationCountry(CountryCode.CA);
//        quoteRequest.setDestinationState(StateCode.PE);
        validator.validate(quoteRequest, errors);
        assertFalse(errors.hasErrors());
        errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        quoteRequest.setDestinationCountry(CountryCode.RU);
//        quoteRequest.setDestinationState(null);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithValidTravelers() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.getTravelers().clear();
        GenericTraveler traveler = getTraveler(25, "02/02/1990", true);
        quoteRequest.getTravelers().add(traveler);
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithEmptyTravelers() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.getTravelers().clear();
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.TRAVELERS));
        assertEquals(errors.getFieldError(QuoteRequestConstants.TRAVELERS).getDefaultMessage(), "Incorrect count of travelers");
    }

    @Test
    public void testValidationWithNegativeAgeTravelers() {
        QuoteRequest quoteRequest = getValidQuoteRequest();
        quoteRequest.getTravelers().clear();
        GenericTraveler traveler1 = getTraveler(20, "01/01/1995", true);
        GenericTraveler traveler2 = getTraveler(-1, null, true);
        quoteRequest.getTravelers().add(traveler1);
        quoteRequest.getTravelers().add(traveler2);
        Errors errors = new BeanPropertyBindingResult(quoteRequest, "quoteRequest");
        validator.validate(quoteRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError("travelers[1].age"));
        assertEquals(errors.getFieldError("travelers[1].age").getDefaultMessage(), "Age should be in range from 1 to 119");
    }

    private QuoteRequest getValidQuoteRequest() {
        QuoteRequest validQuoteRequest = new QuoteRequest();
        validQuoteRequest.setDepartDate(TestUtils.getIncrementedLocalDate(3));
        validQuoteRequest.setReturnDate(TestUtils.getIncrementedLocalDate(8));
        validQuoteRequest.setTripCost(new BigDecimal(1200.0));
        validQuoteRequest.setResidentCountry(CountryCode.US);
        validQuoteRequest.setResidentState(StateCode.WA);
        validQuoteRequest.setCitizenCountry(CountryCode.US);
        validQuoteRequest.setDestinationCountry(CountryCode.CA);
//        validQuoteRequest.setDestinationState(StateCode.PE);
        GenericTraveler validTraveler = getTraveler(27, "01/01/1988", true);
        validQuoteRequest.getTravelers().add(validTraveler);
        return validQuoteRequest;
    }

    private GenericTraveler getTraveler(Integer age, String birthday, boolean primary) {
        GenericTraveler traveler = new GenericTraveler();
        traveler.setAge(age);
        traveler.setAgeInDays((long) (age * 365));
        if (birthday != null) {
            traveler.setBirthday(DateUtil.getLocalDate(birthday));
        }
        traveler.setPrimary(primary);
        return traveler;
    }
}