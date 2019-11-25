package com.travelinsurancemaster.model;

import com.travelinsurancemaster.InsuranceMasterApp;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequestConstants;
import com.travelinsurancemaster.model.webservice.common.forms.Step1QuoteRequestForm;
import com.travelinsurancemaster.model.webservice.common.forms.Step2QuoteRequestForm;
import com.travelinsurancemaster.model.webservice.common.validator.Step1QuoteRequestFormValidator;
import com.travelinsurancemaster.model.webservice.common.validator.Step2QuoteRequestFormValidator;
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
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

/**
 * Created by Chernov Artur on 15.04.15.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
public class StepByStepFormQuoteRequestValidatorTest {

    private Step1QuoteRequestFormValidator step1Validator;
    private Step2QuoteRequestFormValidator step2Validator;

    @Before
    public void setUp() {
        step1Validator = new Step1QuoteRequestFormValidator();
        step2Validator = new Step2QuoteRequestFormValidator();
    }

    @Test
    public void testValidationStep1() {
        Step1QuoteRequestForm step1QuoteRequestForm = getValidStep1QuoteRequestForm();
        Errors errors = new BeanPropertyBindingResult(step1QuoteRequestForm, "step1QuoteRequestForm");
        step1Validator.validate(step1QuoteRequestForm, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationStep2() {
        Step2QuoteRequestForm step2QuoteRequestForm = getValidStep2QuoteRequestForm();
        Errors errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithValidDepartDate() {
        Step1QuoteRequestForm step1QuoteRequestForm = getValidStep1QuoteRequestForm();
        step1QuoteRequestForm.setDepartDate(TestUtils.getIncrementedLocalDate(3));
        Errors errors = new BeanPropertyBindingResult(step1QuoteRequestForm, "step1QuoteRequestForm");
        step1Validator.validate(step1QuoteRequestForm, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithInvalidDepartDate() {
        Step1QuoteRequestForm step1QuoteRequestForm = getValidStep1QuoteRequestForm();
        step1QuoteRequestForm.setDepartDate(TestUtils.getIncrementedLocalDate(-3));
        Errors errors = new BeanPropertyBindingResult(step1QuoteRequestForm, "step1QuoteRequestForm");
        step1Validator.validate(step1QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.DEPART_DATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.DEPART_DATE).getDefaultMessage(), "Departure date must be tomorrow or later");
    }

    @Test
    public void testValidationWithValidReturnDate() {
        Step1QuoteRequestForm step1QuoteRequestForm = getValidStep1QuoteRequestForm();
        step1QuoteRequestForm.setReturnDate(TestUtils.getIncrementedLocalDate(15));
        Errors errors = new BeanPropertyBindingResult(step1QuoteRequestForm, "step1QuoteRequestForm");
        step1Validator.validate(step1QuoteRequestForm, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithInvalidReturnDate() {
        Step1QuoteRequestForm step1QuoteRequestForm = getValidStep1QuoteRequestForm();
        step1QuoteRequestForm.setReturnDate(TestUtils.getIncrementedLocalDate(-1));
        Errors errors = new BeanPropertyBindingResult(step1QuoteRequestForm, "step1QuoteRequestForm");
        step1Validator.validate(step1QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RETURN_DATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RETURN_DATE).getDefaultMessage(), "Return date must be later than depart date");
    }

    @Test
    public void testValidationWithValidTripCost() {
        Step1QuoteRequestForm step1QuoteRequestForm = getValidStep1QuoteRequestForm();
        step1QuoteRequestForm.setTripCost(new BigDecimal(20000.0));
        Errors errors = new BeanPropertyBindingResult(step1QuoteRequestForm, "step1QuoteRequestForm");
        step1Validator.validate(step1QuoteRequestForm, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithNegativeTripCost() {
        Step1QuoteRequestForm step1QuoteRequestForm = getValidStep1QuoteRequestForm();
        step1QuoteRequestForm.setTripCost(new BigDecimal(-17000.0));
        Errors errors = new BeanPropertyBindingResult(step1QuoteRequestForm, "step1QuoteRequestForm");
        step1Validator.validate(step1QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError("tripCost"));
        assertEquals(errors.getFieldError("tripCost").getDefaultMessage(), "The cost of trip must be positive");
    }

    @Test
    public void testValidationWithExistDestinationCountry() {
        Step1QuoteRequestForm step1QuoteRequestForm = getValidStep1QuoteRequestForm();
        step1QuoteRequestForm.setDestinationCountry(CountryCode.AD);
        step1QuoteRequestForm.setDestinationState(null);
        Errors errors = new BeanPropertyBindingResult(step1QuoteRequestForm, "step1QuoteRequestForm");
        step1Validator.validate(step1QuoteRequestForm, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithEmptyDestinationCountry() {
        Step1QuoteRequestForm step1QuoteRequestForm = getValidStep1QuoteRequestForm();
        step1QuoteRequestForm.setDestinationCountry(null);
        Errors errors = new BeanPropertyBindingResult(step1QuoteRequestForm, "step1QuoteRequestForm");
        step1Validator.validate(step1QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.DESTINATION_COUNTRY));
        assertEquals(errors.getFieldError(QuoteRequestConstants.DESTINATION_COUNTRY).getCode(), "error.destinationCountry.required");
    }

    @Test
    public void testValidationWithValidTravelers() {
        Step2QuoteRequestForm step2QuoteRequestForm = getValidStep2QuoteRequestForm();
        step2QuoteRequestForm.getTravelers().clear();
        GenericTraveler traveler = getTraveler(25, "02/02/1990", true);
        step2QuoteRequestForm.getTravelers().add(traveler);
        Errors errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithEmptyTravelers() {
        Step2QuoteRequestForm step2QuoteRequestForm = getValidStep2QuoteRequestForm();
        step2QuoteRequestForm.getTravelers().clear();
        Errors errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.TRAVELERS));
        assertEquals(errors.getFieldError(QuoteRequestConstants.TRAVELERS).getDefaultMessage(), "Incorrect count of travelers");
    }

    @Test
    public void testValidationWithNegativeAgeTravelers() {
        Step2QuoteRequestForm step2QuoteRequestForm = getValidStep2QuoteRequestForm();
        step2QuoteRequestForm.getTravelers().clear();
        GenericTraveler traveler1 = getTraveler(20, "01/01/1995", true);
        GenericTraveler traveler2 = getTraveler(-1, null, true);
        step2QuoteRequestForm.getTravelers().add(traveler1);
        step2QuoteRequestForm.getTravelers().add(traveler2);
        Errors errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError("travelers[1].age"));
        assertEquals(errors.getFieldError("travelers[1].age").getDefaultMessage(), "Age should be in range from 1 to 119");
    }

    @Test
    public void testValidationWithExistResidentCountry() {
        Step2QuoteRequestForm step2QuoteRequestForm = getValidStep2QuoteRequestForm();
        step2QuoteRequestForm.setResidentCountry(CountryCode.AD);
        step2QuoteRequestForm.setResidentState(null);
        Errors errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithEmptyResidentCountry() {
        Step2QuoteRequestForm step2QuoteRequestForm = getValidStep2QuoteRequestForm();
        step2QuoteRequestForm.setResidentCountry(null);
        Errors errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_COUNTRY));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_COUNTRY).getCode(), "error.residentCountry.required");
    }

    @Test
    public void testValidationWithValidResidentState() {
        Step2QuoteRequestForm step2QuoteRequestForm = getValidStep2QuoteRequestForm();
        step2QuoteRequestForm.setResidentCountry(CountryCode.US);
        step2QuoteRequestForm.setResidentState(StateCode.WA);
        Errors errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertFalse(errors.hasErrors());
        errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2QuoteRequestForm.setResidentCountry(CountryCode.CA);
        step2QuoteRequestForm.setResidentState(StateCode.PE);
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertFalse(errors.hasErrors());
        errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2QuoteRequestForm.setResidentCountry(CountryCode.RU);
        step2QuoteRequestForm.setResidentState(null);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithInvalidResidentState() {
        Step2QuoteRequestForm step2QuoteRequestForm = getValidStep2QuoteRequestForm();
        step2QuoteRequestForm.setResidentCountry(CountryCode.US);
        step2QuoteRequestForm.setResidentState(null);
        Errors errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE).getCode(), "error.residentState.required");
        errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2QuoteRequestForm.setResidentCountry(CountryCode.CA);
        step2QuoteRequestForm.setResidentState(null);
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE).getCode(), "error.residentState.required");
        errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2QuoteRequestForm.setResidentCountry(CountryCode.RU);
        step2QuoteRequestForm.setResidentState(StateCode.NJ);
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE).getDefaultMessage(), "Resident state must be empty");
        errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2QuoteRequestForm.setResidentCountry(CountryCode.CA);
        step2QuoteRequestForm.setResidentState(StateCode.WA);
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE).getDefaultMessage(), "There is not this state in this country");
        errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2QuoteRequestForm.setResidentCountry(CountryCode.US);
        step2QuoteRequestForm.setResidentState(StateCode.NT);
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE));
        assertEquals(errors.getFieldError(QuoteRequestConstants.RESIDENT_STATE).getDefaultMessage(), "There is not this state in this country");
    }

    @Test
    public void testValidationWithExistCitizenCountry() {
        Step2QuoteRequestForm step2QuoteRequestForm = getValidStep2QuoteRequestForm();
        step2QuoteRequestForm.setCitizenCountry(CountryCode.AD);
        Errors errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithEmptyCitizenCountry() {
        Step2QuoteRequestForm step2QuoteRequestForm = getValidStep2QuoteRequestForm();
        step2QuoteRequestForm.setCitizenCountry(null);
        Errors errors = new BeanPropertyBindingResult(step2QuoteRequestForm, "step2QuoteRequestForm");
        step2Validator.validate(step2QuoteRequestForm, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.CITIZEN_COUNTRY));
        assertEquals(errors.getFieldError(QuoteRequestConstants.CITIZEN_COUNTRY).getCode(), "error.citizenCountry.required");
    }

    private Step1QuoteRequestForm getValidStep1QuoteRequestForm() {
        Step1QuoteRequestForm validStep1QuoteRequestForm = new Step1QuoteRequestForm();
        validStep1QuoteRequestForm.setDestinationCountry(CountryCode.CA);
        validStep1QuoteRequestForm.setDestinationState(StateCode.PE);
        validStep1QuoteRequestForm.setDepartDate(TestUtils.getIncrementedLocalDate(3));
        validStep1QuoteRequestForm.setReturnDate(TestUtils.getIncrementedLocalDate(8));
        validStep1QuoteRequestForm.setTripCost(new BigDecimal(1200.0));
        validStep1QuoteRequestForm.setTripCostTotal(true);
        return validStep1QuoteRequestForm;
    }

    private Step2QuoteRequestForm getValidStep2QuoteRequestForm() {
        Step2QuoteRequestForm validStep2QuoteRequestForm = new Step2QuoteRequestForm();
        GenericTraveler validTraveler = getTraveler(27, "01/01/1988", true);
        validStep2QuoteRequestForm.getTravelers().add(validTraveler);
        validStep2QuoteRequestForm.setResidentCountry(CountryCode.US);
        validStep2QuoteRequestForm.setResidentState(StateCode.WA);
        validStep2QuoteRequestForm.setCitizenCountry(CountryCode.US);
        return validStep2QuoteRequestForm;
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