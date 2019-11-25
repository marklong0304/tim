package com.travelinsurancemaster.services.clients;


import com.travelinsurancemaster.clients.travelinsured.*;
import com.travelinsurancemaster.clients.travelinsured.xsd.OTAInsuranceBookRS;
import com.travelinsurancemaster.clients.travelinsured.xsd.OTAInsuranceQuoteRS;
import com.travelinsurancemaster.clients.travelinsured.xsd.*;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.services.AbstractSoapClient;
import com.travelinsurancemaster.services.PolicyMetaCategoryValueService;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Alexander.Isaenco
 */
public class TravelInsuredClient extends AbstractSoapClient {

    private static final Logger log = LoggerFactory.getLogger(TravelInsuredClient.class);

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(TravelInsuredClient.DATE_FORMAT);

    private static final String REQUESTOR_ID_TYPE = "1";
    private static final String CODE_CONTEXT = "TII";
    private static final String PLAN_ID = "0";
    private static final String COVERAGE_DETAIL_TYPE = "SingleTrip";
    private static final String ADDRESS_INFO_TYPE = "1";
    private static final String TRAVELER = "Traveler";
    private static final String PRIMARY_TRAVELER = "PrimaryTraveler";
    private static final String PHONE_USE_TYPE = "4";
    private static final String PERSONAL_EMAIL_TYPE = "1";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final SoapActionCallback quoteAction =
            new SoapActionCallback("https://www.travelinsured.com/TIWebService/Quote");
    private static final SoapActionCallback bookAction =
            new SoapActionCallback("https://www.travelinsured.com/TIWebService/Book");
    private static final SoapActionCallback searchAction =
            new SoapActionCallback("https://www.travelinsured.com/TIWebService/Search");

    private static final String PRIMARY_COVERAGE = "6";
    private static final String COVERAGE_LEVEL_OPTIONAL = "Optional";
    private static final String COVERAGE_LEVEL_PRIMARY = "Primary";
    private static final String BENEFITS_UPGRADE_COVERAGE = "7";

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;

    private final JAXBContext jaxbQuoteContext;
    private final JAXBContext jaxbBookContext;

    public TravelInsuredClient() throws JAXBException {
        jaxbQuoteContext = JAXBContext.newInstance(OTAInsuranceQuoteRQ.class);
        jaxbBookContext = JAXBContext.newInstance(OTAInsuranceBookRQ.class);
    }

    public OTAInsuranceQuoteRS quote(OTAInsuranceQuoteRQ quoteRQ) throws IOException, JAXBException {
        /*try {
            search(new OTAInsurancePlanSearchRQ());
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }*/
        StringWriter writer = new StringWriter();

        jaxbQuoteContext.createMarshaller().marshal(quoteRQ, writer);

        Quote quote = new Quote();
        quote.setOTAInsuranceQuoteRQ(writer.toString());

        String response = ((com.travelinsurancemaster.clients.travelinsured.OTAInsuranceQuoteRS)
                getWebServiceTemplate()
                        .marshalSendAndReceive(quote, quoteAction)).getQuoteResult();

        StringReader reader = new StringReader(response);

        final OTAInsuranceQuoteRS result =
                (OTAInsuranceQuoteRS) jaxbQuoteContext.createUnmarshaller().unmarshal(reader);

        return result;
    }

    public Object book(OTAInsuranceBookRQ bookRQ) throws JAXBException {
        StringWriter writer = new StringWriter();
        jaxbBookContext.createMarshaller().marshal(bookRQ, writer);

        Book book = new Book();
        book.setOTAInsuranceBookRQ(writer.toString());

        String response = ((com.travelinsurancemaster.clients.travelinsured.OTAInsuranceBookRS)
                getWebServiceTemplate()
                        .marshalSendAndReceive(book, bookAction)).getBookResult();

        StringReader reader = new StringReader(response);

        Object result = jaxbBookContext.createUnmarshaller().unmarshal(reader);
        return result;
    }

    public PurchaseInsurancePolicyXmlDocumentResponse purchase() {

        PurchaseInsurancePolicyXmlDocument document = new PurchaseInsurancePolicyXmlDocument();
        PurchaseInsurancePolicyXmlDocument.PurchaseInfoXMLDoc xmlDoc = new PurchaseInsurancePolicyXmlDocument.PurchaseInfoXMLDoc();

        ProcessMessageTextResult textResult = new ProcessMessageTextResult();
        document.setPurchaseInfoXMLDoc(xmlDoc);
        List<Object> xmlDocContent = xmlDoc.getContent();
        ProcessMessageTextResult.TINSXMLDATA tinsxmldata = new ProcessMessageTextResult.TINSXMLDATA();
        HeaderType headerType = new HeaderType();

        tinsxmldata.getSegment().add(new ProcessMessageTextResult.TINSXMLDATA.Segment());
        tinsxmldata.getSegment().add(new ProcessMessageTextResult.TINSXMLDATA.Segment());

        tinsxmldata.setHeader(headerType);
        xmlDocContent.add(tinsxmldata);

        xmlDocContent.add(textResult);

        final PurchaseInsurancePolicyXmlDocumentResponse response = (PurchaseInsurancePolicyXmlDocumentResponse) getWebServiceTemplate().marshalSendAndReceive(document,
                new SoapActionCallback(
                        "https://www.travelinsured.com/TIWebService/PurchaseInsurancePolicyXmlDocument"));
        return response;
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
            /*try {
                search(new OTAInsurancePlanSearchRQ());
            } catch (IOException | JAXBException e) {
                e.printStackTrace();
            }*/
        log.debug("Product code: {}, product name: {}", policyMetaCode.getCode(), policyMetaCode.getName());

        OTAInsuranceQuoteRQ quoteRQ = new OTAInsuranceQuoteRQ();
        POSType pos = new POSType();
        SourceType sourceType = new SourceType();
        SourceType.RequestorID requestorID = new SourceType.RequestorID();
        InsCoverageType.CoveredTravelers coveredTravelers = new InsCoverageType.CoveredTravelers();
        CompanyNameType companyName = new CompanyNameType();
        OTAInsuranceQuoteRQ.PlanForQuoteRQ planForQuoteRQ = new OTAInsuranceQuoteRQ.PlanForQuoteRQ();
        InsCoverageDetailType insCoverageDetailType = new InsCoverageDetailType();
        InsCoverageDetailType.CoveredTrips coveredTrips = new InsCoverageDetailType.CoveredTrips();
        InsCoverageDetailType.CoveredTrips.CoveredTrip coveredTrip = new InsCoverageDetailType.CoveredTrips.CoveredTrip();

        quoteRQ.setVersion(BigDecimal.ONE);
        quoteRQ.getPlanForQuoteRQ().add(planForQuoteRQ);

        sourceType.setAgentSine(apiProperties.getTravelInsured().getAgentSine());
        requestorID.setID(apiProperties.getTravelInsured().getUser());
        requestorID.setMessagePassword(apiProperties.getTravelInsured().getPassword());
        // From TravelInsured: "This is a required field in the OTA schema, but TII does not use it.  Fill this with the number 1."
        requestorID.setType(REQUESTOR_ID_TYPE);
        companyName.setCode(apiProperties.getTravelInsured().getCompanyNameCode());
        companyName.setCodeContext(CODE_CONTEXT);

        // From TravelInsured: "This is a required field in the OTA schema, but TII does not use it.  Fill it with a zero (0)"
        planForQuoteRQ.setPlanID(PLAN_ID);
        planForQuoteRQ.setTypeID(policyMetaCode.getCode());

        // From TravelInsured: "This is a required element in the OTA schema, but TII does not use it.  Enter one of the possible values: “SingleTrip”, “MultiTrip” and “Other”. "
        insCoverageDetailType.setType(COVERAGE_DETAIL_TYPE);
        setUpsaleParameters(insCoverageDetailType, request, policyMeta);
        coveredTrip.setStart(dateFormat.format(request.getDepartDate()));
        coveredTrip.setEnd(dateFormat.format(request.getReturnDate()));

        planForQuoteRQ.setInsCoverageDetail(insCoverageDetailType);

        insCoverageDetailType.setCoveredTrips(coveredTrips);

        coveredTrips.getCoveredTrip().add(coveredTrip);

        int i = 0;
        for (GenericTraveler genericTraveler : request.getTravelers()) {
            IndCoverageReqsType indCoveredType = new IndCoverageReqsType();
            IndCoverageReqsType.IndTripCost indTripCost = new IndCoverageReqsType.IndTripCost();

            if(request.getPlanType().getId() == PlanType.LIMITED.getId()) {
                if(policyMeta != null && policyMeta.hasMinimalTripCost())
                    indTripCost.setAmount(policyMeta.getMinimalTripCost());
                else
                    indTripCost.setAmount(BigDecimal.valueOf(0));
            }
            else
                indTripCost.setAmount(genericTraveler.getTripCost());

            indCoveredType.setIndTripCost(indTripCost);

            CoveredTravelerType coveredTravelerType = new CoveredTravelerType();

            CoveredTravelerType.CitizenCountryName citizenCountryName = new CoveredTravelerType.CitizenCountryName();
            citizenCountryName.setCode(request.getCitizenCountry() != null ? request.getCitizenCountry().name() : null);

            coveredTravelerType.setCitizenCountryName(citizenCountryName);

            CoveredTravelerType.CoveredPerson coveredPerson = new CoveredTravelerType.CoveredPerson();
            coveredPerson.setRelation(genericTraveler.isPrimary() ? PRIMARY_TRAVELER : TRAVELER);
            coveredPerson.getGivenName().add(" "); // name is not used in our quote
            coveredPerson.setSurname(" "); // surname is not used in our quote


            coveredPerson.setBirthDate(DateUtil.getXMLGregorianCalendar(DateUtil.fromLocalDate(genericTraveler.getBirthdaySafe()), true));

            coveredTravelerType.setCoveredPerson(coveredPerson);
            coveredTravelerType.setIndCoverageReqs(indCoveredType);


            coveredTravelers.getCoveredTraveler().add(coveredTravelerType);
        }

        planForQuoteRQ.setCoveredTravelers(coveredTravelers);

        quoteRQ.setPOS(pos);
        pos.getSource().add(sourceType);
        sourceType.setRequestorID(requestorID);
        requestorID.setCompanyName(companyName);

        QuoteResult quoteResult = new QuoteResult();
        try {

            StringWriter writer = new StringWriter();

            jaxbQuoteContext.createMarshaller().marshal(quoteRQ, writer);

            Quote quote = new Quote();
            quote.setOTAInsuranceQuoteRQ(writer.toString());

            String response = ((com.travelinsurancemaster.clients.travelinsured.OTAInsuranceQuoteRS)
                    getWebServiceTemplate()
                            .marshalSendAndReceive(quote, quoteAction)).getQuoteResult();

            StringReader reader = new StringReader(response);

            Object result = jaxbQuoteContext.createUnmarshaller().unmarshal(reader);

            if (result instanceof OTAErrorRS) {
                OTAErrorRS errorRS = (OTAErrorRS) result;
                quoteResult.error(errorRS.getErrorCode(), errorRS.getErrorMessage());
                quoteResult.setStatus(Result.Status.ERROR);
            } else {
                OTAInsuranceQuoteRS quoteRS =
                        (OTAInsuranceQuoteRS) result;
                if (quoteRS.getErrors() != null) {
                    quoteResult.setStatus(Result.Status.ERROR);
                    List<ErrorType> errors = quoteRS.getErrors().getError();
                    for (ErrorType error : errors) {
                        quoteResult.getErrors().add(new Result.Error(error.getCode(), error.getValue()));
                    }
                } else {
                    quoteResult.setStatus(Result.Status.SUCCESS);
                    OTAInsuranceQuoteRS.PlanForQuoteRS travelInsuredPlan = quoteRS.getPlanForQuoteRS().get(0);
                    Product product = new Product(policyMeta, policyMetaCode, travelInsuredPlan.getPlanCost().getAmount(),
                            request.getUpsaleValues(), travelInsuredPlan.getQuoteDetail().getQuoteDetailURL().getValue());
                    quoteResult.products.add(product);
                }
            }

        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
            quoteResult.error("-1", e.getMessage());
            quoteResult.setStatus(Result.Status.ERROR);
        }

        return quoteResult;
    }

    private void setUpsaleParameters(InsCoverageDetailType insCoverageDetailType, QuoteRequest quoteRequest, PolicyMeta policyMeta) {
        RequestedCoveragesType coverageRequirements = new RequestedCoveragesType();
        // primary coverage
        CoverageLimitType primaryCoverage = new CoverageLimitType();
        primaryCoverage.setCoverageLevel(COVERAGE_LEVEL_PRIMARY);
        primaryCoverage.setCoverageType("1");
        coverageRequirements.getCoverageRequirement().add(primaryCoverage);
        // flight only ad&d
        String flightAdAndDValue = quoteRequest.getUpsaleValue(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "");
        if (!flightAdAndDValue.isEmpty()) {
            String flightAdAndDApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMeta.getId(), CategoryCodes.FLIGHT_ONLY_AD_AND_D, flightAdAndDValue, quoteRequest);

            if(flightAdAndDValue.equalsIgnoreCase("10000"))
                flightAdAndDValue = "100000";

            CoverageLimitType coverageRequirement = new CoverageLimitType();
            coverageRequirement.setCoverageLevel(COVERAGE_LEVEL_OPTIONAL);
            coverageRequirement.setCoverageType(flightAdAndDApiValue);
            CoverageLimitType.PolicyLimit policyLimit = new CoverageLimitType.PolicyLimit();
            policyLimit.setAmount(new BigDecimal(flightAdAndDValue));
            coverageRequirement.setPolicyLimit(policyLimit);
            coverageRequirements.getCoverageRequirement().add(coverageRequirement);
        }
        String rentalCarValue = quoteRequest.getUpsaleValue(CategoryCodes.RENTAL_CAR, "");
        if (!rentalCarValue.isEmpty()) {
            String rentalCarApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMeta.getId(), CategoryCodes.RENTAL_CAR, rentalCarValue, quoteRequest);
            CoverageLimitType rentalCarCoverage = new CoverageLimitType();
            rentalCarCoverage.setCoverageLevel(COVERAGE_LEVEL_OPTIONAL);
            rentalCarCoverage.setCoverageType(rentalCarApiValue);
            // policy limit
            CoverageLimitType.PolicyLimit policyLimit = new CoverageLimitType.PolicyLimit();
            policyLimit.setAmount(new BigDecimal(rentalCarValue));
            rentalCarCoverage.setPolicyLimit(policyLimit);

            CoverageLimitType.CoverageDate coverageDate = new CoverageLimitType.CoverageDate();
            coverageDate.setStart(DateUtil.getXMLGregorianCalendar(DateUtil.fromLocalDate(quoteRequest.getRentalCarStartDate())));
            coverageDate.setEnd(DateUtil.getXMLGregorianCalendar(DateUtil.fromLocalDate(quoteRequest.getRentalCarEndDate())));
            rentalCarCoverage.setCoverageDate(coverageDate);

            coverageRequirements.getCoverageRequirement().add(rentalCarCoverage);
        }
        String cancelForWorkReasonValue = quoteRequest.getUpsaleValue(CategoryCodes.CANCEL_FOR_WORK_REASONS, "");
        if (!cancelForWorkReasonValue.isEmpty() && !cancelForWorkReasonValue.equals("NOT COVERED")) {
            String cancelForWorkReasonApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMeta.getId(), CategoryCodes.CANCEL_FOR_WORK_REASONS, cancelForWorkReasonValue, quoteRequest);

            if(cancelForWorkReasonApiValue.equals("") || cancelForWorkReasonApiValue.equalsIgnoreCase("Covered"))
                cancelForWorkReasonApiValue = "12";

            CoverageLimitType coverageRequirement = new CoverageLimitType();
            coverageRequirement.setCoverageLevel(COVERAGE_LEVEL_OPTIONAL);
            coverageRequirement.setCoverageType(cancelForWorkReasonApiValue);
            coverageRequirements.getCoverageRequirement().add(coverageRequirement);
        }
        String primaryMedical = quoteRequest.getCategoryValue(CategoryCodes.PRIMARY_MEDICAL);
        if (isNotBlank(primaryMedical) && primaryMedical.equalsIgnoreCase("yes")) {
            CoverageLimitType primaryMedicalCoverage = new CoverageLimitType();
            primaryMedicalCoverage.setCoverageLevel(COVERAGE_LEVEL_OPTIONAL);
            primaryMedicalCoverage.setCoverageType(PRIMARY_COVERAGE);
            coverageRequirements.getCoverageRequirement().add(primaryMedicalCoverage);
        }

        String medicalEvac = quoteRequest.getUpsaleValue(CategoryCodes.MEDICAL_EVACUATION, "");
        String emergencyMedical = quoteRequest.getUpsaleValue(CategoryCodes.EMERGENCY_MEDICAL, "");
        if ((!medicalEvac.isEmpty() || !emergencyMedical.isEmpty()) && !medicalEvac.equals("100000") && !emergencyMedical.equals("10000")) {
            CoverageLimitType primaryMedicalCoverage = new CoverageLimitType();
            primaryMedicalCoverage.setCoverageLevel(COVERAGE_LEVEL_OPTIONAL);
            primaryMedicalCoverage.setCoverageType(PRIMARY_COVERAGE);
            coverageRequirements.getCoverageRequirement().add(primaryMedicalCoverage);
        }

        String travelDelayValue = quoteRequest.getUpsaleValue(CategoryCodes.TRAVEL_DELAY);
        String missedConnectionValue = quoteRequest.getUpsaleValue(CategoryCodes.MISSED_CONNECTION);
        String baggageDelayValue = quoteRequest.getUpsaleValue(CategoryCodes.BAGGAGE_DELAY);
        String baggageLossValue = quoteRequest.getUpsaleValue(CategoryCodes.BAGGAGE_LOSS);
        if (!(policyMetaCategoryValueService.isDefaultValue(policyMetaCategoryValueService.getSortedCategoryValues(
                policyMeta.getId(), CategoryCodes.TRAVEL_DELAY, quoteRequest), travelDelayValue) &&
                policyMetaCategoryValueService.isDefaultValue(policyMetaCategoryValueService.getSortedCategoryValues(
                        policyMeta.getId(), CategoryCodes.MISSED_CONNECTION, quoteRequest), missedConnectionValue) &&
                policyMetaCategoryValueService.isDefaultValue(policyMetaCategoryValueService.getSortedCategoryValues(
                        policyMeta.getId(), CategoryCodes.BAGGAGE_DELAY, quoteRequest), baggageDelayValue) &&
                policyMetaCategoryValueService.isDefaultValue(policyMetaCategoryValueService.getSortedCategoryValues(
                        policyMeta.getId(), CategoryCodes.BAGGAGE_LOSS, quoteRequest), baggageLossValue))) {
            CoverageLimitType travelBenefitsUpgrade = new CoverageLimitType();
            travelBenefitsUpgrade.setCoverageLevel(COVERAGE_LEVEL_OPTIONAL);
            travelBenefitsUpgrade.setCoverageType(BENEFITS_UPGRADE_COVERAGE);
            coverageRequirements.getCoverageRequirement().add(travelBenefitsUpgrade);
        }
        insCoverageDetailType.setCoverageRequirements(coverageRequirements);
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {

        OTAInsuranceBookRQ bookRQ = new OTAInsuranceBookRQ();
        POSType posType = new POSType();
        SourceType sourceType = new SourceType();
        SourceType.RequestorID requestorID = new SourceType.RequestorID();
        CompanyNameType companyName = new CompanyNameType();
        OTAInsuranceBookRQ.PlanForBookRQ planForBookRQ = new OTAInsuranceBookRQ.PlanForBookRQ();
        InsCoverageType.CoveredTravelers coveredTravelers = new InsCoverageType.CoveredTravelers();

        StateProvType stateProvType = new StateProvType();
        CountryNameType countryName = new CountryNameType();
        CoveredTravelerType.Telephone telephone = new CoveredTravelerType.Telephone();
        CoveredTravelerType.CitizenCountryName citizenCountryName = new CoveredTravelerType.CitizenCountryName();
        InsCoverageDetailType insCoverageDetail = new InsCoverageDetailType();
        XMLGregorianCalendar effectiveDate = DateUtil.getXMLGregorianCalendar(DateUtil.fromLocalDate(request.getQuoteRequest().getDepartDate()));

        InsCoverageDetailType.CoveredTrips coveredTrips = new InsCoverageDetailType.CoveredTrips();
        InsCoverageDetailType.CoveredTrips.CoveredTrip coveredTrip = new InsCoverageDetailType.CoveredTrips.CoveredTrip();
        InsuranceCustomerType insuranceCustomer = new InsuranceCustomerType();
        TripFeaturesType.Destinations destinations = new TripFeaturesType.Destinations();
        TripFeaturesType.Destinations.Destination destination = new TripFeaturesType.Destinations.Destination();
        PaymentFormType paymentForm = new PaymentFormType();
        PaymentCardType paymentCard = new PaymentCardType();
        PlanCostType planCost = new PlanCostType();
        PlanCostType.BasePremium basePremium = new PlanCostType.BasePremium();

        bookRQ.setVersion(BigDecimal.ONE);
        bookRQ.setPOS(posType);

        posType.getSource().add(sourceType);

        sourceType.setAgentSine(apiProperties.getTravelInsured().getAgentSine());
        sourceType.setRequestorID(requestorID);

        requestorID.setID(apiProperties.getTravelInsured().getUser());
        requestorID.setMessagePassword(apiProperties.getTravelInsured().getPassword());

        // From TravelInsured: "This is a required field in the OTA schema, but TII does not use it.  Fill this with the number 1."
        requestorID.setType(REQUESTOR_ID_TYPE);
        requestorID.setCompanyName(companyName);

        companyName.setCode(apiProperties.getTravelInsured().getCompanyNameCode());
        companyName.setCodeContext(CODE_CONTEXT);

        bookRQ.getPlanForBookRQ().add(planForBookRQ);
        // From TravelInsured: "This is a required field in the OTA schema, but TII does not use it.  Fill it with a zero (0)"
        planForBookRQ.setPlanID(PLAN_ID);
        planForBookRQ.setTypeID(request.getProduct().getPolicyMetaCode().getCode());

        planForBookRQ.setCoveredTravelers(coveredTravelers);

        for (int i = 0; i < request.getTravelers().size(); i++) {

            GenericTraveler genericTraveler = request.getTravelers().get(i);

            IndCoverageReqsType indCoverageReqs = new IndCoverageReqsType();
            IndCoverageReqsType.IndTripCost indTripCost = new IndCoverageReqsType.IndTripCost();
            indTripCost.setAmount(genericTraveler.getTripCost());

            indCoverageReqs.setIndTripCost(indTripCost);

            CoveredTravelerType travelerType = new CoveredTravelerType();
            CoveredTravelerType.CoveredPerson coveredPerson = new CoveredTravelerType.CoveredPerson();
            AddressInfoType addressInfoType = new AddressInfoType();

            addressInfoType.setType(ADDRESS_INFO_TYPE); // type 1 (Home) will be put in TII's primary address.
            addressInfoType.getAddressLine().add(request.getAddress());
            addressInfoType.setCityName(request.getCity());
            addressInfoType.setPostalCode(request.getPostalCode());

            addressInfoType.setCountryName(countryName);
            addressInfoType.setStateProv(stateProvType);

            coveredTravelers.getCoveredTraveler().add(travelerType);

            travelerType.setIndCoverageReqs(indCoverageReqs);
            if (request.getQuoteRequest().getCitizenCountry() != null) {
                citizenCountryName.setCode(request.getQuoteRequest().getCitizenCountry().name());
            }
            travelerType.setCitizenCountryName(citizenCountryName);
            travelerType.getTelephone().add(telephone);
            travelerType.getAddress().add(addressInfoType);

            if (isNotBlank(genericTraveler.getBeneficiary()) && genericTraveler.getBeneficiary().trim().indexOf(' ') != -1) {
                CoveredTravelerType.Beneficiary beneficiary = new CoveredTravelerType.Beneficiary();
                CoveredTravelerType.Beneficiary.Name name = new CoveredTravelerType.Beneficiary.Name();
                beneficiary.setBenefitPercent(BigDecimal.valueOf(100));
                beneficiary.setName(name);
                String[] beneficiaryNames = TextUtils.getNamesFromFullName(genericTraveler.getBeneficiary());
                name.getGivenName().add(beneficiaryNames[0]);
                name.setSurname(beneficiaryNames[1]);
                travelerType.getBeneficiary().add(beneficiary);
            }

            travelerType.setCoveredPerson(coveredPerson);
            coveredPerson.setBirthDate(DateUtil.getXMLGregorianCalendar(DateUtil.fromLocalDate(genericTraveler.getBirthdaySafe()), true));
            if (genericTraveler.isPrimary()) {
                coveredPerson.setRelation(PRIMARY_TRAVELER);
            } else {
                coveredPerson.setRelation(TRAVELER);
            }
            coveredPerson.getGivenName().add(genericTraveler.getFirstName());
            coveredPerson.setSurname(genericTraveler.getLastName());
            EmailType email = new EmailType();
            email.setValue(request.getEmail());
            email.setEmailType(PERSONAL_EMAIL_TYPE);
            travelerType.getEmail().add(email);
        }

        stateProvType.setStateCode(request.getQuoteRequest().getResidentState() != null ? request.getQuoteRequest().getResidentState().name() : null);
        countryName.setCode(request.getQuoteRequest().getResidentCountry() != null ? request.getQuoteRequest().getResidentCountry().name() : null);
        telephone.setPhoneNumber(request.getPhone());
        telephone.setPhoneUseType(PHONE_USE_TYPE); // Type 4 (Evening Contact) maps to primary

        // From Travel Insured: "This is a required element in the OTA schema, but TII does not use it. Enter one of the possible values: “SingleTrip”, “MultiTrip” and “Other”".
        insCoverageDetail.setType(COVERAGE_DETAIL_TYPE);
        insCoverageDetail.setEffectiveDate(effectiveDate);

        setUpsaleParameters(insCoverageDetail, request.getQuoteRequest(), request.getProduct().getPolicyMeta());
        coveredTrip.setStart(dateFormat.format(request.getQuoteRequest().getDepartDate()));
        coveredTrip.setEnd(dateFormat.format(request.getQuoteRequest().getReturnDate()));
        LocalDate depositDate = request.getQuoteRequest().getDepositDate();
        if (depositDate != null) {
            try {
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                gregorianCalendar.setTime(DateUtil.fromLocalDate(request.getQuoteRequest().getDepositDate()));
                coveredTrip.setDepositDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));
            } catch (DatatypeConfigurationException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            coveredTrip.setDepositDate(null);
        }
        CountryNameType countryNameType = new CountryNameType();
        countryNameType.setValue(request.getQuoteRequest().getDestinationCountry().getCaption());
        destination.setCountryName(countryNameType);

        String cardType;
        switch (request.getCreditCard().getCcType()) {
            case VISA:
                cardType = "VI";
                break;
            case Discover:
                cardType = "DS";
                break;
            case MasterCard:
                cardType = "MC";
                break;
            case AmericanExpress:
                cardType = "AX";
                break;
            default:
                cardType = "";
        }
        paymentCard.setCardCode(cardType);
        paymentCard.setExpireDate(request.getCreditCard().getCcExpMonth() + request.getCreditCard().getCcExpYear().substring(2));

        paymentCard.setCardNumber(request.getCreditCard().getCcNumber().toString());
        paymentCard.setSeriesCode(request.getCreditCard().getCcCode());

        paymentCard.setCardHolderName(request.getCreditCard().getCcName());

        planCost.setAmount(BigDecimal.valueOf(request.getProduct().getTotalPrice().doubleValue()));
        basePremium.setAmount(BigDecimal.valueOf(request.getProduct().getTotalPrice().doubleValue()));
        planCost.setBasePremium(basePremium);

        planForBookRQ.setPlanCost(planCost);

        paymentForm.setPaymentCard(paymentCard);

        insuranceCustomer.getPaymentForm().add(paymentForm);
        planForBookRQ.setInsuranceCustomer(insuranceCustomer);
        destinations.getDestination().add(destination);

        coveredTrip.setDestinations(destinations);
        coveredTrips.getCoveredTrip().add(coveredTrip);
        insCoverageDetail.setCoveredTrips(coveredTrips);

        planForBookRQ.setInsCoverageDetail(insCoverageDetail);
        citizenCountryName.setCode(request.getQuoteRequest().getCitizenCountry() != null ? request.getQuoteRequest().getCitizenCountry().name() : null);

        PurchaseResponse purchaseResponse = new PurchaseResponse();

        Object bookRS;
        try {
            bookRS = book(bookRQ);
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
            return purchaseResponse;
        }
        if (bookRS instanceof OTAInsuranceBookRS) {
            processBookResponse(bookRS, purchaseResponse);
        } else if (bookRS instanceof OTAErrorRS) {
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(new Result.Error(((OTAErrorRS) bookRS).getErrorCode(), ((OTAErrorRS) bookRS).getErrorMessage()));
        }
        return purchaseResponse;
    }

    private void processBookResponse(Object bookRS, PurchaseResponse purchaseResponse) {
        boolean errorsFound = false;
        if (((OTAInsuranceBookRS) bookRS).getErrors() != null && !((OTAInsuranceBookRS) bookRS).getErrors().getError().isEmpty()) {
            List<ErrorType> errors = ((OTAInsuranceBookRS) bookRS).getErrors().getError();
            purchaseResponse.setStatus(Result.Status.ERROR);
            for (ErrorType error : errors) {
                purchaseResponse.getErrors().add(new Result.Error(error.getCode(), error.getValue()));
            }
            errorsFound = true;
        }
        if (((OTAInsuranceBookRS) bookRS).getWarnings() != null && !((OTAInsuranceBookRS) bookRS).getWarnings().getWarning().isEmpty()) {
            List<WarningType> warnings = ((OTAInsuranceBookRS) bookRS).getWarnings().getWarning();
            purchaseResponse.setStatus(Result.Status.ERROR);
            outerloop:
            for (WarningType warning : warnings) {
                for (Result.Error error : purchaseResponse.getErrors()) {
                    if (error.getErrorCode().equals(warning.getCode())) {
                        break outerloop;
                    }
                }
                purchaseResponse.getErrors().add(new Result.Error(warning.getCode(), warning.getValue()));
            }
            errorsFound = true;
        }
        if (!errorsFound) {
            purchaseResponse.setStatus(Result.Status.SUCCESS);
            purchaseResponse.setPolicyNumber(((OTAInsuranceBookRS) bookRS).getPlanForBookRS().get(0).getPolicyDetail().getPolicyNumber().getID());
        }
    }

    /*public OTAInsurancePlanSearchRS search(OTAInsurancePlanSearchRQ searchRQ) throws IOException, JAXBException {
        // Example 1
        StringWriter writer = new StringWriter();
        POSType posType = new POSType();
        SourceType sourceType = new SourceType();
        SourceType.RequestorID requestorID = new SourceType.RequestorID();
        CompanyNameType companyName = new CompanyNameType();
        companyName.setCode(apiProperties.getTravelInsured().getCompanyNameCode());
        requestorID.setCompanyName(companyName);
        requestorID.setID(apiProperties.getTravelInsured().getUser());
        requestorID.setMessagePassword(apiProperties.getTravelInsured().getPassword());
        requestorID.setType("1");
        sourceType.setRequestorID(requestorID);
        sourceType.setAgentSine(apiProperties.getTravelInsured().getAgentSine());
        posType.getSource().add(sourceType);
        searchRQ.setPOS(posType);
        searchRQ.setVersion(new BigDecimal("1.006"));

        jaxbQuoteContext.createMarshaller().marshal(searchRQ, writer);

        Search search = new Search();
        search.setOTAInsurancePlanSearchRQ(writer.toString());

        String response = ((com.travelinsurancemaster.clients.travelinsured.OTAInsurancePlanSearchRS)
                getWebServiceTemplate()
                        .marshalSendAndReceive(search, searchAction)).getSearchResult();
        StringReader reader = new StringReader(response);

        final OTAInsurancePlanSearchRS result =
                (OTAInsurancePlanSearchRS) jaxbQuoteContext.createUnmarshaller().unmarshal(reader);

        return result;
    }*/

    @Override
    public String getVendorCode() {
        return ApiVendor.TravelInsured;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        return Collections.emptyList();
    }
}
