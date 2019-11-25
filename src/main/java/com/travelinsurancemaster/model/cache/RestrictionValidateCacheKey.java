package com.travelinsurancemaster.model.cache;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.Restriction;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Data
public class RestrictionValidateCacheKey<T extends Restriction> implements Serializable {
    private static final long serialVersionUID = -1L;

    private List<Integer> travelersAge = new ArrayList<>();
    private LocalDate departDate;
    private LocalDate returnDate;
    private BigDecimal tripCost;
    private BigDecimal tripCostPerTraveler;
    private CountryCode residentCountry;
    private StateCode residentState;
    private CountryCode citizenCountry;
    private CountryCode destinationCountry;
    private LocalDate depositDate;
    private LocalDate paymentDate;
    private Restriction.RestrictionPermit restrictionPermit;
    private Restriction.RestrictionType restrictionType;
    private Set<CountryCode> countries;
    private Set<StateCode> states;
    private Integer minValue;
    private Integer maxValue;
    private String calculatedRestrictions;

    public RestrictionValidateCacheKey(QuoteRequest request, T restriction) {
        if (!CollectionUtils.isEmpty(request.getTravelers())) {
            for (GenericTraveler traveler : request.getTravelers()) {
                this.travelersAge.add(traveler.getAge());
            }
        }
        Collections.sort(this.travelersAge);
        this.departDate = request.getDepartDate();
        this.returnDate = request.getReturnDate();
        this.tripCost = request.getTripCost();
        this.residentCountry = request.getResidentCountry();
        this.residentState = request.getResidentState();
        this.citizenCountry = request.getCitizenCountry();
        this.destinationCountry = request.getDestinationCountry();
        this.depositDate = request.getDepositDate();
        this.paymentDate = request.getPaymentDate();

        this.restrictionType = restriction.getRestrictionType();
        this.restrictionPermit = restriction.getRestrictionPermit();
        this.countries = restriction.getCountries();
        this.states = restriction.getStates();
        this.minValue = restriction.getMinValue();
        this.maxValue = restriction.getMaxValue();
        this.calculatedRestrictions = restriction.getCalculatedRestrictions();
    }
}
