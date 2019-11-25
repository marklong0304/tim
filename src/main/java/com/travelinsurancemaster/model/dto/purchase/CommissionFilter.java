package com.travelinsurancemaster.model.dto.purchase;

import com.travelinsurancemaster.model.CommissionState;
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

/**
 * Created by Chernov Artur on 09.09.15.
 */
public class CommissionFilter extends AbstractDataTableFilter {
    private List<User> affiliates = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private boolean noAffiliate;
    private PurchaseTravelerJson traveler;
    private List<Vendor> vendors = new ArrayList<>();
    private List<PolicyMeta> policies = new ArrayList<>();
    private DateRange purchaseDate;
    private DateRange departDate;
    private String policyNumber;
    private BigDecimalRange expectedCommission;
    private BigDecimalRange totalPrice;
    private CommissionState confirm;
    private String checkNumber;
    private BigDecimalRange receivedCommission;
    private DateRange receivedDate;
    private String note;
    private Boolean cancellation;

    public CommissionFilter() {
    }

    public List<PolicyMeta> getPolicies() { return policies; }
    public void setPolicies(List<PolicyMeta> policies) { this.policies = policies; }

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

    public BigDecimalRange getExpectedCommission() {
        return expectedCommission;
    }
    public void setExpectedCommission(BigDecimalRange expectedCommission) {
        this.expectedCommission = expectedCommission;
    }
    public void setExpectedCommission(BigDecimal from, BigDecimal to) {
        this.expectedCommission = new BigDecimalRange(from, to);
    }

    public CommissionState getConfirm() {
        return confirm;
    }
    public void setConfirm(CommissionState confirm) {
        this.confirm = confirm;
    }

    public String getCheckNumber() {
        return checkNumber;
    }
    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public BigDecimalRange getReceivedCommission() {
        return receivedCommission;
    }
    public void setReceivedCommission(BigDecimalRange receivedCommission) {
        this.receivedCommission = receivedCommission;
    }

    public void setReceivedCommission(BigDecimal from, BigDecimal to) {
        this.receivedCommission = new BigDecimalRange(from, to);
    }
    public DateRange getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(DateRange receivedDate) {
        this.receivedDate = receivedDate;
    }
    public void setReceivedDate(Date from, Date to) {
        this.receivedDate = new DateRange(DateUtil.getStartOfDay(from), DateUtil.getEndOfDay(to));
    }

    public Boolean isCancellation() {
        return cancellation;
    }
    public void setCancellation(Boolean cancellation) {
        this.cancellation = cancellation;
    }

    public boolean isNoAffiliate() { return noAffiliate; }
    public void setNoAffiliate(boolean noAffiliate) { this.noAffiliate = noAffiliate; }

    public BigDecimalRange getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimalRange totalPrice) {
        this.totalPrice = totalPrice;
    }
    public void setTotalPrice(BigDecimal from, BigDecimal to) {
        this.totalPrice = new BigDecimalRange(from, to);
    }
}