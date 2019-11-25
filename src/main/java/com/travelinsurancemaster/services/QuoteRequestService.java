package com.travelinsurancemaster.services;

import com.maxmind.geoip.Location;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import com.travelinsurancemaster.model.webservice.common.BaseTripCancellation;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.common.forms.EditQuoteRequestFormDTO;
import com.travelinsurancemaster.model.webservice.common.forms.Step1QuoteRequestForm;
import com.travelinsurancemaster.model.webservice.common.forms.Step2QuoteRequestForm;
import com.travelinsurancemaster.model.webservice.common.forms.Step3QuoteRequestForm;
import com.travelinsurancemaster.util.CountryCodesUtils;
import com.travelinsurancemaster.util.DateUtil;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Created by Chernov Artur on 04.08.15.
 */


@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class QuoteRequestService {

    private static final Logger log = LoggerFactory.getLogger(QuoteRequestService.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private GeoIpDatabaseService geoIpDatabaseService;

    public QuoteRequest getQuoteRequestWithCancellation(QuoteRequest quoteRequest, PolicyMeta policyMeta) {
//        log.info("getQuoteRequestWithCancellation() {} {}", quoteRequest, policyMeta);

        // used to filter plans if they should nt be shown with 0 trip cost
        if (quoteRequest == null || (quoteRequest.getTripCost().equals(BigDecimal.ZERO) && !policyMeta.isShowOnQuotes())) {
            return null;
        }
        if (quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
            return quoteRequest;
        } else {
            if (policyMetaService.isPolicyHasAndSupportsCancellation(policyMeta.getId())) {
                QuoteRequest quoteRequestWithCancellation = QuoteRequest.newInstance(quoteRequest);
                quoteRequestWithCancellation.setTripCost(BigDecimal.valueOf(0));
                for (GenericTraveler traveler : quoteRequestWithCancellation.getTravelers()) {
                    traveler.setTripCost(BigDecimal.valueOf(0));
                }
                return quoteRequestWithCancellation;
            } else if (policyMeta.getMinimalTripCost() != null) {
                QuoteRequest quoteRequestWithMinimalTripCost = QuoteRequest.newInstance(quoteRequest);
                quoteRequestWithMinimalTripCost.setTripCost(policyMeta.getMinimalTripCost());
                for (GenericTraveler traveler : quoteRequestWithMinimalTripCost.getTravelers()) {
                    traveler.setTripCost(policyMeta.getMinimalTripCost());
                }
                return quoteRequestWithMinimalTripCost;
            } else {
                return quoteRequest;
            }
        }
    }

    public boolean checkMinTripCost(QuoteRequest quoteRequest, PolicyMeta policyMeta) {
//        log.info("checkMinTripCost() {} {}", quoteRequest, policyMeta);

        return
            !quoteRequest.getCategories().keySet().contains(CategoryCodes.TRIP_CANCELLATION)
        &&
            !policyMetaService.isPolicyHasAndSupportsCancellation(policyMeta.getId())
        &&
            policyMeta.getMinimalTripCost() != null;
    }


    public boolean checkNoTripCancellation(QuoteRequest quoteRequest, PolicyMetaCategoryValue value) {
//        log.info("checkMinTripCost() {} {}", quoteRequest, policyMeta);

        String name = value.getPolicyMetaCategory().getCategory().getCode();
        String code = CategoryCodes.TRIP_CANCELLATION;

        return !quoteRequest.getCategories().keySet().contains(CategoryCodes.TRIP_CANCELLATION) && name.equals(code);
    }

    public QuoteRequest prepareQuoteRequestAfterStep2(Model model) {
        log.info("prepareQuoteRequestAfterStep2() {} ", model);
        log.debug("Prepare QuoteRequest after Step 2 From");
        Step1QuoteRequestForm form1 = (Step1QuoteRequestForm) model.asMap().get("step1Form");
        Step2QuoteRequestForm form2 = (Step2QuoteRequestForm) model.asMap().get("step2Form");
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setDestinationCountry(form1.getDestinationCountry());
        quoteRequest.setTimezoneOffset(form1.getTimezoneOffset());
        quoteRequest.setDepartDate(form1.getDepartDate());
        quoteRequest.setReturnDate(form1.getReturnDate());
        quoteRequest.setTripCostTotal(form1.isTripCostTotal());
        int countTravelers = form2.getTravelers().size();
        BigDecimal tripCost = form1.getTripCost();
        BigDecimal costPerTraveler;
        if (form1.isTripCostTotal()) {
            quoteRequest.setTripCost(tripCost);
            costPerTraveler = tripCost.divide(BigDecimal.valueOf(countTravelers), RoundingMode.CEILING);
        } else {
            quoteRequest.setTripCost(tripCost.multiply(BigDecimal.valueOf(countTravelers)));
            costPerTraveler = tripCost;
        }
        for (GenericTraveler traveler : form2.getTravelers()) {
            traveler.setTripCost(costPerTraveler);
            quoteRequest.getTravelers().add(traveler);
        }
        // first traveler in quote is primary by default
        quoteRequest.getTravelers().get(0).setPrimary(true);
        quoteRequest.setResidentCountry(form2.getResidentCountry());
        quoteRequest.setResidentState(form2.getResidentState());
        quoteRequest.setIncludesUS(form2.getIncludesUS());
        quoteRequest.setCitizenCountry(form2.getCitizenCountry());
        return quoteRequest;
    }

    public QuoteRequest getQuoteRequestAfterStep3(Model model) {
        log.info("getQuoteRequestAfterStep3() {} ", model);

        Step1QuoteRequestForm form1 = (Step1QuoteRequestForm) model.asMap().get("step1Form");
        Step2QuoteRequestForm form2 = (Step2QuoteRequestForm) model.asMap().get("step2Form");
        Step3QuoteRequestForm form3 = (Step3QuoteRequestForm) model.asMap().get("step3Form");
        return getQuoteRequestFromSteps(form1, form2, form3);
    }

    public QuoteRequest getQuoteRequestFromEditForm(EditQuoteRequestFormDTO editForm) {
        log.info("getQuoteRequestFromEditForm() {} ", editForm);

        return getQuoteRequestFromSteps(editForm.getStep1Form(), editForm.getStep2Form(), editForm.getStep3Form());
    }

    private QuoteRequest getQuoteRequestFromSteps(Step1QuoteRequestForm form1, Step2QuoteRequestForm form2, Step3QuoteRequestForm form3) {
        log.info("getQuoteRequestFromSteps() {} ", form1, form2, form3);

        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setDestinationCountry(form1.getDestinationCountry());
        quoteRequest.setTimezoneOffset(form1.getTimezoneOffset());
        quoteRequest.setDepartDate(form1.getDepartDate());
        quoteRequest.setReturnDate(form1.getReturnDate());
        quoteRequest.setTripCostTotal(form1.isTripCostTotal());
        int countTravelers = form2.getTravelers().size();
        BigDecimal tripCost = form1.getTripCost();
        BigDecimal costPerTraveler;
        if (form1.isTripCostTotal()) {
            quoteRequest.setTripCost(tripCost);
            costPerTraveler = tripCost.divide(BigDecimal.valueOf(countTravelers), RoundingMode.CEILING);
        } else {
            quoteRequest.setTripCost(tripCost.multiply(BigDecimal.valueOf(countTravelers)));
            costPerTraveler = tripCost;
        }
        for (GenericTraveler traveler : form2.getTravelers()) {
            traveler.setTripCost(costPerTraveler);
            quoteRequest.getTravelers().add(traveler);
        }
        // first traveler in quote is primary by default
        quoteRequest.getTravelers().get(0).setPrimary(true);
        quoteRequest.setResidentCountry(form2.getResidentCountry());
        quoteRequest.setResidentState(form2.getResidentState());
        quoteRequest.setIncludesUS(form2.getIncludesUS());
        quoteRequest.setCitizenCountry(form2.getCitizenCountry());
        if (quoteRequest.isZeroCost()) {
            quoteRequest.setPaymentDate(DateUtil.getLocalDateNow(quoteRequest.getTimezoneOffset()));
            quoteRequest.setDepositDate(DateUtil.getLocalDateNow(quoteRequest.getTimezoneOffset()));
            quoteRequest.setPreExistingMedicalAndCancellation(false);
            quoteRequest.setPlanType(PlanType.LIMITED);
        } else {
            quoteRequest.setPlanType(PlanType.COMPREHENSIVE);
            quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
            boolean preExistingMedicalAndCancellation = form3.isPreExistingMedicalAndCancellation();
            quoteRequest.setDepositDate(form3.getDepositDate());
            // Important! preExistingMedicalAndCancellation = true when checkbox on 3rd form is not checked and payment date not empty
            if (preExistingMedicalAndCancellation) {
                quoteRequest.setPaymentDate(form3.getPaymentDate());
            } else {
                quoteRequest.setPaymentDate(DateUtil.getLocalDateNow(quoteRequest.getTimezoneOffset()));
            }
            quoteRequest.setPreExistingMedicalAndCancellation(preExistingMedicalAndCancellation);
        }
        return quoteRequest;
    }

    public QuoteRequest getOriginalQuoteRequest(QuoteRequest quoteRequest) {
        log.info("getOriginalQuoteRequest() {} ", quoteRequest);

        QuoteRequest originalQuoteRequest = QuoteRequest.newInstance(quoteRequest);
        originalQuoteRequest.getCategories().clear();
        originalQuoteRequest.getSliderCategories().clear();
        return originalQuoteRequest;
    }

    public QuoteRequest fillBaseTripCancellation(QuoteRequest quoteRequest) {
        log.info("fillBaseTripCancellation() {} ", quoteRequest);

        if (quoteRequest.getBaseTripCancellation().getId() == BaseTripCancellation.NAN.getId()) {
            quoteRequest.setBaseTripCancellation(quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)
                    ? BaseTripCancellation.TRUE : BaseTripCancellation.FALSE);
        }
        return quoteRequest;
    }

    /**
     * @param httpServletRequest
     * @return empty quoteRequest with info from IP
     */
    public QuoteRequest createFakeQuoteRequestByIP(HttpServletRequest httpServletRequest) {
        log.info("createFakeQuoteRequestByIP() {} ", httpServletRequest);

        QuoteRequest quoteRequest = new QuoteRequest();
        Location location = geoIpDatabaseService.getLocation(httpServletRequest);
        if (location == null) {
            return quoteRequest;
        }
        CountryCode locCountry = EnumUtils.getEnum(CountryCode.class, location.countryCode);
        StateCode locRegion = EnumUtils.getEnum(StateCode.class, location.region);
        quoteRequest.setResidentCountry(locCountry);
        quoteRequest.setResidentState(locRegion);
        return quoteRequest;
    }

    public EditQuoteRequestFormDTO getEditQuoteRequestForm(QuoteRequest quoteRequest, String requestId, String policyUniqueCode, Boolean comparePlans) {

        log.info("getEditQuoteRequestForm() {} ", quoteRequest);

        EditQuoteRequestFormDTO editQuoteRequestFormDTO = new EditQuoteRequestFormDTO();
        editQuoteRequestFormDTO.setStep1Form(fillStep1Form(quoteRequest));
        editQuoteRequestFormDTO.setStep2Form(fillStep2Form(quoteRequest));
        editQuoteRequestFormDTO.setStep3Form(fillStep3Form(quoteRequest));
        editQuoteRequestFormDTO.setCountries(CountryCodesUtils.getPublicVisibleCountryCodesSorted());
        editQuoteRequestFormDTO.setStates(StateCode.getStatesUS(), StateCode.getStatesCanada());
        editQuoteRequestFormDTO.setRequestId(requestId);
        editQuoteRequestFormDTO.setPolicyUniqueCode(policyUniqueCode);
        editQuoteRequestFormDTO.setComparePlans(comparePlans);

        return editQuoteRequestFormDTO;
    }

    public Step1QuoteRequestForm fillStep1Form(QuoteRequest quoteRequest) {
        log.info("fillStep1Form() {} ", quoteRequest);

        Step1QuoteRequestForm form1 = new Step1QuoteRequestForm();
        form1.setDestinationCountry(quoteRequest.getDestinationCountry());
        form1.setTimezoneOffset(quoteRequest.getTimezoneOffset());
        form1.setDepartDate(quoteRequest.getDepartDate());
        form1.setReturnDate(quoteRequest.getReturnDate());
        form1.setTripCostTotal(quoteRequest.isTripCostTotal());
        if (!quoteRequest.isTripCostTotal()) {
            form1.setTripCost(quoteRequest.getTripCost().divide(BigDecimal.valueOf(quoteRequest.getTravelers().size()), BigDecimal.ROUND_HALF_UP));
        } else {
            form1.setTripCost(quoteRequest.getTripCost());
        }
        return form1;
    }

    public Step2QuoteRequestForm fillStep2Form(QuoteRequest quoteRequest) {
        log.info("fillStep2Form() {} ", quoteRequest);

        Step2QuoteRequestForm form2 = new Step2QuoteRequestForm();
        quoteRequest.getTravelers().forEach(genericTraveler -> form2.getTravelers().add(genericTraveler));
        form2.setResidentCountry(quoteRequest.getResidentCountry());
        form2.setResidentState(quoteRequest.getResidentState());
        form2.setIncludesUS(quoteRequest.getIncludesUS());
        form2.setResidentCountryStatePair(CountryCodesUtils.getCountryStatePair(quoteRequest.getResidentCountry(), quoteRequest.getResidentState()));
        form2.setCitizenCountry(quoteRequest.getCitizenCountry());
        return form2;
    }

    public Step3QuoteRequestForm fillStep3Form(QuoteRequest quoteRequest) {
        log.info("fillStep3Form() {} ", quoteRequest);

        Step3QuoteRequestForm form3 = new Step3QuoteRequestForm();
        form3.setPreExistingMedicalAndCancellation(quoteRequest.isPreExistingMedicalAndCancellation());
        form3.setDepositDate(quoteRequest.getDepositDate());
        form3.setPaymentDate(quoteRequest.getPaymentDate());
        return form3;
    }

    public boolean equalsQuoteRequests(QuoteRequest firstRequest, QuoteRequest secondRequest) {
        log.info("equalsQuoteRequests() {} {}", firstRequest, secondRequest);

        if (!firstRequest.getDestinationCountry().equals(secondRequest.getDestinationCountry())) {
            return false;
        }
        if (firstRequest.getDepartDate().compareTo(secondRequest.getDepartDate()) != 0) {
            return false;
        }
        if (firstRequest.getReturnDate().compareTo(secondRequest.getReturnDate()) != 0) {
            return false;
        }
        if (firstRequest.getTripCost().compareTo(secondRequest.getTripCost()) != 0) {
            return false;
        }
        if (firstRequest.isTripCostTotal() != secondRequest.isTripCostTotal()) {
            return false;
        }
        if (firstRequest.getTravelers().size() != secondRequest.getTravelers().size()) {
            return false;
        }
        for (int i = 0; i < firstRequest.getTravelers().size(); i++) {
            if (!Objects.equals(firstRequest.getTravelers().get(i).getAge(), secondRequest.getTravelers().get(i).getAge())) {
                return false;
            }
        }
        if (!firstRequest.getResidentCountry().equals(secondRequest.getResidentCountry())) {
            return false;
        }
        if (!Objects.equals(firstRequest.getResidentState(), secondRequest.getResidentState())) {
            return false;
        }
        if (firstRequest.getIncludesUS() != secondRequest.getIncludesUS()) {
            return false;
        }
        if (!firstRequest.getCitizenCountry().equals(secondRequest.getCitizenCountry())) {
            return false;
        }
        if (firstRequest.getDepositDate() != null && secondRequest.getDepositDate() != null && firstRequest.getDepositDate().compareTo(secondRequest.getDepositDate()) != 0) {
            return false;
        }
        if (firstRequest.getPaymentDate() != null && secondRequest.getPaymentDate() != null && firstRequest.getPaymentDate().compareTo(secondRequest.getPaymentDate()) != 0) {
            return false;
        }
        if (firstRequest.getRentalCarStartDate() != null && secondRequest.getRentalCarStartDate() != null && firstRequest.getRentalCarStartDate().compareTo(secondRequest.getRentalCarStartDate()) != 0) {
            return false;
        }
        if (firstRequest.getRentalCarEndDate() != null && secondRequest.getRentalCarEndDate() != null && firstRequest.getRentalCarEndDate().compareTo(secondRequest.getRentalCarEndDate()) != 0) {
            return false;
        }
        if (firstRequest.isPreExistingMedicalAndCancellation() != secondRequest.isPreExistingMedicalAndCancellation()){
            return false;
        }
        return true;
    }

    public QuoteRequest fillUpsalesBeforePurchase(QuoteRequest quoteRequest) {
        log.info("fillUpsalesBeforePurchase() {}", quoteRequest);

        quoteRequest.getUpsaleValues().putAll(quoteRequest.getCategories());
        return quoteRequest;
    }
}
