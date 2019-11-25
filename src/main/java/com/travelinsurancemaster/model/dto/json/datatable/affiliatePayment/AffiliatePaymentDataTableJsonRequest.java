package com.travelinsurancemaster.model.dto.json.datatable.affiliatePayment;

import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.purchase.PurchaseTravelerJson;
import com.travelinsurancemaster.model.util.BigDecimalRange;
import com.travelinsurancemaster.model.util.DateRange;

public class AffiliatePaymentDataTableJsonRequest extends AbstractDataTableJsonRequest {
    private static final long serialVersionUID = 7685924877738833011L;

    private DateRange departDate;
    private DateRange purchaseDate;
    private BigDecimalRange tripCost;
    private BigDecimalRange policyPrice;
    private String policyNumber;
    private String note;
    private PurchaseTravelerJson traveler;
    private DateRange paymentDate;
    private BigDecimalRange paymentTotal;

    public DateRange getDepartDate() {
        return departDate;
    }
    public void setDepartDate(DateRange departDate) {
        this.departDate = departDate;
    }

    public DateRange getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(DateRange purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimalRange getTripCost() {
        return tripCost;
    }
    public void setTripCost(BigDecimalRange tripCost) {
        this.tripCost = tripCost;
    }

    public BigDecimalRange getPolicyPrice() {
        return policyPrice;
    }
    public void setPolicyPrice(BigDecimalRange policyPrice) {
        this.policyPrice = policyPrice;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public PurchaseTravelerJson getTraveler() {
        return traveler;
    }
    public void setTraveler(PurchaseTravelerJson traveler) {
        this.traveler = traveler;
    }

    public DateRange getPaymentDate() { return paymentDate; }
    public void setPaymentDate(DateRange paymentDate) { this.paymentDate = paymentDate; }

    public BigDecimalRange getPaymentTotal() { return paymentTotal; }
    public void setPaymentTotal(BigDecimalRange paymentTotal) { this.paymentTotal = paymentTotal; }
}