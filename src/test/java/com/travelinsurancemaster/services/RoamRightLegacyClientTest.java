package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.clients.roamright.*;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.common.BeneficiaryRelation;
import com.travelinsurancemaster.services.clients.RoamRightClient;
import com.travelinsurancemaster.util.DateUtil;
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

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;

;
;

/**
 * Created by ritchie on 1/28/16.
 */
@Ignore
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class RoamRightLegacyClientTest {

    private static final Logger log = LoggerFactory.getLogger(RoamRightLegacyClientTest.class);

    private static final String PLAN_ADVENTURE = "43";
    private static final String PLAN_ELITE = "40";
    private static final String COVERAGE_OPTIONAL_ADDAIR = "OPTIONAL ADDAIR";
    private static final String GIVEN_NAME = "Test";
    private static final String MIDDLE_NAME = "M.";
    private static final String SURNAME = "Testerson";

    private GetInsuranceQuote quoteRequest;
    private SubmitInsuranceBooking purchaseRequest;

    @Autowired
    private RoamRightClient roamRightClient;

    @Autowired
    private TestCreditCards creditCards;

    @Before
    public void prepareQuoteRequest() {
        quoteRequest = new GetInsuranceQuote();
        GetInsuranceQuote.OTAInsuranceQuoteRQ insuranceQuoteRQ = new GetInsuranceQuote.OTAInsuranceQuoteRQ();
        quoteRequest.setOTAInsuranceQuoteRQ(insuranceQuoteRQ);
        //insuranceQuoteRQ.setVersion(RoamRightClient.REQUEST_VERSION);
        GetInsuranceQuote.OTAInsuranceQuoteRQ.PlanForQuoteRQ planForQuoteRQ =
                new GetInsuranceQuote.OTAInsuranceQuoteRQ.PlanForQuoteRQ();
        planForQuoteRQ.setPlanID(PLAN_ADVENTURE);
        // Covered travelers
        InsCoverageType.CoveredTravelers coveredTravelers = new InsCoverageType.CoveredTravelers();
        CoveredTravelerType coveredTraveler = getCoveredTraveler();
        AddressInfoType address = getAddress(new AddressInfoType(), false);
        coveredTraveler.setAddress(address);
        // Coverage details
        InsCoverageDetailType insCoverageDetail = new InsCoverageDetailType();
        XMLGregorianCalendar effectiveDate = DateUtil.getXMLGregorianCalendar(TestUtils.getIncrementedDate(3));
        insCoverageDetail.setEffectiveDate(effectiveDate);
        XMLGregorianCalendar expireDate = DateUtil.getXMLGregorianCalendar(TestUtils.getIncrementedDate(7));
        insCoverageDetail.setEffectiveDate(expireDate);
        // Coverage requirements
        InsCoverageDetailType.CoverageRequirements coverageRequirements = getCoverageRequirements();
        // Total trip cost
        InsCoverageDetailType.TotalTripCost totalTripCost = getTotalTripCost();
        // Covered trips
        InsCoverageDetailType.CoveredTrips coveredTrips = getCoveredTrips();
        coveredTravelers.getCoveredTraveler().add(coveredTraveler);
        insCoverageDetail.setCoveredTrips(coveredTrips);
        insCoverageDetail.setTotalTripCost(totalTripCost);
        insCoverageDetail.setCoverageRequirements(coverageRequirements);
        planForQuoteRQ.setInsCoverageDetail(insCoverageDetail);
        planForQuoteRQ.setCoveredTravelers(coveredTravelers);
        insuranceQuoteRQ.setPlanForQuoteRQ(planForQuoteRQ);
    }

    private InsCoverageDetailType.CoveredTrips getCoveredTrips() {
        InsCoverageDetailType.CoveredTrips coveredTrips = new InsCoverageDetailType.CoveredTrips();
        InsCoverageDetailType.CoveredTrips.CoveredTrip coveredTrip = new InsCoverageDetailType.CoveredTrips.CoveredTrip();
        TripFeaturesType.Destinations destinations = new TripFeaturesType.Destinations();
        TripFeaturesType.Destinations.Destination destination = new TripFeaturesType.Destinations.Destination();
        CountryNameType destinationCountry = new CountryNameType();
        destinationCountry.setCode(CountryCode.DO.name());
        destination.setCountryName(destinationCountry);
        coveredTrip.setDestinations(destinations);
        XMLGregorianCalendar depositDate = DateUtil.getXMLGregorianCalendar(TestUtils.getIncrementedDate(-1));
        coveredTrip.setDepositDate(depositDate);
        XMLGregorianCalendar finalPaymentDate = DateUtil.getXMLGregorianCalendar(TestUtils.getIncrementedDate(0));
        coveredTrip.setFinalPayDate(finalPaymentDate);
        coveredTrip.getDestinations().getDestination().add(destination);
        coveredTrips.getCoveredTrip().add(coveredTrip);
        return coveredTrips;
    }

    private InsCoverageDetailType.TotalTripCost getTotalTripCost() {
        InsCoverageDetailType.TotalTripCost totalTripCost = new InsCoverageDetailType.TotalTripCost();
        totalTripCost.setAmount(BigDecimal.valueOf(1000));
        return totalTripCost;
    }

    private InsCoverageDetailType.CoverageRequirements getCoverageRequirements() {
        InsCoverageDetailType.CoverageRequirements coverageRequirements = new InsCoverageDetailType.CoverageRequirements();
        CoverageLimitType coverageRequirement = new CoverageLimitType();
        coverageRequirement.setCoverageTypeEnum(COVERAGE_OPTIONAL_ADDAIR);
        CoverageLimitType.Deductible deductible = new CoverageLimitType.Deductible();
        deductible.setAmount(BigDecimal.valueOf(50));
        coverageRequirement.setDeductible(deductible);
        CoverageLimitType.IndividualLimit individualLimit = new CoverageLimitType.IndividualLimit();
        individualLimit.setAmount(BigDecimal.valueOf(200));
        coverageRequirement.setIndividualLimit(individualLimit);
        coverageRequirements.getCoverageRequirement().add(coverageRequirement);
        return coverageRequirements;
    }

    private <T extends AddressType> T getAddress(T address, boolean isPurchaseAddress) {
        StateProvType state = new StateProvType();
        state.setStateCode(StateCode.MD.name());
        CountryNameType countryName = new CountryNameType();
        countryName.setCode(CountryCode.US.name());
        address.setCountryName(countryName);
        address.setStateProv(state);
        if (isPurchaseAddress) {
            address.setAddressLine("125125 test st.");
            address.setCityName("Towson");
            address.setPostalCode("21086");
        }
        return address;
    }

    private CoveredTravelerType getCoveredTraveler() {
        CoveredTravelerType coveredTraveler = new CoveredTravelerType();
        CoveredTravelerTypeCoveredPerson coveredPerson = new CoveredTravelerTypeCoveredPerson();
        XMLGregorianCalendar birthDate = DateUtil.getXMLGregorianCalendar(DateUtil.getDate(1973, 10, 2), true);
        coveredPerson.setBirthDate(birthDate);
        coveredPerson.setGivenName(GIVEN_NAME);
        coveredPerson.setMiddleName(MIDDLE_NAME);
        coveredPerson.setSurname(SURNAME);
        coveredTraveler.setCoveredPerson(coveredPerson);
        CoveredTravelerType.CitizenCountryName citizenCountry = new CoveredTravelerType.CitizenCountryName();
        citizenCountry.setCode(CountryCode.US.name());
        coveredTraveler.setCitizenCountryName(citizenCountry);
        return coveredTraveler;
    }

    @Before
    public void preparePurchaseRequest() {
        purchaseRequest = new SubmitInsuranceBooking();
        SubmitInsuranceBooking.OTAInsuranceBookRQ insuranceBookRQ = new SubmitInsuranceBooking.OTAInsuranceBookRQ();
        //insuranceBookRQ.setVersion(RoamRightClient.REQUEST_VERSION);
        SubmitInsuranceBooking.OTAInsuranceBookRQ.PlanForBookRQ planForBookRQ = new SubmitInsuranceBooking.OTAInsuranceBookRQ.PlanForBookRQ();
        planForBookRQ.setPlanID(PLAN_ELITE);
        // Covered travelers
        InsCoverageType.CoveredTravelers coveredTravelers = new InsCoverageType.CoveredTravelers();
        CoveredTravelerType coveredTraveler = getCoveredTraveler();
        // Address
        AddressInfoType address = getAddress(new AddressInfoType(), true);
        coveredTraveler.setAddress(address);
        EmailType emailType = new EmailType();
        emailType.setValue("test@mailinator.com");
        coveredTraveler.setEmail(emailType);
        CoveredTravelerType.Telephone telephone = new CoveredTravelerType.Telephone();
        telephone.setPhoneNumber("4102345522");
        coveredTraveler.setTelephone(telephone);
        CoveredTravelerType.Beneficiary beneficiary = new CoveredTravelerType.Beneficiary();
        beneficiary.setRelation(BeneficiaryRelation.Other.name());
        beneficiary.setBenefitPercent(BigDecimal.valueOf(100));
        CoveredTravelerType.Beneficiary.Name name = new CoveredTravelerType.Beneficiary.Name();
        name.setGivenName(GIVEN_NAME);
        name.setSurname(SURNAME);
        beneficiary.setName(name);
        InsCoverageDetailType insCoverageDetail = new InsCoverageDetailType();
        XMLGregorianCalendar effectiveDate = DateUtil.getXMLGregorianCalendar(TestUtils.getIncrementedDate(3));
        insCoverageDetail.setEffectiveDate(effectiveDate);
        XMLGregorianCalendar expireDate = DateUtil.getXMLGregorianCalendar(TestUtils.getIncrementedDate(7));
        insCoverageDetail.setExpireDate(expireDate);
        // Coverage requirements
        InsCoverageDetailType.CoverageRequirements coverageRequirements = getCoverageRequirements();
        // Total trip cost
        InsCoverageDetailType.TotalTripCost totalTripCost = getTotalTripCost();
        // Covered trips
        InsCoverageDetailType.CoveredTrips coveredTrips = getCoveredTrips();
        // Insurance customer
        InsuranceCustomerType insuranceCustomer = new InsuranceCustomerType();
        PersonNameType personName = new PersonNameType();
        personName.setGivenName(GIVEN_NAME);
        personName.setSurname(SURNAME);
        insuranceCustomer.setPersonName(personName);
        CustomerType.Address customerAddress = getAddress(new CustomerType.Address(), true);
        CustomerType.PaymentForm paymentForm = new CustomerType.PaymentForm();
        PaymentCardType paymentCard = new PaymentCardType();
        CreditCard creditCard = creditCards.getCreditCardByVendorCode(ApiVendor.RoamRight);
        paymentCard.setCardNumber(String.valueOf(creditCard.getCcNumber()));
        paymentCard.setSeriesCode(creditCard.getCcCode());
        String expireCardDate = creditCard.getCcExpMonth() + creditCard.getCcExpYear().substring(2);
        paymentCard.setExpireDate(expireCardDate);
        paymentCard.setCardHolderName(creditCard.getCcName());
        paymentForm.setPaymentCard(paymentCard);
        insuranceCustomer.setPaymentForm(paymentForm);
        insuranceCustomer.setAddress(customerAddress);
        planForBookRQ.setInsuranceCustomer(insuranceCustomer);
        PlanCostType planCost = new PlanCostType();
        planCost.setAmount(BigDecimal.valueOf(88.));
        planForBookRQ.setPlanCost(planCost);
        coveredTraveler.getBeneficiary().add(beneficiary);
        coveredTravelers.getCoveredTraveler().add(coveredTraveler);
        planForBookRQ.setCoveredTravelers(coveredTravelers);
        insCoverageDetail.setCoverageRequirements(coverageRequirements);
        insCoverageDetail.setTotalTripCost(totalTripCost);
        insCoverageDetail.setCoveredTrips(coveredTrips);
        planForBookRQ.setInsCoverageDetail(insCoverageDetail);
        insuranceBookRQ.setPlanForBookRQ(planForBookRQ);
        purchaseRequest.setOTAInsuranceBookRQ(insuranceBookRQ);
    }

    @Test
    public void quote() {
        /*
        GetInsuranceQuoteResponse.OTAInsuranceQuoteRS quoteResponse = roamRightClient.quote(quoteRequest);
        List<Object> planForQuoteRSOrSuccessOrErrorsList = quoteResponse.getPlanForQuoteRSOrSuccessOrErrors();
        assertTrue("Response is empty!", !CollectionUtils.isEmpty(planForQuoteRSOrSuccessOrErrorsList));
        Object object = planForQuoteRSOrSuccessOrErrorsList.get(0);
        assertTrue("Error!", object instanceof GetInsuranceQuoteResponse.OTAInsuranceQuoteRS.PlanForQuoteRS);
        */
    }

    @Test
    public void purchase() {
        /*
        SubmitInsuranceBookingResponse.OTAInsuranceBookRS purchaseResponse = roamRightClient.purchase(purchaseRequest);
        List<Object> errorsOrSuccessOrPlanForBookRSList = purchaseResponse.getErrorsOrSuccessOrPlanForBookRS();
        assertTrue("Response is empty!", !CollectionUtils.isEmpty(errorsOrSuccessOrPlanForBookRSList));
        Object object = errorsOrSuccessOrPlanForBookRSList.get(0);
        String errorMessage = null;
        if (object instanceof WarningsType) {
            errorMessage = ((WarningsType) object).getWarning().get(0).getShortText();
        }
        if (object instanceof ErrorsType) {
            errorMessage = ((ErrorsType) object).getError().get(0).getShortText();
        }
        assertFalse(errorMessage, object instanceof WarningsType);
        assertFalse(errorMessage, object instanceof ErrorsType);
        */
    }
}
