package com.travelinsurancemaster.model.dto.report;

import com.travelinsurancemaster.model.PaymentCancellationType;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.datatable.AbstractDataTableFilter;
import com.travelinsurancemaster.model.dto.json.datatable.purchase.PurchaseTravelerJson;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.util.BigDecimalRange;
import com.travelinsurancemaster.model.util.DateRange;
import com.travelinsurancemaster.util.DateUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentReportFilter extends AbstractDataTableFilter {
    private List<User> affiliates = new ArrayList<>();
    private boolean noAffiliate;
    private List<User> users = new ArrayList<>();
    private List<Vendor> vendors = new ArrayList<>();
    private List<PolicyMeta> policies = new ArrayList<>();
    private DateRange departDate;
    private DateRange purchaseDate;
    private BigDecimalRange tripCost;
    private BigDecimalRange policyPrice;
    private String policyNumber;
    private String note;
    private PurchaseTravelerJson traveler;
    private PaymentCancellationType cancellation;

    public PaymentReportFilter() {
    }

    public List<User> getAffiliates() {
        return affiliates;
    }

    public void setAffiliates(List<User> affiliates) {
        this.affiliates = affiliates;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Vendor> getVendors() {
        return vendors;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
    }

    public List<PolicyMeta> getPolicies() {
        return policies;
    }

    public void setPolicies(List<PolicyMeta> policies) {
        this.policies = policies;
    }

    public DateRange getDepartDate() {
        return departDate;
    }

    public void setDepartDate(Date from, Date to) {
        this.departDate = new DateRange(DateUtil.getStartOfDay(from), DateUtil.getEndOfDay(to));
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

    public void setPurchaseDate(Date from, Date to) {
        this.purchaseDate = new DateRange(DateUtil.getStartOfDay(from), DateUtil.getEndOfDay(to));
    }

    public BigDecimalRange getTripCost() {
        return tripCost;
    }

    public void setTripCost(BigDecimalRange tripCost) {
        this.tripCost = tripCost;
    }

    public void setTripCost(BigDecimal from, BigDecimal to) {
        this.tripCost = new BigDecimalRange(from, to);
    }

    public BigDecimalRange getPolicyPrice() {
        return policyPrice;
    }

    public void setPolicyPrice(BigDecimalRange policyPrice) {
        this.policyPrice = policyPrice;
    }

    public void setPolicyPrice(BigDecimal from, BigDecimal to) {
        this.policyPrice = new BigDecimalRange(from, to);
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

    public PaymentCancellationType getCancellation() {
        return cancellation;
    }

    public void setCancellation(PaymentCancellationType cancellation) {
        this.cancellation = cancellation;
    }

    public boolean isNoAffiliate() { return noAffiliate; }

    public void setNoAffiliate(boolean noAffiliate) { this.noAffiliate = noAffiliate; }
}
