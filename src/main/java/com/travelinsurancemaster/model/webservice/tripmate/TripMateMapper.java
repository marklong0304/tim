package com.travelinsurancemaster.model.webservice.tripmate;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import com.travelinsurancemaster.services.PolicyMetaCategoryValueService;
import com.travelinsurancemaster.services.clients.MHRossClient;
import com.travelinsurancemaster.services.clients.TripMateClient;
import com.travelinsurancemaster.services.tripmate.DestinationCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ritchie on 8/6/15.
 */
@Component
public class TripMateMapper {

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;

    private Map<String,String> optionalPackages;

    private void getOptionalPackages(){
        optionalPackages = new HashMap<>();
        optionalPackages.put(MHRossClient.ASSET_PRODUCT_CODE+CategoryCodes.FLIGHT_ONLY_AD_AND_D, "option484");
        optionalPackages.put(MHRossClient.ASSET_PLUS_PRODUCT_CODE+CategoryCodes.FLIGHT_ONLY_AD_AND_D, "option488");
        optionalPackages.put(MHRossClient.BRIDGE_PRODUCT_CODE+CategoryCodes.FLIGHT_ONLY_AD_AND_D, "option336");
        optionalPackages.put(MHRossClient.COMPLETE_PRODUCT_CODE+CategoryCodes.FLIGHT_ONLY_AD_AND_D, "option340");
        optionalPackages.put(MHRossClient.ASSET_PRODUCT_CODE+CategoryCodes.RENTAL_CAR, "option483");
        optionalPackages.put(MHRossClient.ASSET_PLUS_PRODUCT_CODE+CategoryCodes.RENTAL_CAR, "option487");
        optionalPackages.put(MHRossClient.BRIDGE_PRODUCT_CODE+CategoryCodes.RENTAL_CAR, "option335");
        optionalPackages.put(MHRossClient.COMPLETE_PRODUCT_CODE+CategoryCodes.RENTAL_CAR, "option339");
        optionalPackages.put(MHRossClient.ASSET_PRODUCT_CODE+CategoryCodes.HAZARDOUS_SPORTS, "option486");
        optionalPackages.put(MHRossClient.ASSET_PLUS_PRODUCT_CODE+CategoryCodes.HAZARDOUS_SPORTS, "option490");
        optionalPackages.put(MHRossClient.BRIDGE_PRODUCT_CODE+CategoryCodes.HAZARDOUS_SPORTS, "option338");
        optionalPackages.put(MHRossClient.COMPLETE_PRODUCT_CODE+CategoryCodes.HAZARDOUS_SPORTS, "option342");
        //optionalPackages.put(MHRossClient.ASSET_PRODUCT_CODE+CategoryCodes.PERSONAL_PROPERTY, "option521");
        //optionalPackages.put(MHRossClient.ASSET_PLUS_PRODUCT_CODE+CategoryCodes.PERSONAL_PROPERTY, "option525");
        //optionalPackages.put(MHRossClient.BRIDGE_PRODUCT_CODE+CategoryCodes.PERSONAL_PROPERTY, "option529");
        //optionalPackages.put(MHRossClient.COMPLETE_PRODUCT_CODE+CategoryCodes.PERSONAL_PROPERTY, "option533");

    }

    public MultiValueMap<String, String> toMap(QuoteRequest request, String policyCode, InsuranceMasterApiProperties apiProperties) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(TripMateRequestConstants.DATE_FORMAT);
        MultiValueMap<String, String> quoteParams = new LinkedMultiValueMap<>();
        switch (policyCode) {
            case MHRossClient.ASSET_PRODUCT_CODE:
            case MHRossClient.ASSET_PLUS_PRODUCT_CODE:
            case MHRossClient.BRIDGE_PRODUCT_CODE:
            case MHRossClient.COMPLETE_PRODUCT_CODE:
                quoteParams.add(TripMateRequestConstants.LOCATION, MHRossClient.RCONSUMER);
                InsuranceMasterApiProperties.MHRoss mhRossProperties = apiProperties.getmHRoss();
                quoteParams.add(TripMateRequestConstants.UNAME, mhRossProperties.getUser());
                quoteParams.add(TripMateRequestConstants.PWD, mhRossProperties.getPassword());
        }
        quoteParams.add(TripMateRequestConstants.TOTAL_TRAVS, String.valueOf(request.getTravelers().size()));
        quoteParams.add(TripMateRequestConstants.DEPARTDATE, dateFormat.format(request.getDepartDate()));
        quoteParams.add(TripMateRequestConstants.RETURNDATE, dateFormat.format(request.getReturnDate()));
        quoteParams.add(TripMateRequestConstants.INDIVIDUAL_DATES, TripMateRequestConstants.INDIVIDUAL_DATES_VALUE); // todo
        int i = 1;
        for (GenericTraveler traveler : request.getTravelers()) {
            String travelerIndex = TripMateRequestConstants.TRAVELER_PREFIX + i + "_";
            quoteParams.add(travelerIndex + TripMateRequestConstants.AGE, String.valueOf(traveler.getAge()));
            quoteParams.add(travelerIndex + TripMateRequestConstants.TRIPCOST, String.valueOf(traveler.getTripCost().toPlainString()));
            i++;
        }
        if (request.getResidentState() != null) {
            quoteParams.add(TripMateRequestConstants.STATE, request.getResidentState().name());
        } else {
            quoteParams.add(TripMateRequestConstants.STATE, "");
        }
        quoteParams.add(TripMateRequestConstants.COUNTRY, request.getResidentCountry().name());
        return quoteParams;
    }

    public MultiValueMap<String, String> toMap(PurchaseRequest purchaseRequest, InsuranceMasterApiProperties apiProperties) {
        getOptionalPackages();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(TripMateRequestConstants.DATE_FORMAT);
        MultiValueMap<String, String> purchaseParams = new LinkedMultiValueMap<>();
        String policyCode = purchaseRequest.getProduct().getPolicyMetaCode().getCode();
        switch (policyCode) {
            case MHRossClient.ASSET_PRODUCT_CODE:
            case MHRossClient.ASSET_PLUS_PRODUCT_CODE:
            case MHRossClient.BRIDGE_PRODUCT_CODE:
            case MHRossClient.COMPLETE_PRODUCT_CODE:
                purchaseParams.add(TripMateRequestConstants.LOCATION, MHRossClient.RCONSUMER);
                purchaseParams.add(TripMateRequestConstants.UNAME, apiProperties.getmHRoss().getUser());
                purchaseParams.add(TripMateRequestConstants.PWD, apiProperties.getmHRoss().getPassword());
                purchaseParams.add(TripMateRequestConstants.COUNTRY, TripMateRequestConstants.COUNTRY_US);
                purchaseParams.add(TripMateRequestConstants.CC_COUNTRY, TripMateRequestConstants.COUNTRY_US);
        }
        purchaseParams.add(TripMateRequestConstants.PRODUCTID, policyCode);
        purchaseParams.add(TripMateRequestConstants.TRAVELER_COUNT, String.valueOf(purchaseRequest.getTravelers().size()));
        purchaseParams.add(TripMateRequestConstants.INDIVIDUAL_DATES, TripMateRequestConstants.INDIVIDUAL_DATES_VALUE); // todo
        int i = 1;
        for (GenericTraveler traveler : purchaseRequest.getTravelers()) {
            if (i == 1) {
                purchaseParams.add(TripMateRequestConstants.BENEFICIARY, traveler.getBeneficiary());
            }
            String travelerIndex = TripMateRequestConstants.TRAVELER_PREFIX + i + "_";
            purchaseParams.add(travelerIndex + TripMateRequestConstants.PREFIX, "");
            purchaseParams.add(travelerIndex + TripMateRequestConstants.FIRSTNAME, traveler.getFirstName());
            purchaseParams.add(travelerIndex + TripMateRequestConstants.LASTNAME, traveler.getLastName());
            purchaseParams.add(travelerIndex + TripMateRequestConstants.MIDDLENAME, traveler.getMiddleInitials());
            purchaseParams.add(travelerIndex + TripMateRequestConstants.SUFFIX, "");
            purchaseParams.add(travelerIndex + TripMateRequestConstants.AGE, String.valueOf(traveler.getAge()));
            purchaseParams.add(travelerIndex + TripMateRequestConstants.DOB, dateFormat.format(traveler.getBirthdaySafe()));
            purchaseParams.add(travelerIndex + TripMateRequestConstants.GENDER, "M"); // todo
            purchaseParams.add(travelerIndex + TripMateRequestConstants.TRIPCOST, String.valueOf(traveler.getTripCost().toPlainString()));
            i++;
        }
        purchaseParams.add(TripMateRequestConstants.BOOKING_NUMBER, "");
        purchaseParams.add(TripMateRequestConstants.TRIPCOST, purchaseRequest.getQuoteRequest().getTripCost().toString());
        purchaseParams.add(TripMateRequestConstants.EMAIL, purchaseRequest.getEmail());
        purchaseParams.add(TripMateRequestConstants.FAX, "");
        purchaseParams.add(TripMateRequestConstants.PHONE1, purchaseRequest.getPhone());
        purchaseParams.add(TripMateRequestConstants.ADDRESS1, purchaseRequest.getAddress());
        purchaseParams.add(TripMateRequestConstants.ADDRESS2, "");
        purchaseParams.add(TripMateRequestConstants.STATE, purchaseRequest.getQuoteRequest().getResidentState().name());
        purchaseParams.add(TripMateRequestConstants.ZIP, purchaseRequest.getPostalCode());
        purchaseParams.add(TripMateRequestConstants.ZIP4, "");
        purchaseParams.add(TripMateRequestConstants.CITY, purchaseRequest.getCity());
        purchaseParams.add(TripMateRequestConstants.DEPARTDATE, dateFormat.format(purchaseRequest.getQuoteRequest().getDepartDate()));
        purchaseParams.add(TripMateRequestConstants.RETURNDATE, dateFormat.format(purchaseRequest.getQuoteRequest().getReturnDate()));
        purchaseParams.add(TripMateRequestConstants.TRIP_LENGTH, String.valueOf(purchaseRequest.getQuoteRequest().getTripLength()));
        purchaseParams.add(TripMateRequestConstants.DEPOSITDATE, purchaseRequest.getQuoteRequest().getDepositDate() != null ? dateFormat.format(purchaseRequest.getQuoteRequest().getDepositDate()) : "");
        setUpsalePurchaseParameters(purchaseParams, purchaseRequest);
        purchaseParams.add(TripMateRequestConstants.PREMIUM, String.valueOf(purchaseRequest.getProduct().getTotalPrice()));
        purchaseParams.add(TripMateRequestConstants.DESTINATION, String.valueOf(DestinationCodes.getDestinationByCode(purchaseRequest.getQuoteRequest().getDestinationCountry().name())));
        String tripType = TripMateClient.getTripType(purchaseRequest.getTripTypes());
        purchaseParams.add(TripMateRequestConstants.TRIPTYPE, tripType);
        purchaseParams.add(TripMateRequestConstants.AIRLINE, TripMateRequestConstants.AIRLINE_VALUE); // todo: One Required (airline or tour operator or misc travel providers or cruise line)
        CreditCard creditCard = purchaseRequest.getCreditCard();
        purchaseParams.add(TripMateRequestConstants.CC_ADDRESS1, creditCard.getCcAddress());
        purchaseParams.add(TripMateRequestConstants.CC_ADDRESS2, "");
        purchaseParams.add(TripMateRequestConstants.CC_CITY, creditCard.getCcCity());
        purchaseParams.add(TripMateRequestConstants.CC_POSTALCODE, creditCard.getCcZipCode());
        purchaseParams.add(TripMateRequestConstants.CC_STATE, creditCard.getCcStateCode().name());
        purchaseParams.add(TripMateRequestConstants.CC_ZIP4, "");
        purchaseParams.add(TripMateRequestConstants.CC_EMAIL, purchaseRequest.getEmail()); // todo: from credit card?
        purchaseParams.add(TripMateRequestConstants.CC_PHONE1, creditCard.getCcPhone());
        purchaseParams.add(TripMateRequestConstants.CC_FIRSTNAME, creditCard.getCcName());
        purchaseParams.add(TripMateRequestConstants.CC_LASTNAME, " ");
        purchaseParams.add(TripMateRequestConstants.CC_MIDDLENAME, "");
        String cardType;
        switch (creditCard.getCcType()) {
            case VISA:
                cardType = "v";
                break;
            case MasterCard:
                cardType = "mc";
                break;
            case AmericanExpress:
                cardType = "ae";
                break;
            case Discover:
                cardType = "d";
                break;
            default:
                cardType = "";
        }
        purchaseParams.add(TripMateRequestConstants.CC_PAYMENTTYPE, cardType);
        purchaseParams.add(TripMateRequestConstants.CC_CODE, creditCard.getCcCode());
        purchaseParams.add(TripMateRequestConstants.CC_MONTH, creditCard.getCcExpMonth());
        purchaseParams.add(TripMateRequestConstants.CC_YEAR, creditCard.getCcExpYear().substring(2));
        purchaseParams.add(TripMateRequestConstants.CC_NUMBER, String.valueOf(creditCard.getCcNumber()));
        return purchaseParams;
    }

    private void setUpsalePurchaseParameters(MultiValueMap<String, String> purchaseParams, PurchaseRequest purchaseRequest) {
        String flightAdAndDValue = purchaseRequest.getQuoteRequest().getUpsaleValue(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "");
        String policyCode = purchaseRequest.getProduct().getPolicyMetaCode().getCode();
        if (!flightAdAndDValue.isEmpty()) {
            String flightAdAndDApiValue = policyMetaCategoryValueService.getApiValue(
                    purchaseRequest.getProduct().getPolicyMeta().getId(), CategoryCodes.FLIGHT_ONLY_AD_AND_D, flightAdAndDValue, purchaseRequest.getQuoteRequest());
            purchaseParams.add(optionalPackages.get(policyCode + CategoryCodes.FLIGHT_ONLY_AD_AND_D), flightAdAndDApiValue);
        }
        String rentalCarValue = purchaseRequest.getQuoteRequest().getUpsaleValue(CategoryCodes.RENTAL_CAR, "");
        if (!rentalCarValue.isEmpty()) {
            String rentalCarApiValue = policyMetaCategoryValueService.getApiValue(
                    purchaseRequest.getProduct().getPolicyMeta().getId(), CategoryCodes.RENTAL_CAR, rentalCarValue, purchaseRequest.getQuoteRequest());
            purchaseParams.add(optionalPackages.get(policyCode+CategoryCodes.RENTAL_CAR),rentalCarApiValue);
            purchaseParams.add(TripMateRequestConstants.RENTALCAR, String.valueOf(purchaseRequest.getQuoteRequest().getRentalCarLength()));
        }
        String hazardousSportValue = purchaseRequest.getQuoteRequest().getUpsaleValue(CategoryCodes.HAZARDOUS_SPORTS, "");
        if (!hazardousSportValue.isEmpty()) {
            String hazardousSportApiValue = policyMetaCategoryValueService.getApiValue(
                    purchaseRequest.getProduct().getPolicyMeta().getId(), CategoryCodes.HAZARDOUS_SPORTS, hazardousSportValue, purchaseRequest.getQuoteRequest());
            purchaseParams.add(optionalPackages.get(policyCode+CategoryCodes.HAZARDOUS_SPORTS),hazardousSportApiValue);
        }
        /*
        String personalPropertyValue = purchaseRequest.getQuoteRequest().getUpsaleValue(CategoryCodes.PERSONAL_PROPERTY, "");
        if (!personalPropertyValue.isEmpty()) {
            String personalPropertyApiValue = policyMetaCategoryValueService.getApiValue(
                    purchaseRequest.getProduct().getPolicyMeta().getId(), CategoryCodes.PERSONAL_PROPERTY, personalPropertyValue, purchaseRequest.getQuoteRequest());
            purchaseParams.add(optionalPackages.get(policyCode+CategoryCodes.PERSONAL_PROPERTY),personalPropertyApiValue);
        }
        */
    }
}