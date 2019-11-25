package com.travelinsurancemaster.services;


import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.repository.PolicyMetaRepository;
import com.travelinsurancemaster.services.clients.BHTravelProtectionClient;
import com.travelinsurancemaster.util.DateUtil;
import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

;
;

/**
 * Created by maleev on 27.04.2016.
 */
@Ignore
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@Transactional
public class BHTravelProtectionTest {



    private QuoteRequest testQuoteRequest;
    private PurchaseRequest testPurchaseRequest;

    @Autowired
    @Qualifier(ApiVendor.BHTravelProtection)
    private BHTravelProtectionClient bhtpProtectionClient;

    @Autowired
    PolicyMetaRepository policyMetaRepository;

    @Autowired
    TestCreditCards testCreditCards;

    private List<PolicyMeta> policyMetaList;

    private List<Product> products = new LinkedList<>();


    @Before
    @Transactional
    public void init(){
        policyMetaList = policyMetaRepository.findByVendorCodeAndVendorActiveTrueAndActiveTrueAndDeletedDateIsNull(ApiVendor.BHTravelProtection);

        for (PolicyMeta policyMeta:policyMetaList) {
            for(PolicyMetaCategory category: policyMeta.getPolicyMetaCategories()){
                Hibernate.initialize(category);
                for (PolicyMetaCategoryValue policyMetaCategoryValue: category.getValues()) {
                    Hibernate.initialize(policyMetaCategoryValue);
                }
            }

        }
    }


    @Before
    public void fillQuoteRequest(){
        testQuoteRequest = new QuoteRequest();
        try {
            testQuoteRequest.setDepartDate(DateUtil.getLocalDate("07-22-2016"));
            testQuoteRequest.setReturnDate(DateUtil.getLocalDate("08-07-2016"));
            testQuoteRequest.setDepositDate(DateUtil.getLocalDate("05-24-2016"));
        } catch(Exception ex){}

        testQuoteRequest.setDestinationCountry(CountryCode.FR);
        testQuoteRequest.setCitizenCountry(CountryCode.US);
        testQuoteRequest.setResidentCountry(CountryCode.US);
        testQuoteRequest.setResidentState(StateCode.FL);

        //Setting all optional categories
        testQuoteRequest.getUpsaleValues().put(CategoryCodes.RENTAL_CAR, "Yes");
        testQuoteRequest.getUpsaleValues().put(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "200000");
        //testQuoteRequest.getUpsaleValues().put(CategoryCodes.EMERGENCY_MEDICAL, "50000");
        //testQuoteRequest.getUpsaleValues().put(CategoryCodes.CANCEL_FOR_ANY_REASON, "Yes");
        testQuoteRequest.getUpsaleValues().put(CategoryCodes.MEDICAL_EVACUATION, "1000000");



        GenericTraveler traveler = new GenericTraveler();
        traveler.setAge(29);
        traveler.setPrimary(true);
        traveler.setTripCost(BigDecimal.valueOf(6553L));

        testQuoteRequest.setTripCost(BigDecimal.valueOf(6553L));

        testQuoteRequest.getTravelers().add(traveler);

        fillPurchaseRequest();
    }

    private void fillPurchaseRequest(){
        testPurchaseRequest = new PurchaseRequest();
        testPurchaseRequest.setQuoteRequest(testQuoteRequest);

        GenericTraveler traveler = testQuoteRequest.getTravelers().get(0);
        traveler.setFirstName("Egor");
        traveler.setLastName("Maleev");
        try {
            traveler.setBirthday(DateUtil.getLocalDate("06-21-1986"));
        } catch (Exception ex){}
        traveler.setTripCost(BigDecimal.valueOf(6553L));

        testPurchaseRequest.getTravelers().add(traveler);
        testPurchaseRequest.setEmail("egor.maleev@gmail.com");
        testPurchaseRequest.setPhone("18444112487");
        testPurchaseRequest.setCity("Miami");
        testPurchaseRequest.setAddress("Park avenue 45");
        testPurchaseRequest.setPostalCode("32301");

        testPurchaseRequest.setCreditCard(testCreditCards.getCreditCardByVendorCode(ApiVendor.BHTravelProtection));
    }

    @Test
    public void quoteAndPurchase(){

        for (PolicyMeta policyMeta:policyMetaList) {
            String message = "";
            QuoteResult result = bhtpProtectionClient.testQuoteInternalMethod(testQuoteRequest, policyMeta, policyMeta.getPolicyMetaCodes().get(0));
            if (result.getErrors() != null && result.getErrors().size() > 0){
                message = result.getErrors().get(0).getErrorMsg();
            }
            org.junit.Assert.assertTrue(message, result.getErrors().size() == 0);

            products.addAll(result.products);

        }

        //purchase
        for (Product product:products) {
            testPurchaseRequest.setProduct(product);
            PurchaseResponse response = bhtpProtectionClient.testPurchaseInternal(testPurchaseRequest);
            org.junit.Assert.assertEquals("Не удачная оплата. Policy " + product.getPolicyMetaCode(),Result.Status.SUCCESS, response.getStatus());
        }


    }






}
