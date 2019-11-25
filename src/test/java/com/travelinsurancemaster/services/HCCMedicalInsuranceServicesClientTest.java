package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.hccmis.HCCMISAtlasMultiTripRequest;
import com.travelinsurancemaster.model.webservice.hccmis.HCCMISAtlasTravelRequest;
import com.travelinsurancemaster.model.webservice.hccmis.HCCMISStudentSecureRequest;
import com.travelinsurancemaster.services.clients.HCCMedicalInsuranceServicesClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

;
;

/**
 * Created by ritchie on 3/11/15.
 */
@Ignore
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class HCCMedicalInsuranceServicesClientTest {

    private static final String DATE_FORMAT = "MM/dd/yyyy";

    @Autowired
    private HCCMedicalInsuranceServicesClient hccMedicalInsuranceServicesClient;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PolicyQuoteParamService policyQuoteParamService;

    @Test
    public void purchaseAtlasTravel() {
        HCCMISAtlasTravelRequest atlasTravelRequest = new HCCMISAtlasTravelRequest();
        atlasTravelRequest.setPrimaryFirstName("John");
        atlasTravelRequest.setPrimaryMiddleInitial("");
        atlasTravelRequest.setPrimaryLastName("Smith");
        atlasTravelRequest.setDateOfBirth("11/10/1980");
        atlasTravelRequest.setAddress1("53rd Street");
        atlasTravelRequest.setCity("City");
        atlasTravelRequest.setZip("90003");
        atlasTravelRequest.setCountry(CountryCode.AL.name());
        atlasTravelRequest.setTelephone("123456789");
        atlasTravelRequest.setEmail("test@mail.com");
        atlasTravelRequest.setPrimaryTravelerCountryCitizenship(CountryCode.AL.getCaption());
        LocalDate departDate = TestUtils.getIncrementedLocalDate(2);
        LocalDate returnDate = TestUtils.getIncrementedLocalDate(7);
        atlasTravelRequest.setRequestedEffectiveDate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        atlasTravelRequest.setDepartureDate(TestUtils.getFormattedLocalDate(departDate, DATE_FORMAT));
        atlasTravelRequest.setReturnDate(TestUtils.getFormattedLocalDate(returnDate, DATE_FORMAT));
        atlasTravelRequest.setCountriesToBeVisited(CountryCode.US.getCaption());
        atlasTravelRequest.setPolicySelected("AMER"); // "AMER" or "INTL"
        atlasTravelRequest.setProducerNumber(HCCMedicalInsuranceServicesClient.REFER_ID);
        atlasTravelRequest.setBeneficiaryName("John Smith");
        atlasTravelRequest.setPrimaryGender("M");
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(HCCMedicalInsuranceServicesClient.HCCMIS_ATLAS_TRAVEL);
        Long priceDaily = policyQuoteParamService.getPolicyQuoteParam(new QuoteRequest(), policyMeta, 0l, 50000l, 30l, null, null);
        int days = (int) DAYS.between(departDate, returnDate) + 1;
        BigDecimal price = BigDecimal.valueOf(priceDaily).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(days)); // price for 1 traveler
        atlasTravelRequest.setPolicyCost(price);
        // atlasTravelRequest.setMedicalLimit(50000l); todo: not set, needs to know medical limit value
        atlasTravelRequest.setCardNumber("4111111111111111");
        atlasTravelRequest.setExpMonthYear("12/2025"); // "MM/yyyy"
        atlasTravelRequest.setCardName("Test Name");
        atlasTravelRequest.setBillingAddress1("53rd Street");
        atlasTravelRequest.setBillingCity("City");
        atlasTravelRequest.setBillingCountry(CountryCode.US.name());
        atlasTravelRequest.setBillingZip("90003");
        atlasTravelRequest.setQuotedTerm(days); // todo: is quoted term a difference between return and depart days?
        atlasTravelRequest.setHomeCountry(CountryCode.AL.name());
        atlasTravelRequest.setPracaExempt("0");
        String requestString = atlasTravelRequest.buildStringRequest();
        MultiValueMap<String, String> purchaseParams = new LinkedMultiValueMap<>();
        purchaseParams.add("string", requestString);
        String response = hccMedicalInsuranceServicesClient.getAtlasTravelPurchaseResponse(purchaseParams);
        // Example of correct certificate number: A91217488
        Assert.assertTrue(response, response.matches("[A-Z]\\d{8}"));
    }

    @Test
    public void purchaseMultiTrip() {
        HCCMISAtlasMultiTripRequest atlasMultiTripRequest = new HCCMISAtlasMultiTripRequest();
        atlasMultiTripRequest.setPrimaryFirstName("John");
        atlasMultiTripRequest.setPrimaryMiddleInitial("");
        atlasMultiTripRequest.setPrimaryLastName("Smith");
        atlasMultiTripRequest.setDateOfBirth("11/10/1980");
        atlasMultiTripRequest.setAddress1("53rd Street");
        atlasMultiTripRequest.setCity("City");
        atlasMultiTripRequest.setState(StateCode.CA.name());
        atlasMultiTripRequest.setZip("130256");
        atlasMultiTripRequest.setCountry(CountryCode.US.name());
        atlasMultiTripRequest.setTelephone("123456789");
        atlasMultiTripRequest.setEmail("test@mail.com");
        atlasMultiTripRequest.setCountryOfCitizenship(CountryCode.US.name());
        LocalDate departDate = TestUtils.getIncrementedLocalDate(2);
        LocalDate returnDate = TestUtils.getIncrementedLocalDate(7);
        atlasMultiTripRequest.setRequestedEffectiveDate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        atlasMultiTripRequest.setDepartureDate(TestUtils.getFormattedLocalDate(departDate, DATE_FORMAT));
        atlasMultiTripRequest.setReturnDate(TestUtils.getFormattedLocalDate(returnDate, DATE_FORMAT));
        atlasMultiTripRequest.setCountriesToBeVisited(CountryCode.US.name());
        atlasMultiTripRequest.setPolicySelected("EXCLUS");
        atlasMultiTripRequest.setPolicyCost(BigDecimal.valueOf(300));
        atlasMultiTripRequest.setMedicalLimit("1200");
        atlasMultiTripRequest.setCardNumber("4111111111111111");
        atlasMultiTripRequest.setExpMonthYear("12/2025"); // "MM/yyyy"
        atlasMultiTripRequest.setCardName("Test Name");
        atlasMultiTripRequest.setBillingAddress1("53rd Street");
        atlasMultiTripRequest.setBillingCity("City");
        atlasMultiTripRequest.setBillingState(StateCode.CA.name());
        atlasMultiTripRequest.setBillingZip("130256");
        atlasMultiTripRequest.setBillingCountry(CountryCode.US.name());
        int quotedTerm = (int) DAYS.between(departDate, returnDate) + 1;
        atlasMultiTripRequest.setQuotedTerm(quotedTerm); // todo: is quoted term a difference between return and depart days?
        atlasMultiTripRequest.setHomeCountry(CountryCode.US.name());
        atlasMultiTripRequest.setPhysicallyInStateNyWaMd("0");
        atlasMultiTripRequest.setPhysicallyInCanada("0");
        atlasMultiTripRequest.setMaxTripDuration("30");
        String requestString = atlasMultiTripRequest.buildStringRequest();
        MultiValueMap<String, String> purchaseParams = new LinkedMultiValueMap<>();
        purchaseParams.add("string", requestString);
        String response = hccMedicalInsuranceServicesClient.getAtlasMultiTripPurchaseResponse(purchaseParams);
        Assert.assertTrue(response, response.matches("[A-Z]\\d{7,}"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void purchaseStudentSecureTest() {
        HCCMISStudentSecureRequest studentSecureRequest = new HCCMISStudentSecureRequest();
        studentSecureRequest.setReferId("6800");
        studentSecureRequest.setCulture("en-US");
        studentSecureRequest.setCoverageArea("I");
        studentSecureRequest.setCoverageBeginDate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        studentSecureRequest.setCoverageEndDate(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(7), DATE_FORMAT));
        studentSecureRequest.setDependentCoverage(0);
        studentSecureRequest.setOnlineFulfillment(1);
        studentSecureRequest.setSelectedPaymentType("F");
        studentSecureRequest.setSelectedPlanType("Select");
        studentSecureRequest.setPrimaryBeneficiary("Ritchie Zet");
        studentSecureRequest.setPrimaryCitizenship("Turkey");
        studentSecureRequest.setPrimaryBirthDate("12/10/1980");
        studentSecureRequest.setPrimaryEligibleRequirements(1);
        studentSecureRequest.setPrimaryEmailAddress("test@mail.com");
        studentSecureRequest.setPrimaryGender("M");
        studentSecureRequest.setPrimaryHomeCountry("Turkey");
        studentSecureRequest.setPrimaryHostCountry("United States");
        studentSecureRequest.setPrimaryMailAddress("123 Street");
        studentSecureRequest.setPrimaryMailCity("City");
        studentSecureRequest.setPrimaryMailCountry("United States");
        studentSecureRequest.setPrimaryMailState(StateCode.CA.name());
        studentSecureRequest.setPhysicallyLocated(0);
        studentSecureRequest.setPrimaryMailZip("12345");
        studentSecureRequest.setPrimaryNameFirst("Ritchie");
        studentSecureRequest.setPrimaryNameLast("Zet");
        studentSecureRequest.setPrimaryNameMiddle("");
        studentSecureRequest.setPrimaryPhone("123456789");
        studentSecureRequest.setPrimaryStudentScholarStatus(2);
        studentSecureRequest.setPrimaryUniversityName("universityName");
        studentSecureRequest.setPrimaryUsCitizenOrResident(0);
        studentSecureRequest.setPrimaryUsState(StateCode.CA.name());
        studentSecureRequest.setPrimaryVisaType("F-1");
        HCCMISStudentSecureRequest.CreditCard creditCard = studentSecureRequest.new CreditCard();
        creditCard.setCardExpirationMonth("12");
        creditCard.setCardExpirationYear("2025");
        creditCard.setCardHolderAddress("123 Main");
        creditCard.setCardHolderCity("City");
        creditCard.setCardHolderCountry("United States");
        creditCard.setCardHolderName("Ritchie Zet");
        creditCard.setCardHolderState(StateCode.CA.name());
        creditCard.setCardHolderZip("12345");
        creditCard.setCardNumber(4111111111111111l);
        creditCard.setCardSecurityCode("000");
        studentSecureRequest.setCreditCard(creditCard);
        List<Map<String, Object>> response = hccMedicalInsuranceServicesClient.getStudentSecurePurchaseResponse(studentSecureRequest);
        Map<String, Object> responseMap = response.get(0);
        Object object = responseMap.get(HCCMedicalInsuranceServicesClient.VALUE);
        String errorMessage = null;
        if (object instanceof String) {
            errorMessage = (String) object;
        } else if (object instanceof List) {
            errorMessage = ((ArrayList<String>) object).get(0);
        }
        Assert.assertTrue(errorMessage, responseMap.get(HCCMedicalInsuranceServicesClient.KEY).equals(HCCMedicalInsuranceServicesClient.CERT));
    }
}
