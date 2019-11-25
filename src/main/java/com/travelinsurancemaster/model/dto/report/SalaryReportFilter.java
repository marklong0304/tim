package com.travelinsurancemaster.model.dto.report;

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
 * Created by Chernov Artur on 29.09.15.
 */
public class SalaryReportFilter extends AbstractDataTableFilter {
    private static final long serialVersionUID = -821158637909975870L;

    private List<User> affiliates = new ArrayList<>();
    private PurchaseTravelerJson traveler;
    private DateRange purchaseDate;
    private List<Vendor> vendors = new ArrayList<>();
    private List<PolicyMeta> policies = new ArrayList<>();
    private String policyNumber;
    private BigDecimalRange totalPrice;
    private BigDecimalRange expectedSalary;
    private BigDecimalRange salary;
    private Boolean pay;
    private String note;
    private Boolean cancellation;
    private DateRange payDate;

    public SalaryReportFilter() {
    }

    public DateRange getPayDate() {
        return payDate;
    }

    public void setPayDate(DateRange payDate) {
        this.payDate = payDate;
    }

    public void setPayDate(Date from, Date to) {
        this.payDate = new DateRange(DateUtil.getStartOfDay(from), DateUtil.getEndOfDay(to));
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

    public BigDecimalRange getExpectedSalary() {
        return expectedSalary;
    }

    public void setExpectedSalary(BigDecimalRange expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public void setExpectedSalary(BigDecimal from, BigDecimal to) {
        this.expectedSalary = new BigDecimalRange(from, to);
    }

    public List<User> getAffiliates() {
        return affiliates;
    }

    public void setAffiliates(List<User> affiliates) {
        this.affiliates = affiliates;
    }

    public List<Vendor> getVendors() {
        return vendors;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
    }

    public BigDecimalRange getSalary() {
        return salary;
    }

    public void setSalary(BigDecimalRange salary) {
        this.salary = salary;
    }

    public void setSalary(BigDecimal from, BigDecimal to) {
        this.salary = new BigDecimalRange(from, to);
    }

    public Boolean isPay() {
        return pay;
    }

    public void setPay(Boolean pay) {
        this.pay = pay;
    }

    public Boolean isCancellation() {
        return cancellation;
    }

    public void setCancellation(Boolean cancellation) {
        this.cancellation = cancellation;
    }

    public List<PolicyMeta> getPolicies() {
        return policies;
    }

    public void setPolicies(List<PolicyMeta> policies) {
        this.policies = policies;
    }

    public BigDecimalRange getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimalRange totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTotalPrice(BigDecimal from, BigDecimal to) {
        this.totalPrice = new BigDecimalRange(from, to);
    }

    public PurchaseTravelerJson getTraveler() {
        return traveler;
    }

    public void setTraveler(PurchaseTravelerJson traveler) {
        this.traveler = traveler;
    }
}
