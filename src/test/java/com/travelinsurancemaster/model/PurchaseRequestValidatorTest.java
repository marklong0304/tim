package com.travelinsurancemaster.model;

import com.travelinsurancemaster.InsuranceMasterApp;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.common.validator.PurchaseRequestValidator;
import com.travelinsurancemaster.services.PolicyMetaService;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Created by Chernov Artur on 16.04.15.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
public class PurchaseRequestValidatorTest {

    private static final Logger log = LoggerFactory.getLogger(PurchaseRequestValidatorTest.class);

    @Autowired
    private PurchaseRequestValidator validator;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Test
    public void testValidation() {
        PurchaseRequest purchaseRequest = getValidPurchaseRequest();
        Errors errors = new BeanPropertyBindingResult(purchaseRequest, "purchaseRequest");
        validator.validate(purchaseRequest, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithValidTravelers() {
        PurchaseRequest purchaseRequest = getValidPurchaseRequest();
        purchaseRequest.getTravelers().clear();
        GenericTraveler traveler = getTraveler(true);
        purchaseRequest.getTravelers().add(traveler);
        Errors errors = new BeanPropertyBindingResult(purchaseRequest, "purchaseRequest");
        validator.validate(purchaseRequest, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidationWithEmptyTravelers() {
        PurchaseRequest purchaseRequest = getValidPurchaseRequest();
        purchaseRequest.getTravelers().clear();
        Errors errors = new BeanPropertyBindingResult(purchaseRequest, "purchaseRequest");
        validator.validate(purchaseRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.TRAVELERS));
        assertEquals(errors.getFieldError(QuoteRequestConstants.TRAVELERS).getDefaultMessage(), "Incorrect count of travelers");
    }

    @Test
    public void testValidationWithNegativeAgeTravelers() {
        PurchaseRequest purchaseRequest = getValidPurchaseRequest();
        purchaseRequest.getTravelers().clear();
        GenericTraveler traveler1 = getTraveler(true);
        GenericTraveler traveler2 = getTraveler(true);
        traveler2.setAge(-1);
        purchaseRequest.getTravelers().add(traveler1);
        purchaseRequest.getTravelers().add(traveler2);
        Errors errors = new BeanPropertyBindingResult(purchaseRequest, "purchaseRequest");
        validator.validate(purchaseRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.TRAVELERS));
        assertEquals(errors.getFieldError(QuoteRequestConstants.TRAVELERS).getDefaultMessage(), "Primary traveler should be just one");
        assertEquals(errors.getFieldError("travelers[1].age").getDefaultMessage(), "Incorrect traveler age");
    }

    @Test
    public void testValidationWithIncorrectBirthdayTravelers() {
        PurchaseRequest purchaseRequest = getValidPurchaseRequest();
        purchaseRequest.getTravelers().clear();
        GenericTraveler traveler1 = getTraveler(true);
        traveler1.setAge(traveler1.getAge()+1);
        purchaseRequest.getTravelers().add(traveler1);
        Errors errors = new BeanPropertyBindingResult(purchaseRequest, "purchaseRequest");
        validator.validate(purchaseRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError("travelers[0].birthday"));
        assertEquals(errors.getFieldError("travelers[0].birthday").getDefaultMessage(), "Incorrect traveler birthday");
    }

    @Test
    public void testValidationWithMultiPrimaryTravelers() {
        PurchaseRequest purchaseRequest = getValidPurchaseRequest();
        purchaseRequest.getTravelers().clear();
        GenericTraveler traveler1 = getTraveler(true);
        GenericTraveler traveler2 = getTraveler(true);
        purchaseRequest.getTravelers().add(traveler1);
        purchaseRequest.getTravelers().add(traveler2);
        Errors errors = new BeanPropertyBindingResult(purchaseRequest, "purchaseRequest");
        validator.validate(purchaseRequest, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError(QuoteRequestConstants.TRAVELERS));
        assertEquals(errors.getFieldError(QuoteRequestConstants.TRAVELERS).getDefaultMessage(), "Primary traveler should be just one");
    }

    private PurchaseRequest getValidPurchaseRequest() {
        PurchaseRequest validPurchaseRequest = new PurchaseRequest();
        CreditCard creditCard = new CreditCard("ccName", "ccExpMonth", "ccExpYear", 4111111111111111l, CardType.VISA, "ccZipCode", "ccCode");
        creditCard.setCcAddress("address");
        creditCard.setCcCity("city");
        creditCard.setCcZipCode("zipCode");
        creditCard.setCcCountry(CountryCode.US);
        creditCard.setCcStateCode(StateCode.LA);
        validPurchaseRequest.setCreditCard(creditCard);
        validPurchaseRequest.setPostalCode("12345");
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setResidentCountry(CountryCode.US);
        validPurchaseRequest.setQuoteRequest(quoteRequest);
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("TIWTP14");
        Product product = new Product();
        product.setPolicyMeta(policyMeta);
        validPurchaseRequest.setProduct(product);
        GenericTraveler validTraveler = getTraveler(true);
        validPurchaseRequest.getTravelers().add(validTraveler);
        return validPurchaseRequest;
    }

    private GenericTraveler getTraveler(boolean primary) {
        GenericTraveler traveler = new GenericTraveler();
        Integer age = RandomUtils.nextInt(1, 99);
        LocalDate birthday = LocalDate.now().minusYears(age);
        traveler.setAge(age);
        traveler.setBirthday(birthday);
        traveler.setFirstName("Name");
        traveler.setLastName("Surname");
        traveler.setPrimary(primary);
        traveler.setBeneficiary("Beneficiary Test");
        traveler.setBeneficiaryRelation(BeneficiaryRelation.Spouse);
        return traveler;
    }
}
