package com.travelinsurancemaster.services;

import com.travelinsurancemaster.InsuranceMasterApp;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.model.*;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.services.clients.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by ritchie on 2/17/15.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
@Ignore
public class AbstractClientTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractClientTest.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    private Map<String, InsuranceVendorApi> clients;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private TestCreditCards creditCards;

    @Autowired
    private PolicyMetaCodeService policyMetaCodeService;

    private QuoteRequest quoteRequest;
    private PurchaseRequest purchaseRequest;
    private String errorMessage;

    @Before
    public void prepareRequests() {
        prepareQuoteRequest();
        preparePurchaseRequest();
    }

    private void prepareQuoteRequest() {
        quoteRequest = new QuoteRequest();
        quoteRequest.setDepartDate(TestUtils.getIncrementedLocalDate(2));
        quoteRequest.setReturnDate(TestUtils.getIncrementedLocalDate(7));
        quoteRequest.setDepositDate(TestUtils.getIncrementedLocalDate(-2));
        quoteRequest.setResidentCountry(CountryCode.US);
        quoteRequest.setResidentState(StateCode.CA);
        quoteRequest.setCitizenCountry(CountryCode.US);
        quoteRequest.setTripCost(BigDecimal.valueOf(1200.));
        quoteRequest.setDestinationCountry(CountryCode.CA);
        quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
        GenericTraveler traveler = new GenericTraveler();
        traveler.setTripCost(BigDecimal.valueOf(1200.));
        traveler.setAge(27);
        traveler.setFirstName("John");
        traveler.setLastName("Smith");
        traveler.setMiddleInitials("");
        traveler.setPrimary(true);
        traveler.setBeneficiary("Test Testerson");
        traveler.setBeneficiaryRelation(BeneficiaryRelation.Spouse);
        quoteRequest.getTravelers().add(traveler);
    }

    private void preparePurchaseRequest() {
        purchaseRequest = new PurchaseRequest();
        purchaseRequest.setQuoteRequest(quoteRequest);
        purchaseRequest.getTravelers().addAll(quoteRequest.getTravelers());
        purchaseRequest.setEmail("test@mail.com");
        purchaseRequest.setCity("City");
        purchaseRequest.setAddress("83 Forest Drive");
        purchaseRequest.setPhone("1234567890");
        purchaseRequest.setPostalCode("90001"); // CA postal code
    }

    @Test
    public void quoteTrawickTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(TrawickClient.SAFE_TRAVELS_INTERNATIONAL_PLAN_UNIQUE_CODE);
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        quoteAbstractTest(ApiVendor.Trawick, policyMetaInitialized);
    }

    @Ignore
    @Test
    public void quoteITravelInsuredTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(ITravelInsuredClient.TRAVEL_LITE_UNIQUE_CODE);
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        quoteAbstractTest(ApiVendor.ITravelInsured, policyMetaInitialized);
    }

    // not supported
    @Ignore
    @Test
    public void quoteAllianzClientTest() throws ExecutionException, InterruptedException {
        /*PolicyMeta policyMeta = new PolicyMeta();
        policyMeta.setCode("001000109");
        quoteRequest.setResidentCountry(CountryCode.US);
        quoteAbstractTest(ApiVendor.Allianz, policyMeta);*/
    }

    @Test
    public void quoteCsaClientTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("CSAC");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        quoteAbstractTest(ApiVendor.CSA, policyMetaInitialized);
    }

    @Test
    public void quoteTravelInsuredClientTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("TIWTP14");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        quoteAbstractTest(ApiVendor.TravelInsured, policyMetaInitialized);
    }

    @Ignore
    @Test
    public void quoteRoamRightClientTest() throws ExecutionException, InterruptedException {
        /*
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(RoamRightLegacyClient.PLAN_ADVENTURE_UNIQUE_CODE);
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        quoteAbstractTest(ApiVendor.RoamRight, policyMetaInitialized);
        */
    }

    @Ignore
    @Test
    public void quoteTravelSafeClientTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("TS6029");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        quoteAbstractTest(ApiVendor.TravelSafe, policyMetaInitialized);
    }

    @Test
    public void quoteTravelGuardTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("TGCA6600");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        quoteAbstractTest(ApiVendor.TravelGuard, policyMetaInitialized);
    }

    @Ignore
    @Test
    public void quoteTravelexInsuranceTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("TSTB");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        quoteAbstractTest(ApiVendor.TravelexInsurance, policyMetaInitialized);
    }

    @Test
    public void quoteMHRossTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("AR795A");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        quoteAbstractTest(ApiVendor.MHRoss, policyMetaInitialized);
    }

    @Ignore
    @Test
    public void quoteTravelMedicalStandardPlanTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(UsaAssistClient.TRAVEL_MEDICAL_STANDARD_UNIQUE_CODE);
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        quoteAbstractTest(ApiVendor.UsaAssist, policyMetaInitialized);
    }

    @Ignore
    @Test
    public void quoteUpsaleTravelexInsuranceFlight() {
        quoteRequest.getUpsaleValues().put(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "500000");
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("TSFS");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = clients.get(ApiVendor.TravelexInsurance).quote(quoteRequest, policyMetaInitialized);
        quoteRequest.getUpsaleValues().clear();
        if (!CollectionUtils.isEmpty(quoteResult.getErrors())) {
            errorMessage = quoteResult.getErrors().get(0).getErrorMsg();
        }
        Assert.assertTrue(errorMessage, quoteResult.getErrors().isEmpty());
    }

    @Ignore
    @Test
    public void quoteUpsaleTravelexInsuranceBusinessWithTC() {
        quoteRequest.getUpsaleValues().put(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "300000");
        quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("TSFS");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = clients.get(ApiVendor.TravelexInsurance).quote(quoteRequest, policyMetaInitialized);
        quoteRequest.getUpsaleValues().clear();
        if (!CollectionUtils.isEmpty(quoteResult.getErrors())) {
            errorMessage = quoteResult.getErrors().get(0).getErrorMsg();
        }
        Assert.assertTrue(errorMessage, quoteResult.getErrors().isEmpty());
    }

    QuoteResult quoteAbstractTest(String apiVendor, PolicyMeta policyMeta) throws ExecutionException, InterruptedException {
        QuoteResult quoteResult = clients.get(apiVendor).quote(quoteRequest, policyMeta);
        if (!CollectionUtils.isEmpty(quoteResult.getErrors())) {
            errorMessage = quoteResult.getErrors().get(0).getErrorMsg();
        }
        Assert.assertTrue(errorMessage, quoteResult.getErrors().isEmpty());
        Assert.assertFalse("No products returned", quoteResult.products.isEmpty());
        return quoteResult;
    }

    @Ignore
    @Test
    public void purchaseITravelInsuredClientTest() {
        CreditCard iTravelInsuredCard = new CreditCard("George Smith", "10", "2017", 4112344112344113L, CardType.VISA, "921236789", "");
        iTravelInsuredCard.setCcAddress("address");
        iTravelInsuredCard.setCcCity("city");
        iTravelInsuredCard.setCcCountry(CountryCode.US);
        iTravelInsuredCard.setCcStateCode(StateCode.AA);
        purchaseRequest.setCreditCard(iTravelInsuredCard);
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(ITravelInsuredClient.TRAVEL_LITE_UNIQUE_CODE);
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), quoteRequest);
        Product product = new Product(policyMeta, policyMetaCode, new BigDecimal(10));
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.ITravelInsured).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Ignore
    @Test
    public void purchaseSevenCornersClientTest() throws ExecutionException, InterruptedException {
        CreditCard sevenCornersCard = new CreditCard("Test T Testerson", "12", "2015", 4111111111111111L, CardType.VISA, "12345", "");
        sevenCornersCard.setCcAddress("address");
        sevenCornersCard.setCcCity("city");
        sevenCornersCard.setCcCountry(CountryCode.US);
        sevenCornersCard.setCcStateCode(StateCode.AA);
        purchaseRequest.setCreditCard(sevenCornersCard);

        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("SC14");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.SevenCorners, policyMetaInitialized);
        Product product = quoteResult.products.get(0);
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.SevenCorners).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Test
    public void purchaseCsaClientTest() {
        purchaseRequest.setCreditCard(new CreditCard("George Smith", "8", "2025", 4111111111111111L, CardType.VISA, "921236789", ""));
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(CSAClient.CUSTOM_UNIQUE_CODE);
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), quoteRequest);
        Product product = new Product(policyMeta, policyMetaCode, new BigDecimal(43.50));
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.CSA).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Test
    public void purchaseTrawickClientTest() {
        quoteRequest.getTravelers().get(0).setFirstName(UUID.randomUUID().toString());
        quoteRequest.getTravelers().get(0).setLastName(UUID.randomUUID().toString());
        CreditCard trawickCard = new CreditCard("Test Card", "12", "2025", 4111111111111111L, CardType.VISA, "36693", "999");
        trawickCard.setCcStateCode(StateCode.AL);
        trawickCard.setCcCity("Mobile");
        trawickCard.setCcAddress("123 Main Str");
        trawickCard.setCcCountry(CountryCode.US);
        purchaseRequest.setCreditCard(trawickCard);
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(TrawickClient.SAFE_TRAVELS_INTERNATIONAL_PLAN_UNIQUE_CODE);
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), quoteRequest);
        Product product = new Product(policyMetaInitialized, policyMetaCode, new BigDecimal(10));
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.Trawick).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Test
    public void purchaseTravelInsuredClientTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("TIWTP14");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.TravelInsured, policyMetaInitialized);
        quoteRequest.getTravelers().get(0).setFirstName(UUID.randomUUID().toString());
        quoteRequest.getTravelers().get(0).setLastName(UUID.randomUUID().toString());
        purchaseRequest.setCreditCard(new CreditCard("John P.Doe", "09", "2025", 4007000000027L, CardType.VISA, "921236789", "123"));
        Product product = quoteResult.products.get(0);
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.TravelInsured).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Ignore
    @Test
    public void purchaseRoamRightClientLegacyTest() throws ExecutionException, InterruptedException {
        /*
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(RoamRightLegacyClient.PLAN_ADVENTURE_UNIQUE_CODE);
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.RoamRight, policyMetaInitialized);
        CreditCard creditCard = creditCards.getCreditCardByVendorCode(ApiVendor.RoamRight);
        purchaseRequest.setCreditCard(creditCard);
        Product product = quoteResult.products.get(0);
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.RoamRight).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
        */
    }

    // Allianz is not supported
    @Ignore
    @Test
    public void purchaseAllianzClientTest() {
        /*PurchaseRequest purchaseRequest = new PurchaseRequest();
        QuoteRequest quoteRequest = new QuoteRequest();
        purchaseRequest.setQuoteRequest(quoteRequest);

        CreditCard allianzCC = new CreditCard("Vickie S. Stone", "5", "2017", 4012000033330026L, CardType.VISA, "24112", "");
        allianzCC.setCcAddress("155 A P Hill Road");
        allianzCC.setCcCity("Martinsville");
        allianzCC.setCcCountry(CountryCode.US);

        purchaseRequest.setCreditCard(allianzCC);
        quoteRequest.setDepartDate(TestUtils.getIncrementedDate(2));
        quoteRequest.setReturnDate(TestUtils.getIncrementedDate(7));
        quoteRequest.setDepositDate(TestUtils.getIncrementedDate(-2));
        PolicyMeta policyMeta = new PolicyMeta();
        // policyMeta.setName(); not sended, just ProductCode is sended
        policyMeta.setCode("001000109");
        Product product = new Product(policyMeta, new BigDecimal(450));
        purchaseRequest.setProduct(product);
        GenericTraveler traveler = new GenericTraveler();
        traveler.setFirstName("Vickie S.");
        traveler.setLastName("Stone");
        traveler.setTripCost(BigDecimal.valueOf(196.1));
        traveler.setAge(26);
        purchaseRequest.getTravelers().add(traveler);
        quoteRequest.setDestinationCountry(CountryCode.US);
        quoteRequest.setTripCost(BigDecimal.valueOf(196.1));
        quoteRequest.setResidentCountry(CountryCode.US);
        purchaseRequest.setCity("Martinsville");
        purchaseRequest.setAddress("155 A P Hill Road");
        quoteRequest.setResidentState(StateCode.UT);
        purchaseRequest.setPostalCode("24112");
        purchaseRequest.setPhone("8601111111");
        purchaseRequest.setEmail("nicolas.morel@allianzassistance.com");
        purchaseRequest.getCreditCard().setCcType(CardType.VISA);
        purchaseRequest.getCreditCard().setCcNumber(4012000033330026l);
        purchaseRequest.getCreditCard().setCcExpMonth("5");
        purchaseRequest.getCreditCard().setCcExpYear("2017");
        purchaseRequest.getCreditCard().setCcName("Vickie S. Stone");
        // purchaseRequest.setCcPostalCode(""); // absent
        purchaseRequest.getCreditCard().setCcZipCode("24112");
        purchaseRequest.getCreditCard().setCcCity("Martinsville");
        purchaseRequest.getCreditCard().setCcCountry(CountryCode.US);
        purchaseRequest.getCreditCard().setCcAddress("155 A P Hill Road");
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.Allianz).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());*/
    }

    @Ignore
    @Test
    public void purchaseTravelSafeClientTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("TS6028");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.TravelSafe, policyMetaInitialized);
        CreditCard travelSafeCC = new CreditCard("Erica C.Boling", "02", "2019", 4111111111111111L, CardType.VISA, "67037", "123");
        travelSafeCC.setCcAddress("83 Forest Drive");
        travelSafeCC.setCcPhone("9082272963");
        travelSafeCC.setCcStateCode(StateCode.NJ);
        travelSafeCC.setCcCity("Piscataway");
        travelSafeCC.setCcCountry(CountryCode.US);

        purchaseRequest.setCreditCard(travelSafeCC);
        purchaseRequest.setProduct(quoteResult.products.get(0));
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.TravelSafe).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Test
    public void purchaseTravelGuardClientTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("TGCA6600");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.TravelGuard, policyMetaInitialized);
        CreditCard travelGuardCard = new CreditCard("Steve Harris", "12", "2025", 4111111111111111L, CardType.VISA, "40583", "333");
        travelGuardCard.setCcStateCode(StateCode.CA);
        travelGuardCard.setCcCity("Arcata");
        travelGuardCard.setCcAddress("54 Street");
        travelGuardCard.setCcPhone("9086587845");
        travelGuardCard.setCcCountry(CountryCode.US);
        purchaseRequest.setCreditCard(travelGuardCard);
        Product product = quoteResult.products.get(0);
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.TravelGuard).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Ignore
    @Test
    public void purchaseHCCMISAtlasTravelInternationalClientTest() throws ExecutionException, InterruptedException {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(HCCMedicalInsuranceServicesClient.HCCMIS_ATLAS_TRAVEL);
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.HCCMedicalInsuranceServices, policyMetaInitialized);

        CreditCard hCCMISCard = new CreditCard("Test Name", "12", "2025", 4111111111111111L, CardType.VISA, "40583", "333");
        hCCMISCard.setCcCity("City");
        hCCMISCard.setCcStateCode(StateCode.CA);
        hCCMISCard.setCcAddress("53rd Street");
        hCCMISCard.setCcZipCode("130256");
        hCCMISCard.setCcCountry(CountryCode.US);
        purchaseRequest.setCreditCard(hCCMISCard);
        Product product = quoteResult.products.get(0);
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.HCCMedicalInsuranceServices).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Ignore
    @Test
    public void purchaseHCCMISStudentSecureClientTest() {
        CreditCard hCCMISCard = new CreditCard("Joe Black", "12", "2025", 4111111111111111L, CardType.VISA, "55555", "000");
        hCCMISCard.setCcCity("City");
        hCCMISCard.setCcStateCode(StateCode.CA);
        hCCMISCard.setCcAddress("53rd Street");
        hCCMISCard.setCcCountry(CountryCode.US);
        purchaseRequest.setCreditCard(hCCMISCard);
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(HCCMedicalInsuranceServicesClient.HCCMIS_STUDENT_SECURE_SELECT);
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), quoteRequest);
        Product product = new Product(policyMeta, policyMetaCode, new BigDecimal(10));
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.HCCMedicalInsuranceServices).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Ignore
    @Test
    public void purchaseTravelexInsuranceClientTest() {
        purchaseRequest.setCreditCard(creditCards.getCreditCardByVendorCode(ApiVendor.TravelexInsurance));
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("TSTB");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), quoteRequest);
        Product product = new Product(policyMetaInitialized, policyMetaCode, new BigDecimal(10));
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.TravelexInsurance).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Ignore
    @Test
    public void purchaseHTHExcursionClientTest() throws ExecutionException, InterruptedException {
        purchaseRequest.setCreditCard(creditCards.getCreditCardByVendorCode(ApiVendor.HTHTravelInsurance));
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("HTH75");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.HTHTravelInsurance, policyMetaInitialized);
        Product product = quoteResult.products.get(0);
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.HTHTravelInsurance).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Ignore
    @Test
    public void purchaseHTHExcursionUpsaleClientTest() throws ExecutionException, InterruptedException {
        quoteRequest.getUpsaleValues().put(CategoryCodes.MEDICAL_DEDUCTIBLE, "500");
        quoteRequest.getUpsaleValues().put(CategoryCodes.EMERGENCY_MEDICAL, "100000");
        purchaseRequest.setCreditCard(creditCards.getCreditCardByVendorCode(ApiVendor.HTHTravelInsurance));
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("HTH75");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.HTHTravelInsurance, policyMetaInitialized);
        Product product = quoteResult.products.get(0);
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.HTHTravelInsurance).purchase(purchaseRequest);
        quoteRequest.getUpsaleValues().clear();
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Ignore
    @Test
    public void purchaseHTHTravelGapGoldClientTest() throws ExecutionException, InterruptedException {
        purchaseRequest.setCreditCard(creditCards.getCreditCardByVendorCode(ApiVendor.HTHTravelInsurance));
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("HTH73");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.HTHTravelInsurance, policyMetaInitialized);
        Product product = quoteResult.products.get(0);
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.HTHTravelInsurance).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Test
    public void purchaseMHRossAsset() throws ExecutionException, InterruptedException {
        purchaseRequest.setCreditCard(creditCards.getCreditCardByVendorCode(ApiVendor.MHRoss));
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode("AR795A");
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.MHRoss, policyMetaInitialized);
        Product product = quoteResult.products.get(0);
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.MHRoss).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Ignore
    @Test
    public void purchaseHCCMISAtlasMultiTripClientTest() throws ExecutionException, InterruptedException {
        purchaseRequest.setCreditCard(creditCards.getCreditCardByVendorCode(ApiVendor.HCCMedicalInsuranceServices));
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(HCCMedicalInsuranceServicesClient.HCCMIS_MULTITRIP_INTERNATIONAL);
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.HCCMedicalInsuranceServices, policyMetaInitialized);
        Product product = quoteResult.products.get(0);
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.HCCMedicalInsuranceServices).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }

    @Ignore
    @Test
    public void purchaseTravelMedicalStandardPlanTest() throws ExecutionException, InterruptedException {
        purchaseRequest.setCreditCard(creditCards.getCreditCardByVendorCode(ApiVendor.UsaAssist));
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(UsaAssistClient.TRAVEL_MEDICAL_STANDARD_UNIQUE_CODE);
        PolicyMeta policyMetaInitialized = policyMetaService.getPolicyMetaById(policyMeta.getId());
        QuoteResult quoteResult = quoteAbstractTest(ApiVendor.UsaAssist, policyMetaInitialized);
        Product product = quoteResult.products.get(0);
        purchaseRequest.setProduct(product);
        PurchaseResponse purchaseResponse = clients.get(ApiVendor.UsaAssist).purchase(purchaseRequest);
        if (!purchaseResponse.getErrors().isEmpty()) {
            errorMessage = purchaseResponse.getErrors().get(0).getErrorMsg();
        }
        Assert.assertEquals(errorMessage, Result.Status.SUCCESS, purchaseResponse.getStatus());
    }
}
