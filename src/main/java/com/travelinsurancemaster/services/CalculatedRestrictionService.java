package com.travelinsurancemaster.services;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.Restriction;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequestConstants;
import com.travelinsurancemaster.util.DateUtil;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;


@Service
public class CalculatedRestrictionService {

    private static final String TRIP_COST = "tripCost";
    private static final String TODAY = "planPaymentDate";
    private static final String FINAL_PAYMENT_DATE = "finalPaymentDate";

    /**
     * Validates policy meta categories by groovy conditions
     *
     * @return true if valid, false otherwise
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isValid(QuoteRequest request, String calculatedRestrictions, boolean isScriptValidation) {
        // for cms categories validation without quote request
        if (request.isEmpty()) {
            return true;
        }
        Binding binding = getVariablesBinding(request);
        GroovyShell groovyShell = new GroovyShell(binding);
        boolean calculatedResult;
        try {
            calculatedResult = (Boolean) groovyShell.evaluate(calculatedRestrictions);
        } catch (Exception e) {
            return false;
        }
        if (!isScriptValidation) {
            return calculatedResult;
        } else {
            return true;
        }
    }

    private Binding getVariablesBinding(QuoteRequest request) {
        Binding binding = new Binding();
        LocalDate today = DateUtil.getLocalDateNow(request.getTimezoneOffset());
        binding.setVariable(TODAY, DateUtil.fromLocalDate(today));
        binding.setVariable(QuoteRequestConstants.DEPART_DATE, DateUtil.fromLocalDate(request.getDepartDate()));
        binding.setVariable(QuoteRequestConstants.RETURN_DATE, DateUtil.fromLocalDate(request.getReturnDate()));
        binding.setVariable(TRIP_COST, request.getTripCost());
        binding.setVariable(QuoteRequestConstants.TRIP_COST_PER_TRAVELER, request.getTripCostPerTraveler());
        binding.setVariable(QuoteRequestConstants.TRAVELERS_COUNT, request.getTravelers().size());
        binding.setVariable(QuoteRequestConstants.RESIDENT_COUNTRY, request.getResidentCountry().name());
        if (request.getResidentCountry().equals(CountryCode.US))
            binding.setVariable(QuoteRequestConstants.RESIDENT_STATE, request.getResidentState().name());
        binding.setVariable(QuoteRequestConstants.CITIZEN_COUNTRY, request.getCitizenCountry().name());
        binding.setVariable(QuoteRequestConstants.DESTINATION_COUNTRY, request.getDestinationCountry().name());
        binding.setVariable(QuoteRequestConstants.DEPOSIT_DATE, DateUtil.fromLocalDate(request.getDepositDate()));
        binding.setVariable(FINAL_PAYMENT_DATE, DateUtil.fromLocalDate(request.getPaymentDate()));
        binding.setVariable(QuoteRequestConstants.TRIP_LENGTH, request.getTripLength());
        binding.setVariable(QuoteRequestConstants.TRAVELERS, request.getTravelers());
        binding.setVariable(QuoteRequestConstants.TRAVELER_AGE, request.getTravelers().get(0).getAge());
        return binding;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Cacheable(value = CacheConfig.RESTRICTION_VALIDATION_CACHE, keyGenerator = "restrictionValidateCacheKeyGenerator")
    public boolean isValidRestriction(QuoteRequest request, Restriction restriction) {
        switch (restriction.getRestrictionType()) {
            case RESIDENT:
                return request.getResidentState() != null ?
                        isValid(request.getResidentState(), restriction) && isValid(request.getResidentCountry(), restriction) :
                        isValid(request.getResidentCountry(), restriction);
            case DESTINATION:
                return isValid(request.getDestinationCountry(), restriction);
            case CITIZEN:
                return isValid(request.getCitizenCountry(), restriction);
            case AGE:
                return CollectionUtils.isNotEmpty(request.getTravelers()) && isValid(request.getTravelers(), restriction);
            case TRIP_COST_PER_TRAVELER:
                return CollectionUtils.isNotEmpty(request.getTravelers()) && isValid(request.getTravelers().get(0).getTripCost(), restriction);
            case TRIP_COST:
                return isValid(request.getTripCost(), restriction);
            case LENGTH_OF_TRAVEL:
                return isValid(request.getDepartDate(), request.getReturnDate(), restriction);
            case CALCULATE:
                return isValid(request, restriction.getCalculatedRestrictions(), false);
            default:
                return true;
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isValid(CountryCode countryCode, Restriction restriction) {
        if (restriction == null) {
            return true;
        }
        Set<CountryCode> countries = restriction.getCountries();
        if (CollectionUtils.isEmpty(countries) || countryCode == null) {
            return true;
        }
        for (CountryCode country : countries) {
            if (country == countryCode) {
                return restriction.getRestrictionPermit() == Restriction.RestrictionPermit.ENABLED;
            }
        }
        return restriction.getRestrictionPermit() != Restriction.RestrictionPermit.ENABLED; //if enabled and none found, than all others are forbidden
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isValid(StateCode stateCode, Restriction restriction) {
        if (restriction == null) {
            return true;
        }
        Set<StateCode> states = restriction.getStates();
        if (CollectionUtils.isEmpty(states) || stateCode == null) {
            return true;
        }
        for (StateCode state : states) {
            if (state == stateCode) {
                return restriction.getRestrictionPermit() == Restriction.RestrictionPermit.ENABLED;
            }
        }
        return restriction.getRestrictionPermit() != Restriction.RestrictionPermit.ENABLED; //if enabled and none found, than all others are forbidden
    }

    private boolean isValid(List<GenericTraveler> travelers, Restriction restriction) {
        if (restriction.getMinValue() == null && restriction.getMaxValue() == null) {
            return true;
        }
        boolean isEnabled = restriction.getRestrictionPermit() == Restriction.RestrictionPermit.ENABLED;
        boolean isValidMinAge = true;
        boolean isValidMaxAge = true;
        for (GenericTraveler traveler : travelers) {
            if (restriction.getMinValue() != null) {
                if (restriction.getMinValue() > traveler.getAge()) {
                    isValidMinAge = false;
                }
            }
            if (restriction.getMaxValue() != null) {
                if (restriction.getMaxValue() < traveler.getAge()) {
                    isValidMaxAge = false;
                }
            }
        }
        return (isValidMinAge & isValidMaxAge) == isEnabled;
    }

    private boolean isValid(BigDecimal price, Restriction restriction) {
        if (restriction.getMinValue() == null && restriction.getMaxValue() == null) {
            return true;
        }
        boolean isEnabled = restriction.getRestrictionPermit() == Restriction.RestrictionPermit.ENABLED;
        boolean isValidMinPrice = true;
        boolean isValidMaxPrice = true;
        if (restriction.getMinValue() != null) {
            BigDecimal restrictionMinPrice = new BigDecimal(restriction.getMinValue());
            if (restrictionMinPrice.compareTo(price) > 0) {
                isValidMinPrice = false;
            }
        }
        if (restriction.getMaxValue() != null) {
            BigDecimal restrictionMaxPrice = new BigDecimal(restriction.getMaxValue());
            if (restrictionMaxPrice.compareTo(price) < 0) {
                isValidMaxPrice = false;
            }
        }
        return (isValidMaxPrice & isValidMinPrice) == isEnabled;
    }

    private boolean isValid(LocalDate departDate, LocalDate returnDate, Restriction restriction) {
        if (restriction.getMinValue() == null && restriction.getMaxValue() == null) {
            return true;
        }
        int lengthOfTravel = (int) ChronoUnit.DAYS.between(departDate, returnDate) + 1;
        boolean isEnabled = restriction.getRestrictionPermit() == Restriction.RestrictionPermit.ENABLED;
        boolean isValidMinLength = true;
        boolean isValidMaxLength = true;
        if (restriction.getMinValue() != null) {
            if (restriction.getMinValue() > lengthOfTravel) {
                isValidMinLength = false;
            }
        }
        if (restriction.getMaxValue() != null) {
            if (restriction.getMaxValue() < lengthOfTravel) {
                isValidMaxLength = false;
            }
        }
        return (isValidMaxLength & isValidMinLength) == isEnabled;
    }

}
