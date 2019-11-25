package com.travelinsurancemaster.services.clients;

import com.travelinsurancemaster.clients.csa.xsd.Error;
import com.travelinsurancemaster.clients.csa.xsd.*;
import com.travelinsurancemaster.model.*;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.services.AbstractRestClient;
import com.travelinsurancemaster.services.csa.CsaCountryArea;
import com.travelinsurancemaster.util.CountryCodes;
import com.travelinsurancemaster.util.TextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Vlad on 04.02.2015.
 */
public class CSAClient extends AbstractRestClient {

    private static final Logger log = LoggerFactory.getLogger(CSAClient.class);

    private static final String XML_REQ_STR = "xmlrequeststring";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String CUSTOM_UNIQUE_CODE = "CSAC";
    private static final String CANANYREASON = "cananyreason";
    private static final String AIR_ONLY = "Air Only";
    private static final String CRUISE = "Cruise";
    private static final String OTHER = "Other";

    private Jaxb2Marshaller marshaller;
    private HttpHeaders requestHeaders;

    public CSAClient(RestTemplate restTemplate, Jaxb2Marshaller marshaller) {
        super(restTemplate);
        this.marshaller = marshaller;
        requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    public Response quote(Quoterequest request) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        StringResult result = new StringResult();
        marshaller.marshal(request, result);
        params.add(XML_REQ_STR, result.toString());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(apiProperties.getCsa().getUrl(), HttpMethod.POST, requestEntity, String.class);

        Response unmarshalledResponse = (Response) marshaller.unmarshal(new StringSource(response.getBody()));
        return unmarshalledResponse;
    }

    public Purchaseresponse book(Purchaserequest request) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        StringResult result = new StringResult();
        marshaller.marshal(request, result);
        params.add(XML_REQ_STR, result.toString());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(apiProperties.getCsa().getUrl(), HttpMethod.POST, requestEntity, String.class);

        Response unmarshalledResponse = (Response) marshaller.unmarshal(new StringSource(response.getBody()));
        return unmarshalledResponse.getPurchaseresponse();
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        String policyCode = policyMetaCode.getCode();
        log.debug("Product code: {}, product name: {}", policyCode, policyMetaCode.getName());

        Quoterequest clientQuoteRQ = new Quoterequest();
        QuoteResult result = new QuoteResult();

        clientQuoteRQ.setAff(apiProperties.getCsa().getUser());
        clientQuoteRQ.setProducer(apiProperties.getCsa().getUser());
        clientQuoteRQ.setTripcost(request.getTripCost());
        if (request.getDestinationCountry() == CountryCode.US && request.getResidentState() == StateCode.AK) {
            clientQuoteRQ.setDestination(StateCode.AK.getCaption());
        } else if (request.getDestinationCountry() == CountryCode.US && request.getResidentState() == StateCode.HI) {
            clientQuoteRQ.setDestination(StateCode.HI.getCaption());
        } else {
            clientQuoteRQ.setDestination(CountryCodes.getCountryByCode(request.getDestinationCountry() != null ? request.getDestinationCountry().name() : null)); //todo: just for test!
        }
        Travelers travelers = new Travelers();
        clientQuoteRQ.setTravelers(travelers);

        for (GenericTraveler genericTraveler : request.getTravelers()) {
            Travelers.Traveler traveler = new Travelers.Traveler();
            travelers.getTraveler().add(traveler);
            traveler.setAge(genericTraveler.getAge());

            if(request.getPlanType().getId() == PlanType.LIMITED.getId())
                traveler.setTripcost(BigDecimal.valueOf(0));
            else
                traveler.setTripcost(genericTraveler.getTripCost());
        }
        clientQuoteRQ.setTriptype("CRUISE"); //TODO
        clientQuoteRQ.setProductclass(policyCode);
        clientQuoteRQ.setNuminsured(request.getTravelers().size());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        clientQuoteRQ.setDepartdate(dateFormat.format(request.getDepartDate()));
        clientQuoteRQ.setReturndate(dateFormat.format(request.getReturnDate()));
        clientQuoteRQ.setInitdate(request.getDepositDate() != null ? dateFormat.format(request.getDepositDate()) : "");

        setQuoteUpsaleParameters(clientQuoteRQ, request);

        Response clientResponse = quote(clientQuoteRQ);
        if (clientResponse.getErrorresponse() != null) {
            Error error = clientResponse.getErrorresponse().getError();
            result.setStatus(Result.Status.ERROR);
            result.getErrors().add(new Result.Error(error.getErrorid(), error.getMessage()));
        }
        if (clientResponse.getQuoteresponse() != null) {
            if (clientResponse.getQuoteresponse().getError() == null) {
                result.setStatus(Result.Status.SUCCESS);
                Product product = new Product(policyMeta, policyMetaCode, clientResponse.getQuoteresponse().getPrice(), request.getUpsaleValues());
                result.products.add(product);
            } else {
                Error error = clientResponse.getQuoteresponse().getError();
                result.setStatus(Result.Status.ERROR);
                result.getErrors().add(new Result.Error(error.getErrorid(), error.getMessage()));
            }
        }
        return result;
    }

    private String getCountryArea(CountryCode destinationCountry) {
        return CsaCountryArea.getAreaByCode(destinationCountry != null ? destinationCountry.name() : null);
    }

    private void setQuoteUpsaleParameters(Quoterequest clientQuoteRQ, QuoteRequest request) {
        String cancelForAnyReason = request.getUpsaleValue(CategoryCodes.CANCEL_FOR_ANY_REASON);
        if (StringUtils.isNotEmpty(cancelForAnyReason)) {
            Options options = new Options();
            Options.Option option = new Options.Option();
            option.setType(CANANYREASON);
            options.setOption(option);
            clientQuoteRQ.setOptions(options);
        }
        String rentalCar = request.getUpsaleValue(CategoryCodes.RENTAL_CAR);
        if (StringUtils.isNotEmpty(rentalCar)) {
            clientQuoteRQ.setCdw(1);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
            clientQuoteRQ.setCdwstartdate(dateFormat.format(request.getRentalCarStartDateSafe()));
            clientQuoteRQ.setCdwenddate(dateFormat.format(request.getRentalCarEndDateSafe()));
        }
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {

        Purchaserequest purchaseRequest = new Purchaserequest();
        purchaseRequest.setActioncode(Actioncode.NEW);
        purchaseRequest.setAff(apiProperties.getCsa().getUser());
        purchaseRequest.setProducer(apiProperties.getCsa().getUser());
        purchaseRequest.setProductclass(request.getProduct().getPolicyMetaCode().getCode());
        purchaseRequest.setBookingreservno("123456789AB");// todo
        purchaseRequest.setNuminsured(request.getTravelers().size());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        purchaseRequest.setDepartdate(dateFormat.format(request.getQuoteRequest().getDepartDate()));
        purchaseRequest.setReturndate(dateFormat.format(request.getQuoteRequest().getReturnDate()));
        purchaseRequest.setInitdate(request.getQuoteRequest().getDepositDate() != null ? dateFormat.format(request.getQuoteRequest().getDepositDate()) : "");
        String tripType = getTripType(request.getTripTypes());
        purchaseRequest.setTriptype(tripType);
        purchaseRequest.setDestination(getCountryArea(request.getQuoteRequest().getDestinationCountry()));
        purchaseRequest.setSupplier("Carnival Cruise Lines");// todo
        Travelers travelers = new Travelers();
        purchaseRequest.setTravelers(travelers);

        for (GenericTraveler genericTraveler : request.getTravelers()) {
            Travelers.Traveler traveler = new Travelers.Traveler();
            travelers.getTraveler().add(traveler);

            traveler.setTravelerfirstname(genericTraveler.getFirstName());
            traveler.setTravelerlastname(genericTraveler.getLastName());
            traveler.setAge(genericTraveler.getAge());
            traveler.setTripcost(genericTraveler.getTripCost());
        }

        purchaseRequest.setTripcost(request.getQuoteRequest().getTripCost());
        purchaseRequest.setAddress1(request.getAddress());
        purchaseRequest.setCity(request.getCity());
        purchaseRequest.setState(request.getQuoteRequest().getResidentState() != null ? request.getQuoteRequest().getResidentState().name() : null);
        if (StringUtils.isNotEmpty(request.getPostalCode())) {
            purchaseRequest.setZipcode(request.getPostalCode());
        }
        if (StringUtils.isNotEmpty(request.getPhone())) {
            purchaseRequest.setTelephonehome(BigDecimal.valueOf(Double.valueOf(request.getPhone())));
        }
        purchaseRequest.setPrintpolconfltr(3);
        purchaseRequest.setEmailaddress(request.getEmail());
        purchaseRequest.setAgentid(apiProperties.getCsa().getAgentId());
        purchaseRequest.setAgentemail(apiProperties.getCsa().getAgentEmail());
        GenericTraveler traveler = request.getTravelers().get(0);
        String[] beneficiaryNames = TextUtils.getNamesFromFullName(traveler.getBeneficiary());
        purchaseRequest.setBeneficiaryfirstname(beneficiaryNames[0]);
        purchaseRequest.setBeneficiarylastname(beneficiaryNames[1]);
        purchaseRequest.setBeneficiaryrelationship(traveler.getBeneficiaryRelation().name());
        purchaseRequest.setPrice(request.getProduct().getTotalPrice());
        setPurchaseUpsaleParameters(purchaseRequest, request.getQuoteRequest());
        String type;
        switch (request.getCreditCard().getCcType()) {
            case VISA:
                type = "Vi";
                break;
            case MasterCard:
                type = "Ma";
                break;
            case AmericanExpress:
                type = "AM";
                break;
            case Diners:
                type = "DC";
                break;
            case Discover:
                type = "Di";
                break;
            default:
                type = "";
        }
        purchaseRequest.setPaymentmethod(type);
        purchaseRequest.setCcorcheckno(BigDecimal.valueOf(request.getCreditCard().getCcNumber()));
        String expiration = request.getCreditCard().getCcExpMonth() + ("" + request.getCreditCard().getCcExpYear()).substring(2);
        purchaseRequest.setCcexpiration(expiration);
        purchaseRequest.setCcname(request.getCreditCard().getCcName());
        purchaseRequest.setCczipcode(request.getCreditCard().getCcZipCode());

        PurchaseResponse response = new PurchaseResponse();

        Purchaseresponse purchaseResponse = book(purchaseRequest);
        if (purchaseResponse.getError() == null) {
            response.setStatus(Result.Status.SUCCESS);
            response.setPolicyNumber(purchaseResponse.getPolicynumber());
        } else {
            response.setStatus(Result.Status.ERROR);
            response.getErrors().add(
                    new Result.Error(purchaseResponse.getError().getErrorid(), purchaseResponse.getError().getMessage()));
        }
        return response;
    }

    private String getTripType(Set<String> tripTypes) {
        if (tripTypes.size() == 1) {
            if (tripTypes.contains(TripType.Air.name())) {
                return AIR_ONLY;
            }
            if (tripTypes.contains(TripType.Cruise.name())) {
                return CRUISE;
            }
        }
        return OTHER;
    }

    private void setPurchaseUpsaleParameters(Purchaserequest purchaseRequest, QuoteRequest request) {
        String cancelForAnyReason = request.getUpsaleValue(CategoryCodes.CANCEL_FOR_ANY_REASON);
        if (StringUtils.isNotEmpty(cancelForAnyReason)) {
            Options options = new Options();
            Options.Option option = new Options.Option();
            option.setType(CANANYREASON);
            options.setOption(option);
            purchaseRequest.setOptions(options);
        }
        String rentalCar = request.getUpsaleValue(CategoryCodes.RENTAL_CAR);
        if (StringUtils.isNotEmpty(rentalCar)) {
            purchaseRequest.setCdw(1);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
            purchaseRequest.setCdwstartdate(dateFormat.format(request.getRentalCarStartDateSafe()));
            purchaseRequest.setCdwenddate(dateFormat.format(request.getRentalCarEndDateSafe()));
        }
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.CSA;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        return Collections.emptyList();
    }
}