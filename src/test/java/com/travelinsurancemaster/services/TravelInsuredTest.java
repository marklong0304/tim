package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.clients.travelinsured.xsd.*;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.services.clients.TravelInsuredClient;
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

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;


/**
 * Created by Vlad on 09.02.2015.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class TravelInsuredTest {
    private static final Logger log = LoggerFactory.getLogger(TravelInsuredTest.class);
    private static final String POLICY_ID_ERROR = "0";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    private TravelInsuredClient travelInsuredClient;

    private OTAInsuranceBookRQ bookRequest;
    private OTAInsuranceQuoteRQ quoteRequest;
    private String errorMessage = null;

    @Before
    public void initQuoteRequest() {
        quoteRequest = new OTAInsuranceQuoteRQ();
        POSType pos = new POSType();
        SourceType sourceType = new SourceType();
        SourceType.RequestorID requestorID = new SourceType.RequestorID();
        InsCoverageType.CoveredTravelers coveredTravelers = new InsCoverageType.CoveredTravelers();
        CompanyNameType companyName = new CompanyNameType();
        OTAInsuranceQuoteRQ.PlanForQuoteRQ planForQuoteRQ = new OTAInsuranceQuoteRQ.PlanForQuoteRQ();
        CoveredTravelerType coveredTravelerType = new CoveredTravelerType();
        CoveredTravelerType.CoveredPerson coveredPerson = new CoveredTravelerType.CoveredPerson();
        XMLGregorianCalendar calendar = null;
        try {
            calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        } catch (DatatypeConfigurationException e) {
            log.error(e.getMessage(), e);
        }
        CoveredTravelerType.CitizenCountryName citizenCountryName = new CoveredTravelerType.CitizenCountryName();
        IndCoverageReqsType indCoveredType = new IndCoverageReqsType();
        IndCoverageReqsType.IndTripCost indTripCost = new IndCoverageReqsType.IndTripCost();
        InsCoverageDetailType insCoverageDetailType = new InsCoverageDetailType();
        InsCoverageDetailType.CoveredTrips coveredTrips = new InsCoverageDetailType.CoveredTrips();
        InsCoverageDetailType.CoveredTrips.CoveredTrip coveredTrip = new InsCoverageDetailType.CoveredTrips.CoveredTrip();

        quoteRequest.setVersion(BigDecimal.ONE);
        quoteRequest.getPlanForQuoteRQ().add(planForQuoteRQ);

        sourceType.setAgentSine("travelmaster1");
        requestorID.setID("travelmaster1");
        requestorID.setMessagePassword("A#eB5FnP");
        requestorID.setMessagePassword("A#eB5FnP");
        requestorID.setType("1");
        companyName.setCode("52784");
        companyName.setCodeContext("TII");

        planForQuoteRQ.setPlanID("0");
        planForQuoteRQ.setTypeID("WTP14");
        calendar.setYear(1971);
        calendar.setMonth(1);
        calendar.setDay(1);

        coveredPerson.setBirthDate(calendar);
        coveredPerson.setRelation("PrimaryTraveler");
        coveredPerson.getGivenName().add("John");
        coveredPerson.setSurname("Doe");

        citizenCountryName.setCode(CountryCode.US.name());
        indTripCost.setAmount(new BigDecimal(1500));
        insCoverageDetailType.setType("SingleTrip");

        coveredTrip.setStart(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        coveredTrip.setEnd(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(7), DATE_FORMAT));

        planForQuoteRQ.setInsCoverageDetail(insCoverageDetailType);

        insCoverageDetailType.setCoveredTrips(coveredTrips);

        coveredTrips.getCoveredTrip().add(coveredTrip);

        indCoveredType.setIndTripCost(indTripCost);

        coveredTravelerType.setCitizenCountryName(citizenCountryName);
        coveredTravelerType.setCoveredPerson(coveredPerson);
        coveredTravelerType.setIndCoverageReqs(indCoveredType);

        coveredTravelers.getCoveredTraveler().add(coveredTravelerType);

        planForQuoteRQ.setCoveredTravelers(coveredTravelers);

        quoteRequest.setPOS(pos);
        pos.getSource().add(sourceType);
        sourceType.setRequestorID(requestorID);
        requestorID.setCompanyName(companyName);
    }

    @Before
    public void initBookRequest() {
        bookRequest = new OTAInsuranceBookRQ();
        POSType posType = new POSType();
        SourceType sourceType = new SourceType();
        SourceType.RequestorID requestorID = new SourceType.RequestorID();
        CompanyNameType companyName = new CompanyNameType();
        OTAInsuranceBookRQ.PlanForBookRQ planForBookRQ = new OTAInsuranceBookRQ.PlanForBookRQ();
        CoveredTravelerType travelerType = new CoveredTravelerType();
        CoveredTravelerType.CoveredPerson coveredPerson = new CoveredTravelerType.CoveredPerson();
        XMLGregorianCalendar birthDate = null;
        try {
            birthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        } catch (DatatypeConfigurationException e) {
            log.error(e.getMessage(), e);
        }
        InsCoverageType.CoveredTravelers coveredTravelers = new InsCoverageType.CoveredTravelers();
        AddressInfoType addressInfoType = new AddressInfoType();

        StateProvType stateProvType = new StateProvType();
        CountryNameType countryName = new CountryNameType();
        CoveredTravelerType.Telephone telephone = new CoveredTravelerType.Telephone();
        CoveredTravelerType.CitizenCountryName citizenCountryName = new CoveredTravelerType.CitizenCountryName();
        CoveredTravelerType.Beneficiary beneficiary = new CoveredTravelerType.Beneficiary();
        CoveredTravelerType.Beneficiary.Name name = new CoveredTravelerType.Beneficiary.Name();
        IndCoverageReqsType indCoverageReqs = new IndCoverageReqsType();
        IndCoverageReqsType.IndTripCost indTripCost = new IndCoverageReqsType.IndTripCost();
        InsCoverageDetailType insCoverageDetail = new InsCoverageDetailType();
        XMLGregorianCalendar effectiveDate = null;
        try {
            effectiveDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        } catch (DatatypeConfigurationException e) {
            log.error(e.getMessage(), e);
        }

        RequestedCoveragesType coverageRequirements = new RequestedCoveragesType();
        CoverageLimitType coverageLimitType = new CoverageLimitType();
        InsCoverageDetailType.CoveredTrips coveredTrips = new InsCoverageDetailType.CoveredTrips();
        InsCoverageDetailType.CoveredTrips.CoveredTrip coveredTrip = new InsCoverageDetailType.CoveredTrips.CoveredTrip();
        InsuranceCustomerType insuranceCustomer = new InsuranceCustomerType();
        TripFeaturesType.Destinations destinations = new TripFeaturesType.Destinations();
        TripFeaturesType.Destinations.Destination destination = new TripFeaturesType.Destinations.Destination();
        PaymentFormType paymentForm = new PaymentFormType();
        DistribPrefType distribPrefType = new DistribPrefType();
        PaymentCardType paymentCard = new PaymentCardType();
        PlanCostType planCost = new PlanCostType();
        PlanCostType.BasePremium basePremium = new PlanCostType.BasePremium();

        bookRequest.setVersion(BigDecimal.ONE);
        bookRequest.setPOS(posType);

        posType.getSource().add(sourceType);

        sourceType.setAgentSine("travelmaster1");
        sourceType.setRequestorID(requestorID);

        requestorID.setID("travelmaster1");
        requestorID.setMessagePassword("A#eB5FnP");

        requestorID.setType("1");
        requestorID.setCompanyName(companyName);

        companyName.setCode("52784");
        companyName.setCodeContext("TII");

        bookRequest.getPlanForBookRQ().add(planForBookRQ);
        planForBookRQ.setPlanID("0");
        planForBookRQ.setTypeID("WTP14");

        planForBookRQ.setCoveredTravelers(coveredTravelers);
        coveredTravelers.getCoveredTraveler().add(travelerType);
        coveredPerson.setBirthDate(birthDate);
        birthDate.setYear(1971);
        birthDate.setMonth(1);
        birthDate.setDay(1);

        coveredPerson.setRelation("PrimaryTraveler");
        coveredPerson.getGivenName().add(UUID.randomUUID().toString());
        coveredPerson.setSurname(UUID.randomUUID().toString());

//        AddressesType.Address address = new AddressesType.Address();
//        addressType.getAddress().add(address);
        addressInfoType.setType("1");
        addressInfoType.getAddressLine().add("111 Asylum Ave");
        addressInfoType.setCityName("Hartford");
        addressInfoType.setPostalCode("06067");

        stateProvType.setStateCode(StateCode.CT.name());

        countryName.setCode(CountryCode.US.name());

        telephone.setPhoneNumber("8601111111");
        telephone.setPhoneUseType("4");


        beneficiary.setBenefitPercent(BigDecimal.valueOf(100));
        name.getGivenName().add("Mary");
        name.setSurname("Doe");


        effectiveDate.setYear(2007);
        effectiveDate.setMonth(5);
        effectiveDate.setDay(15);

        insCoverageDetail.setType("SingleTrip");
        insCoverageDetail.setEffectiveDate(effectiveDate);

        coverageLimitType.setCoverageLevel("Primary");
        coverageLimitType.setCoverageType("1");

        coverageRequirements.getCoverageRequirement().add(coverageLimitType);


        coveredTrip.setStart(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        coveredTrip.setEnd(TestUtils.getFormattedDate(TestUtils.getIncrementedDate(7), DATE_FORMAT));


        destination.setCityName("Las Vegas");
        distribPrefType.setDistribType("3");

        paymentCard.setCardCode("VI");
        paymentCard.setExpireDate("0922");

        paymentCard.setCardNumber("4111111111111111");
        paymentCard.setSeriesCode("123");

        paymentCard.setCardHolderName("John P.Doe");

        planCost.setAmount(BigDecimal.valueOf(72));
        basePremium.setAmount(BigDecimal.valueOf(72));
        planCost.setBasePremium(basePremium);

        planForBookRQ.setPlanCost(planCost);

        paymentForm.setPaymentCard(paymentCard);

        insuranceCustomer.getPaymentForm().add(paymentForm);

        planForBookRQ.setInsuranceCustomer(insuranceCustomer);

        insCoverageDetail.getDeliveryPref().add(distribPrefType);

        destinations.getDestination().add(destination);

        coveredTrip.setDestinations(destinations);

        coveredTrips.getCoveredTrip().add(coveredTrip);

        insCoverageDetail.setCoveredTrips(coveredTrips);
        insCoverageDetail.setCoverageRequirements(coverageRequirements);

        planForBookRQ.setInsCoverageDetail(insCoverageDetail);

        indTripCost.setAmount(BigDecimal.valueOf(1500));

        indCoverageReqs.setIndTripCost(indTripCost);

        travelerType.setIndCoverageReqs(indCoverageReqs);

        beneficiary.setName(name);
        travelerType.getBeneficiary().add(beneficiary);
        citizenCountryName.setCode(CountryCode.US.name());

        travelerType.setCitizenCountryName(citizenCountryName);

        travelerType.getTelephone().add(telephone);
        addressInfoType.setCountryName(countryName);
        addressInfoType.setStateProv(stateProvType);

        travelerType.getAddress().add(addressInfoType);

        travelerType.setCoveredPerson(coveredPerson);
    }

    @Test
    public void quote() throws IOException, JAXBException {
        OTAInsuranceQuoteRS quoteRS = travelInsuredClient.quote(quoteRequest);
        if (quoteRS.getErrors() != null && !quoteRS.getErrors().getError().isEmpty()) {
            errorMessage = quoteRS.getErrors().getError().get(0).getValue();
        }
        assertNull(errorMessage, quoteRS.getErrors());
        if (quoteRS.getWarnings() != null && !quoteRS.getWarnings().getWarning().isEmpty()) {
            errorMessage = quoteRS.getWarnings().getWarning().get(0).getValue();
        }
        assertNull(errorMessage, quoteRS.getWarnings());
        assertNotNull(quoteRS.getSuccess());
    }

    @Test
    @Ignore
    public void book() throws JAXBException {
        Object bookRS = travelInsuredClient.book(bookRequest);
        if (bookRS instanceof OTAErrorRS) {
            errorMessage = ((OTAErrorRS) bookRS).getErrorMessage();
        }
        assertTrue(errorMessage, bookRS instanceof OTAInsuranceBookRS);
        assertNotEquals(POLICY_ID_ERROR, ((OTAInsuranceBookRS) bookRS).getPlanForBookRS().get(0).getPolicyDetail().getPolicyNumber().getID());
    }

}
