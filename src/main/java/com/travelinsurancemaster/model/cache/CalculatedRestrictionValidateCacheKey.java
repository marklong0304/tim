package com.travelinsurancemaster.model.cache;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class CalculatedRestrictionValidateCacheKey implements Serializable {
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
    private String calculatedRestrictions;
    private boolean isScriptValidation;

    public CalculatedRestrictionValidateCacheKey(QuoteRequest request, String calculatedRestrictions, boolean isScriptValidation) {
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
        this.calculatedRestrictions = calculatedRestrictions;
        this.isScriptValidation = isScriptValidation;
    }
}
