package com.travelinsurancemaster.model.webservice.common.forms;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Chernov Artur on 26.04.15.
 */

public class Step1QuoteRequestForm implements Serializable {
    static final long serialVersionUID = 1L;

    @NotNull
    private CountryCode destinationCountry;

    private StateCode destinationState;

    private Long timezoneOffset;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate departDate;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate returnDate;

    @NotNull
    private BigDecimal tripCost;

    @NotNull
    private boolean tripCostTotal = true;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @NotNull
    public CountryCode getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(@NotNull CountryCode destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public StateCode getDestinationState() {
        return destinationState;
    }

    public void setDestinationState(StateCode destinationState) {
        this.destinationState = destinationState;
    }

    public Long getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(Long timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    @NotNull
    public LocalDate getDepartDate() {
        return departDate;
    }

    public void setDepartDate(@NotNull LocalDate departDate) {
        this.departDate = departDate;
    }

    @NotNull
    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(@NotNull LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @NotNull
    public BigDecimal getTripCost() {
        return tripCost;
    }

    public void setTripCost(@NotNull BigDecimal tripCost) {
        this.tripCost = tripCost;
    }

    public boolean isTripCostTotal() {
        return tripCostTotal;
    }

    public void setTripCostTotal(boolean tripCostTotal) {
        this.tripCostTotal = tripCostTotal;
    }
}
