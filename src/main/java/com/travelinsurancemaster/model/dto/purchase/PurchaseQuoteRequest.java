package com.travelinsurancemaster.model.dto.purchase;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 19.08.15.
 */

@Embeddable
public class PurchaseQuoteRequest implements Serializable {
    private static final long serialVersionUID = -1021983400469024541L;

    @NotNull(message = "Destination country is required!")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CountryCode destinationCountry;

    @NotNull(message = "Depart date is required!")
    @Column(nullable = false)
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDate departDate;

    @NotNull(message = "Return date is required!")
    @Column(nullable = false)
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDate returnDate;

    @NotNull(message = "Trip cost is required!")
    @Column(nullable = false)
    private BigDecimal tripCost;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "purchase_traveler", joinColumns = @JoinColumn(name = "purchase_id"))
    private List<PurchaseTraveler> travelers = new ArrayList<>();

    @Column
    @Embedded
    private PurchaseTraveler primaryTraveler;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CountryCode residentCountry;

    @Column
    @Enumerated(EnumType.STRING)
    private StateCode residentState;

    @NotNull(message = "Citizenship is required!")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CountryCode citizenCountry;

    @Column
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDate depositDate;

    @Column
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDate paymentDate;

    public PurchaseQuoteRequest() {
    }

    public PurchaseQuoteRequest(QuoteRequest quoteRequest) {
        this.destinationCountry = quoteRequest.getDestinationCountry();
        this.departDate = quoteRequest.getDepartDate();
        this.returnDate = quoteRequest.getReturnDate();
        this.tripCost = quoteRequest.getTripCost();
        for (GenericTraveler traveler : quoteRequest.getTravelers()) {
            this.travelers.add(new PurchaseTraveler(traveler));
            if (traveler.isPrimary()) {
                this.primaryTraveler = new PurchaseTraveler(traveler);
            }
        }
        this.residentCountry = quoteRequest.getResidentCountry();
        this.residentState = quoteRequest.getResidentState();
        this.citizenCountry = quoteRequest.getCitizenCountry();
        this.depositDate = quoteRequest.getDepositDate();
        this.paymentDate = quoteRequest.getPaymentDate();
    }

    public PurchaseQuoteRequest(PurchaseQuoteRequest purchaseQuoteRequest) {
        this.destinationCountry = purchaseQuoteRequest.getDestinationCountry();
        this.departDate = purchaseQuoteRequest.getDepartDate();
        this.returnDate = purchaseQuoteRequest.getReturnDate();
        this.tripCost = purchaseQuoteRequest.getTripCost();
        this.travelers = purchaseQuoteRequest.getTravelers().stream().map(PurchaseTraveler::clone).collect(Collectors.toList());
        this.primaryTraveler = purchaseQuoteRequest.getPrimaryTraveler();
        this.residentCountry = purchaseQuoteRequest.getResidentCountry();
        this.residentState = purchaseQuoteRequest.getResidentState();
        this.citizenCountry = purchaseQuoteRequest.getCitizenCountry();
        this.depositDate = purchaseQuoteRequest.getDepositDate();
        this.paymentDate = purchaseQuoteRequest.getPaymentDate();
    }

    public List<PurchaseTraveler> getTravelers() {
        return travelers;
    }

    public void setTravelers(List<PurchaseTraveler> travelers) {
        this.travelers = travelers;
    }

    public LocalDate getDepartDate() {
        return departDate;
    }

    public void setDepartDate(LocalDate departDate) {
        this.departDate = departDate;
    }

    public BigDecimal getTripCost() {
        return tripCost;
    }

    public void setTripCost(BigDecimal tripCost) {
        this.tripCost = tripCost;
    }

    public PurchaseTraveler getPrimaryTraveler() {
        return primaryTraveler;
    }

    public void setPrimaryTraveler(PurchaseTraveler primaryTraveler) {
        this.primaryTraveler = primaryTraveler;
    }

    public CountryCode getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(CountryCode destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public CountryCode getResidentCountry() {
        return residentCountry;
    }

    public void setResidentCountry(CountryCode residentCountry) {
        this.residentCountry = residentCountry;
    }

    public StateCode getResidentState() {
        return residentState;
    }

    public void setResidentState(StateCode residentState) {
        this.residentState = residentState;
    }

    public CountryCode getCitizenCountry() {
        return citizenCountry;
    }

    public void setCitizenCountry(CountryCode citizenCountry) {
        this.citizenCountry = citizenCountry;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(LocalDate depositDate) {
        this.depositDate = depositDate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
