package com.travelinsurancemaster.services.clients;

import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.hthtravelinsurance.HTHTravelInsuranceMapper;
import com.travelinsurancemaster.services.AbstractRestClient;
import com.travelinsurancemaster.services.PolicyQuoteParamService;
import com.travelinsurancemaster.services.hthtravelinsurance.RegionFactor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by ritchie on 6/11/15.
 */
public class HTHTravelInsuranceClient extends AbstractRestClient {

    @Autowired
    private PolicyQuoteParamService policyQuoteParamService;

    @Autowired
    private HTHTravelInsuranceMapper hthTravelInsuranceMapper;

    public static final String EXCURSION_PRODUCT_CODE = "75";
    public static final String VOYAGER_PRODUCT_CODE = "76";
    public static final String TRAVEL_GAP_GOLD_PRODUCT_CODE = "73";
    public static final String TRAVEL_GAP_SILVER_PRODUCT_CODE = "74";
    public static final String TRIP_PROTECTOR_PREFFERED_PRODUCT_CODE = "110";
    public static final String TRIP_PROTECTOR_ECONOMY_PRODUCT_CODE = "108";
    public static final String TRIP_PROTECTOR_CLASSIC_PRODUCT_CODE = "109";

    public static final String ERROR = "ERROR";


    public HTHTravelInsuranceClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public String getSingleTripPurchaseResponse(MultiValueMap<String, String> requestParameters) {
        String purchaseRequest = restTemplate.postForObject(apiProperties.gethTHTravelInsuranceSingleTrip().getUrl(), requestParameters, String.class);
        return purchaseRequest;
    }

    public String getTripProtectorPurchaseResponse(MultiValueMap<String, String> requestParameters) {
        String purchaseRequest = restTemplate.postForObject(apiProperties.gethTHTravelInsuranceTripProtector().getUrl(), requestParameters, String.class);
        return purchaseRequest;
    }

    public String getMultiTripPurchaseResponse(MultiValueMap<String, String> requestParameters) {
        String purchaseRequest = restTemplate.postForObject(apiProperties.gethTHTravelInsuranceTravelGap().getUrl(), requestParameters, String.class);
        return purchaseRequest;
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        QuoteResult quoteResult;
        String policyCode = policyMetaCode.getCode();
        switch (policyCode) {
            case EXCURSION_PRODUCT_CODE:
            case VOYAGER_PRODUCT_CODE:
                quoteResult = quoteSingleTrip(request, policyMeta, policyMetaCode);
                break;
            case TRAVEL_GAP_GOLD_PRODUCT_CODE:
            case TRAVEL_GAP_SILVER_PRODUCT_CODE:
                quoteResult = quoteMultiTrip(request, policyMeta, policyMetaCode);
                break;
            case TRIP_PROTECTOR_CLASSIC_PRODUCT_CODE:
            case TRIP_PROTECTOR_ECONOMY_PRODUCT_CODE:
            case TRIP_PROTECTOR_PREFFERED_PRODUCT_CODE:
                quoteResult = quoteTripProtector(request, policyMeta, policyMetaCode);
                break;
            default:
                quoteResult = new QuoteResult();
                quoteResult.error("-1", "Wrong policyMeta code " + policyCode);
        }
        return quoteResult;
    }

    private QuoteResult quoteTripProtector(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        QuoteResult quoteResult = new QuoteResult();
        Long regionFactor = RegionFactor.getRegionRateByCountryCode(request.getDestinationCountry().name());
        //TODO: LocalDate.now() must be in the local timezone
        long daysBeforeDepart = DAYS.between(LocalDate.now(), request.getDepartDate()) + 1;
        Long lagFactor;
        if (daysBeforeDepart <= 30) {
            lagFactor = 90l;
        } else if (daysBeforeDepart > 30 && daysBeforeDepart <= 90) {
            lagFactor = 95l;
        } else {
            lagFactor = 104l;
        }
        BigDecimal priceAllTravelers = BigDecimal.valueOf(0);
        for (GenericTraveler traveler : request.getTravelers()) {
            Long baseRate = policyQuoteParamService.getPolicyQuoteParam(request, policyMeta, traveler.getTripCost().longValue(), 0l, Long.valueOf(traveler.getAge()), null, null);
            if (baseRate == null) {
                quoteResult.setStatus(Result.Status.ERROR);
                quoteResult.getErrors().add(new Result.Error("-1", "Trip cost or Age do not comply with the limits"));
                return quoteResult;
            }
            priceAllTravelers = priceAllTravelers.add(BigDecimal.valueOf(baseRate).divide(BigDecimal.valueOf(100))
                    .multiply(BigDecimal.valueOf(regionFactor).divide(BigDecimal.valueOf(100)))
                    .multiply(BigDecimal.valueOf(lagFactor)).divide(BigDecimal.valueOf(100)));
        }
        if (policyMetaCode.getCode().equals(TRIP_PROTECTOR_PREFFERED_PRODUCT_CODE)) {
            String maximumLimit = request.getUpsaleValue(CategoryCodes.CANCEL_FOR_ANY_REASON, "0");
            if (!maximumLimit.equals("0")) {
                priceAllTravelers = priceAllTravelers.add(priceAllTravelers.multiply(BigDecimal.valueOf(50)).divide(BigDecimal.valueOf(100)));
            }
        }
        priceAllTravelers = priceAllTravelers.setScale(0, BigDecimal.ROUND_HALF_UP);
        quoteResult.setStatus(Result.Status.SUCCESS);
        Product product = new Product(policyMeta, policyMetaCode, priceAllTravelers, request.getUpsaleValues());
        quoteResult.products.add(product);
        return quoteResult;
    }

    private QuoteResult quoteMultiTrip(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        QuoteResult quoteResult = new QuoteResult();
        Long price = policyQuoteParamService.getPolicyQuoteParam(request, policyMeta, 0l, 0l, Long.valueOf(request.getTravelers().get(0).getAge()), null, null);
        if (price == null) {
            quoteResult.setStatus(Result.Status.ERROR);
            quoteResult.getErrors().add(new Result.Error("-1", "Age does not comply with the deductible limits"));
            return quoteResult;
        }
        quoteResult.setStatus(Result.Status.SUCCESS);
        Product product = new Product(policyMeta, policyMetaCode, BigDecimal.valueOf(price), request.getUpsaleValues());
        quoteResult.products.add(product);
        return quoteResult;
    }

    private QuoteResult quoteSingleTrip(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        QuoteResult quoteResult = new QuoteResult();
        Long priceDailyAllTravelers = 0l;
        for (GenericTraveler traveler : request.getTravelers()) {
            long deductibleLevel = NumberUtils.toLong(request.getUpsaleValue(CategoryCodes.MEDICAL_DEDUCTIBLE, HTHTravelInsuranceMapper.DEFAULT_DEDUCTIBLE_LEVEL));
            long maximumBenefitLimit = NumberUtils.toLong(request.getUpsaleValue(CategoryCodes.EMERGENCY_MEDICAL, HTHTravelInsuranceMapper.DEFAULT_MAXIMUM_BENEFIT_LEVEL));
            Long priceDaily = policyQuoteParamService.getPolicyQuoteParam(request, policyMeta, deductibleLevel, maximumBenefitLimit, Long.valueOf(traveler.getAge()), null, null);
            if (priceDaily == null) {
                quoteResult.setStatus(Result.Status.ERROR);
                quoteResult.getErrors().add(new Result.Error("-1", "Age does not comply with the deductible limits"));
                return quoteResult;
            }
            priceDailyAllTravelers += priceDaily;
        }

        long days = DAYS.between(request.getDepartDate(), request.getReturnDate()) + 1;
        BigDecimal price;
        if (days <= 7) {
            price = BigDecimal.valueOf(priceDailyAllTravelers).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(7));
        } else {
            price = BigDecimal.valueOf(priceDailyAllTravelers).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(days));
        }
        quoteResult.setStatus(Result.Status.SUCCESS);
        Product product = new Product(policyMeta, policyMetaCode, price, request.getUpsaleValues());
        quoteResult.products.add(product);
        return quoteResult;
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {
        PurchaseResponse purchaseResponse = new PurchaseResponse();
        String response;
        MultiValueMap<String, String> purchaseParameters;
        String policyCode = request.getProduct().getPolicyMetaCode().getCode();
        switch (policyCode) {
            case EXCURSION_PRODUCT_CODE:
            case VOYAGER_PRODUCT_CODE:
                purchaseParameters = hthTravelInsuranceMapper.toSingleAndMultiTripMap(request, apiProperties.gethTHTravelInsurance());
                response = getSingleTripPurchaseResponse(purchaseParameters);
                break;
            case TRAVEL_GAP_GOLD_PRODUCT_CODE:
            case TRAVEL_GAP_SILVER_PRODUCT_CODE:
                purchaseParameters = hthTravelInsuranceMapper.toSingleAndMultiTripMap(request, apiProperties.gethTHTravelInsurance());
                response = getMultiTripPurchaseResponse(purchaseParameters);
                break;
            case TRIP_PROTECTOR_CLASSIC_PRODUCT_CODE:
            case TRIP_PROTECTOR_ECONOMY_PRODUCT_CODE:
            case TRIP_PROTECTOR_PREFFERED_PRODUCT_CODE:
                purchaseParameters = hthTravelInsuranceMapper.toTripProtectorMap(request, apiProperties.gethTHTravelInsurance());
                response = getTripProtectorPurchaseResponse(purchaseParameters);
                break;
            default:
                throw new IllegalArgumentException(policyCode);
        }
        if (response.contains(ERROR)) {
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(
                    new Result.Error("-1", response));
        } else {
            purchaseResponse.setStatus(Result.Status.SUCCESS);
            purchaseResponse.setPolicyNumber(response.split(" ")[1]);
        }
        return purchaseResponse;
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.HTHTravelInsurance;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        List<Result.Error> errors = new ArrayList<>();
        switch (policyMetaCode.getCode()) {
            case TRAVEL_GAP_GOLD_PRODUCT_CODE:
            case TRAVEL_GAP_SILVER_PRODUCT_CODE:
                long days = DAYS.between(request.getDepartDate(), request.getReturnDate()) + 1;
                if (days > 70) {
                    errors.add(new Result.Error("-1", "Trip length is up to 70 days"));
                }
                // todo: family question
                if (request.getTravelers().size() > 1) {
                    errors.add(new Result.Error("-1", "Amount of travelers is more than one"));
                }
                return errors;
        }
        return errors;
    }
}
