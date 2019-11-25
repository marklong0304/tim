package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.webservice.common.BeneficiaryRelation;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteResult;
import com.travelinsurancemaster.model.webservice.travelguard.*;
import com.travelinsurancemaster.services.clients.TravelGuardClient;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

;
;

/**
 * Created by ritchie on 3/5/15.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@Ignore
public class TravelGuardClientTest {

    @Autowired
    private InsuranceMasterApiProperties apiProperties;

    @Autowired
    private TravelGuardClient travelGuardClient;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private Map<String, InsuranceVendorApi> clients;

    private static final Logger log = LoggerFactory.getLogger(TravelGuardClientTest.class);

    private String errorMessage = null;
    private static final int PACKAGE_ID_1 = 17014;
    private static final int PACKAGE_ID_2 = 17015;
    private static final int PACKAGE_ID_3 = 17016;
    private static final int UNITS_OF_COVERAGE = 100000;
    private static final int DAYS = 1;
    private static final int PRIMARY_COVERAGE = 0;
    private static final String PLAN_ID = "117705";
    private static final String PRODUCT_CODE = "406700";
    private static final BigDecimal TRIP_COST = BigDecimal.valueOf(500.00);
    private static final String BIRTH_DATE = "04/24/1984";
    private static final String TRUE = "true";
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String EMAIL = "test@mail.com";
    private static final String FIRST = "First";
    private static final String LAST = "Last";
    private static final String CITY = "City";
    private static final String ADDRESS = "53rd Street";
    private static final String ZIP_CODE = "11705";
    private static final String CARD_NAME = "John Traveler";
    private static final String VISA = "Visa";
    private static final String EXP_MONTH = "12";
    private static final String EXP_YEAR = "2025";
    private static final String OTHER_EMAIL = "customerconfirmation@email.com";
    private static final Double TOTAL_PREMIUM_AMOUNT = 67.00;
    private static final String DEPART_DATE = TestUtils.getFormattedLocalDate(TestUtils.getIncrementedLocalDate(3), DATE_FORMAT);
    private static final String RETURN_DATE = TestUtils.getFormattedLocalDate(TestUtils.getIncrementedLocalDate(7), DATE_FORMAT);
    private static final String DEPOSIT_DATE = TestUtils.getFormattedLocalDate(TestUtils.getIncrementedLocalDate(-3), DATE_FORMAT);
    private static final String PAYMENT_DATE = TestUtils.getFormattedLocalDate(TestUtils.getIncrementedLocalDate(1), DATE_FORMAT);
    int AGENCY_ARC;
    String AGENT_EMAIL;
    private static final long CARD_NUMBER = 4111111111111111l;

    private QuoteRequest quoteRequest;

    @Before
    public void setup() {
        AGENCY_ARC = apiProperties.getTravelGuard().getArc();
        AGENT_EMAIL = apiProperties.getTravelGuard().getAgentEmail();
    }

    @Test
    public void quote() {
        PolicySpecificationTG policySpecification = new PolicySpecificationTG();
        PolicyTG policy = new PolicyTG();

        HashMap<Integer, Integer> coverages = new HashMap<>();
        coverages.put(PACKAGE_ID_1, UNITS_OF_COVERAGE);
        coverages.put(PACKAGE_ID_2, DAYS);
        coverages.put(PACKAGE_ID_3, null);
        OptionalPackagesTG optionalPackages = getOptionalPackagesTG(coverages);

        ProductRequestTG product = new ProductRequestTG(
                PLAN_ID,
                PRODUCT_CODE,
                TravelGuardClient.QUOTE,
                TravelGuardClient.MERCHANT_XML_QUOTE,
                optionalPackages);
        policy.setProduct(product);

        TravelersTG travelers = new TravelersTG();
        TravelerTG traveler = new TravelerTG(TRIP_COST, BIRTH_DATE);
        travelers.getTravelers().add(traveler);
        policy.setTravelers(travelers);

        DestinationsTG destinations = new DestinationsTG();
        DestinationTG destination = new DestinationTG(CountryCode.US, StateCode.NY);
        destinations.getDestinations().add(destination);

        TripTG trip = new TripTG(DEPART_DATE, RETURN_DATE, DEPOSIT_DATE, PAYMENT_DATE, destinations);
        policy.setTrip(trip);

        policySpecification.setPolicy(policy);

        PolicyQuoteDetailsTG response = travelGuardClient.quote(policySpecification);
        if (response.getStatus().getErrors() != null && !response.getStatus().getErrors().getErrorsList().isEmpty()) {
            errorMessage = response.getStatus().getErrors().getErrorsList().get(0).getDescription();
        }
        Assert.assertTrue(errorMessage, response.getStatus().getSuccess().equals(TRUE));
    }

    @Test
    public void quoteBasicPlan() {
        PolicySpecificationTG policySpecification = new PolicySpecificationTG();
        PolicyTG policy = new PolicyTG();

        HashMap<Integer, Integer> coverages = new HashMap<>();
        coverages.put(17655, UNITS_OF_COVERAGE);
        coverages.put(17653, DAYS);
        coverages.put(17653, null);
        OptionalPackagesTG optionalPackages = getOptionalPackagesTG(coverages);

        ProductRequestTG product = new ProductRequestTG(
                "133661",
                "807838",
                TravelGuardClient.QUOTE,
                TravelGuardClient.MERCHANT_XML_QUOTE,
                optionalPackages
        );
        policy.setProduct(product);

        TravelersTG travelers = new TravelersTG();
        TravelerTG traveler = new TravelerTG(TRIP_COST, BIRTH_DATE);
        travelers.getTravelers().add(traveler);
        policy.setTravelers(travelers);

        DestinationsTG destinations = new DestinationsTG();
        DestinationTG destination = new DestinationTG(CountryCode.US, StateCode.WA);
        destinations.getDestinations().add(destination);

        TripTG trip = new TripTG(DEPART_DATE, RETURN_DATE, DEPOSIT_DATE, PAYMENT_DATE, destinations);
        policy.setTrip(trip);

        policySpecification.setPolicy(policy);

        PolicyQuoteDetailsTG response = travelGuardClient.quote(policySpecification);
        if (response.getStatus().getErrors() != null && !response.getStatus().getErrors().getErrorsList().isEmpty()) {
            errorMessage = response.getStatus().getErrors().getErrorsList().get(0).getDescription();
        }
        Assert.assertTrue(errorMessage, response.getStatus().getSuccess().equals(TRUE));
    }

    @Test
    public void purchase() {
        PolicySpecificationTG policySpecification = new PolicySpecificationTG();
        PolicyTG policy = new PolicyTG();

        HashMap<Integer, Integer> coverages = new HashMap<>();
        coverages.put(PACKAGE_ID_1, UNITS_OF_COVERAGE);
        coverages.put(PACKAGE_ID_2, DAYS);
        coverages.put(PACKAGE_ID_3, null);
        OptionalPackagesTG optionalPackages = getOptionalPackagesTG(coverages);

        ProductRequestTG product = new ProductRequestTG(
                PLAN_ID, PRODUCT_CODE,
                TravelGuardClient.PURCHASE,
                TravelGuardClient.MERCHANT_XML_PURCHASE,
                optionalPackages
        );
        policy.setProduct(product);

        AddressTG address = new AddressTG(CountryCode.US, StateCode.NY, CITY, ADDRESS, ZIP_CODE);
        TravelerTG traveler = new TravelerTG(TRIP_COST, BIRTH_DATE, EMAIL, new TravelerNameTG(FIRST, LAST), address);
        TravelersTG travelers = new TravelersTG();
        travelers.getTravelers().add(traveler);
        policy.setTravelers(travelers);

        DestinationsTG destinations = new DestinationsTG();
        DestinationTG destination = new DestinationTG(CountryCode.US, StateCode.NY);
        destinations.getDestinations().add(destination);
        TripTG trip = new TripTG(DEPART_DATE, RETURN_DATE, DEPOSIT_DATE, PAYMENT_DATE, destinations);
        policy.setTrip(trip);

        CreditCardTG creditCard = new CreditCardTG(TOTAL_PREMIUM_AMOUNT, CARD_NAME, CARD_NUMBER, EXP_MONTH, EXP_YEAR, VISA);
        CreditCardDetailsTG creditCardDetails = new CreditCardDetailsTG(creditCard);
        PaymentTG payment = new PaymentTG(TOTAL_PREMIUM_AMOUNT, creditCardDetails);
        policy.setPayment(payment);

        FulfillmentTG fulfillment = new FulfillmentTG(TravelGuardClient.ONLINE, OTHER_EMAIL, TravelGuardClient.YES);
        policy.setFulfillment(fulfillment);

        AgencyTG agency = new AgencyTG(AGENCY_ARC, AGENT_EMAIL);
        policy.setAgency(agency);

        policySpecification.setPolicy(policy);

        PolicyPurchaseDetailsTG purchaseResponse = travelGuardClient.purchase(policySpecification);
        if (purchaseResponse.getStatus().getErrors() != null && !purchaseResponse.getStatus().getErrors().getErrorsList().isEmpty()) {
            errorMessage = purchaseResponse.getStatus().getErrors().getErrorsList().get(0).getDescription();
        }
        Assert.assertTrue(errorMessage, purchaseResponse.getStatus().getSuccess().equals(TRUE));
    }

    @Test
    public void quotePlatinum() throws IOException {
        prepareQuoteRequest();
        List<List<String>> productList = initTXT("src/test/resources/platinum.txt");
        productList.forEach(list -> {
            ParseTGproduct tGproduct = new ParseTGproduct();
            tGproduct.setPlanId(list.get(2));
            tGproduct.setProductCode(list.get(1));
            tGproduct.setStateCode(StateCode.valueOf(list.get(0)));
            if (list.get(3).equals("N/A")) {
                tGproduct.setCFAR(null);
            } else {
                tGproduct.setCFAR(Integer.valueOf(list.get(3)));
            }
            if (list.get(4).equals("N/A")) {
                tGproduct.setRentalCar(null);
            } else {
                tGproduct.setRentalCar(Integer.valueOf(list.get(4)));
            }
            if (list.get(5).equals("N/A")) {
                tGproduct.setMedical(null);
            } else {
                tGproduct.setMedical(Integer.valueOf(list.get(5)));
            }
            if (list.get(6).equals("N/A")) {
                tGproduct.setFligthADD(null);
            } else {
                tGproduct.setFligthADD(Integer.valueOf(list.get(6)));
            }
            if (list.get(7).equals("N/A")) {
                tGproduct.setWorkReas(null);
            } else {
                tGproduct.setWorkReas(Integer.valueOf(list.get(7)));
            }
            HashMap<Integer, Integer> coverages = checkCoverages(tGproduct);
            OptionalPackagesTG optionalPackages = getOptionalPackagesTG(coverages);
            try {
                quoteTest(tGproduct, optionalPackages, 39L);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        });
    }

    @Test
    public void quoteGold() throws IOException {
        prepareQuoteRequest();
        List<List<String>> productList = initTXT("src/test/resources/gold.txt");
        productList.forEach(list -> {
            ParseTGproduct tGproduct = new ParseTGproduct();
            tGproduct.setPlanId(list.get(2));
            tGproduct.setProductCode(list.get(1));
            tGproduct.setStateCode(StateCode.valueOf(list.get(0)));
            if (list.get(3).equals("N/A")) {
                tGproduct.setCFAR(null);
            } else {
                tGproduct.setCFAR(Integer.valueOf(list.get(3)));
            }
            if (list.get(4).equals("N/A")) {
                tGproduct.setRentalCar(null);
            } else {
                tGproduct.setRentalCar(Integer.valueOf(list.get(4)));
            }
            if (list.get(5).equals("N/A")) {
                tGproduct.setMedical(null);
            } else {
                tGproduct.setMedical(Integer.valueOf(list.get(5)));
            }
            if (list.get(6).equals("N/A")) {
                tGproduct.setFligthADD(null);
            } else {
                tGproduct.setFligthADD(Integer.valueOf(list.get(6)));
            }
            if (list.get(7).equals("N/A")) {
                tGproduct.setWorkReas(null);
            } else {
                tGproduct.setWorkReas(Integer.valueOf(list.get(7)));
            }
            if (list.get(8).equals("N/A")) {
                tGproduct.setMedUpgrade(null);
            } else {
                tGproduct.setMedUpgrade(Integer.valueOf(list.get(8)));
            }

            HashMap<Integer, Integer> coverages = checkCoverages(tGproduct);
            OptionalPackagesTG optionalPackages = getOptionalPackagesTG(coverages);
            Long policyMetaId = tGproduct.getStateCode().equals(StateCode.CO) ? 30892L : 28L;

            try {
                quoteTest(tGproduct, optionalPackages, policyMetaId);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        });
    }

    @Test
    public void quoteSilver() throws IOException {
        prepareQuoteRequest();
        List<List<String>> productList = initTXT("src/test/resources/silver.txt");
        productList.forEach(list -> {
            ParseTGproduct tGproduct = new ParseTGproduct();
            tGproduct.setPlanId(list.get(2));
            tGproduct.setProductCode(list.get(1));
            tGproduct.setStateCode(StateCode.valueOf(list.get(0)));
            if (list.get(3).equals("N/A")) {
                tGproduct.setRentalCar(null);
            } else {
                tGproduct.setRentalCar(Integer.valueOf(list.get(3)));
            }
            if (list.get(4).equals("N/A")) {
                tGproduct.setMedical(null);
            } else {
                tGproduct.setMedical(Integer.valueOf(list.get(4)));
            }
            if (list.get(5).equals("N/A")) {
                tGproduct.setFligthADD(null);
            } else {
                tGproduct.setFligthADD(Integer.valueOf(list.get(5)));
            }

            HashMap<Integer, Integer> coverages = checkCoverages(tGproduct);
            OptionalPackagesTG optionalPackages = getOptionalPackagesTG(coverages);
            Long policyMetaId = tGproduct.getStateCode().equals(StateCode.CO) ? 30892L : 47L;

            try {
                quoteTest(tGproduct, optionalPackages, policyMetaId);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        });
    }

    @Test
    public void quoteBasic() throws IOException {
        prepareQuoteRequest();
        List<List<String>> productList = initTXT("src/test/resources/basic.txt");
        productList.forEach(list -> {
            ParseTGproduct tGproduct = new ParseTGproduct();
            tGproduct.setPlanId(list.get(2));
            tGproduct.setProductCode(list.get(1));
            tGproduct.setStateCode(StateCode.valueOf(list.get(0)));
            if (list.get(3).equals("N/A")) {
                tGproduct.setRentalCar(null);
            } else {
                tGproduct.setRentalCar(Integer.valueOf(list.get(3)));
            }
            if (list.get(4).equals("N/A")) {
                tGproduct.setMedical(null);
            } else {
                tGproduct.setMedical(Integer.valueOf(list.get(4)));
            }
            if (list.get(5).equals("N/A")) {
                tGproduct.setFligthADD(null);
            } else {
                tGproduct.setFligthADD(Integer.valueOf(list.get(5)));
            }

            HashMap<Integer, Integer> coverages = checkCoverages(tGproduct);
            OptionalPackagesTG optionalPackages = getOptionalPackagesTG(coverages);
            Long policyMetaId = 30249L;

            try {
                quoteTest(tGproduct, optionalPackages, policyMetaId);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        });
    }

    @Test
    public void quotePlus() throws IOException {
        prepareQuoteRequest();

        List<List<String>> productList = initTXT("src/test/resources/plus.txt");
        productList.forEach((List<String> list) -> {
            ParseTGproduct tGproduct = new ParseTGproduct();
            OptionalPackageTG cFarCoverage = new OptionalPackageTG();
            tGproduct.setPlanId(list.get(2));
            tGproduct.setProductCode(list.get(1));
            tGproduct.setStateCode(StateCode.valueOf(list.get(0)));
            if (list.get(3).equals("N/A")) {
                tGproduct.setCFAR(null);
            } else {
                Integer cFAR = Integer.valueOf(list.get(3));
                cFarCoverage.setPackageId(cFAR);
                cFarCoverage.setUnitsOfCoverage(.50);
            }
            if (list.get(4).equals("N/A")) {
                tGproduct.setRentalCar(null);
            } else {
                tGproduct.setRentalCar(Integer.valueOf(list.get(4)));
            }
            if (list.get(5).equals("N/A")) {
                tGproduct.setFligthADD(null);
            } else {
                tGproduct.setFligthADD(Integer.valueOf(list.get(5)));
            }

            HashMap<Integer, Integer> coverages = checkCoverages(tGproduct);
            OptionalPackagesTG optionalPackages = getOptionalPackagesTG(coverages);
            if (cFarCoverage != null) {
                optionalPackages.getOptionalPackages().add(cFarCoverage);
            }
            Long policyMetaId = 30435L;

            try {
                quoteTest(tGproduct, optionalPackages, policyMetaId);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        });
    }

    private void quoteTest(ParseTGproduct tGproduct, OptionalPackagesTG optionalPackages, Long policyMetaId) throws ExecutionException, InterruptedException {
        quoteRequest.setResidentState(tGproduct.getStateCode());
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        List<PolicyMetaCategory> upsalesFromPolicy = policyMetaService.getUpsalesFromPolicy(policyMeta, quoteRequest);
        quoteRequest.getUpsaleValues().clear();
        upsalesFromPolicy.forEach(upsale -> quoteRequest.getUpsaleValues().put(upsale.getCategory().getCode(), upsale.getValues().get(1).getValue()));

        PolicySpecificationTG policySpecification = new PolicySpecificationTG();
        PolicyTG policy = new PolicyTG();

        ProductRequestTG product = new ProductRequestTG(
                tGproduct.getPlanId(),
                tGproduct.getProductCode(),
                TravelGuardClient.QUOTE,
                TravelGuardClient.MERCHANT_XML_QUOTE,
                optionalPackages);
        policy.setProduct(product);

        TravelersTG travelers = new TravelersTG();
        TravelerTG traveler = new TravelerTG(TRIP_COST, BIRTH_DATE);
        travelers.getTravelers().add(traveler);
        policy.setTravelers(travelers);

        DestinationsTG destinations = new DestinationsTG();
        DestinationTG destination = new DestinationTG(CountryCode.US, tGproduct.getStateCode());
        destinations.getDestinations().add(destination);

        TripTG trip = new TripTG(DEPART_DATE, RETURN_DATE, DEPOSIT_DATE, PAYMENT_DATE, destinations);
        policy.setTrip(trip);

        policySpecification.setPolicy(policy);

        PolicyQuoteDetailsTG response = travelGuardClient.quote(policySpecification);
        if (response.getStatus().getErrors() != null && !response.getStatus().getErrors().getErrorsList().isEmpty()) {
            errorMessage = response.getStatus().getErrors().getErrorsList().get(0).getDescription();
        }
        Assert.assertTrue(errorMessage, response.getStatus().getSuccess().equals(TRUE));

        QuoteResult result = quoteAbstractTest(ApiVendor.TravelGuard, policyMeta);
        Assert.assertNotNull(result);

    }

    private OptionalPackagesTG getOptionalPackagesTG(HashMap<Integer, Integer> coverages) {
        OptionalPackagesTG optionalPackages = new OptionalPackagesTG();
        coverages.forEach((k, v) -> {
            OptionalPackageTG optionalPackageTG = new OptionalPackageTG();
            optionalPackageTG.setPackageId(k);
            if (v != null) {
                switch (v) {
                    case DAYS:
                        optionalPackageTG.setDays(DAYS);
                        optionalPackageTG.setUnitsOfCoverage(35000.0);
                        break;
                    case UNITS_OF_COVERAGE:
                        optionalPackageTG.setUnitsOfCoverage(UNITS_OF_COVERAGE);
                        break;
                    case PRIMARY_COVERAGE:
                        optionalPackageTG.setPrimaryCoverage(PRIMARY_COVERAGE);
                        break;
                }
            }
            optionalPackages.getOptionalPackages().add(optionalPackageTG);
        });
        return optionalPackages;
    }

    private void prepareQuoteRequest() {
        quoteRequest = new QuoteRequest();
        quoteRequest.setDepartDate(TestUtils.getIncrementedLocalDate(3));
        quoteRequest.setReturnDate(TestUtils.getIncrementedLocalDate(7));
        quoteRequest.setDepositDate(TestUtils.getIncrementedLocalDate(-3));
        quoteRequest.setResidentCountry(CountryCode.US);
        quoteRequest.setCitizenCountry(CountryCode.US);
        quoteRequest.setTripCost(BigDecimal.valueOf(500.));
        quoteRequest.setDestinationCountry(CountryCode.CA);
        quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
        GenericTraveler traveler = new GenericTraveler();
        traveler.setTripCost(BigDecimal.valueOf(500.));
        traveler.setAge(34);
        traveler.setFirstName("John");
        traveler.setLastName("Smith");
        traveler.setMiddleInitials("");
        traveler.setPrimary(true);
        traveler.setBeneficiary("Test Testerson");
        traveler.setBeneficiaryRelation(BeneficiaryRelation.Spouse);
        quoteRequest.getTravelers().add(traveler);
    }

    private List<List<String>> initTXT(String fileName) throws IOException {
        Stream<String> lines = Files.lines(Paths.get(fileName));
        return lines.map(line -> Arrays.asList(line.replaceAll("\\s+", "\t").split("\\t"))).collect(Collectors.toList());
    }

    private HashMap<Integer, Integer> checkCoverages(ParseTGproduct product) {
        HashMap<Integer, Integer> intMap = new HashMap<>();
        if (product.getCFAR() != null) {
            intMap.put(product.getCFAR(), UNITS_OF_COVERAGE);
        }
        if (product.getFligthADD() != null) {
            intMap.put(product.getFligthADD(), UNITS_OF_COVERAGE);
        }
        if (product.getRentalCar() != null) {
            intMap.put(product.getRentalCar(), DAYS);
        }
        if (product.getMedical() != null) {
            intMap.put(product.getMedical(), null);
        }
        if (product.getWorkReas() != null) {
            intMap.put(product.getWorkReas(), null);
        }
        if (product.getMedUpgrade() != null) {
            intMap.put(product.getMedUpgrade(), null);
        }
        return intMap;
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
}

class ParseTGproduct {
    private String productCode;
    private String planId;
    private StateCode stateCode;
    private Integer rentalCar;
    private Integer cFAR;
    private Integer fligthADD;
    private Integer medical;
    private Integer workReas;
    private Integer medUpgrade;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public StateCode getStateCode() {
        return stateCode;
    }

    public void setStateCode(StateCode stateCode) {
        this.stateCode = stateCode;
    }

    public Integer getRentalCar() {
        return rentalCar;
    }

    public void setRentalCar(Integer rentalCar) {
        this.rentalCar = rentalCar;
    }

    public Integer getCFAR() {
        return cFAR;
    }

    public void setCFAR(Integer cFAR) {
        this.cFAR = cFAR;
    }

    public Integer getFligthADD() {
        return fligthADD;
    }

    public void setFligthADD(Integer fligthADD) {
        this.fligthADD = fligthADD;
    }

    public Integer getMedical() {
        return medical;
    }

    public void setMedical(Integer medical) {
        this.medical = medical;
    }

    public Integer getWorkReas() {
        return workReas;
    }

    public void setWorkReas(Integer workReas) {
        this.workReas = workReas;
    }
    public void setMedUpgrade(Integer medUpgrade){
        this.medUpgrade = medUpgrade;
    }

    public Integer getMedUpgrade() {
        return medUpgrade;
    }
}