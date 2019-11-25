package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.dto.json.datatable.salary.report.SalaryCorrectionJson;
import com.travelinsurancemaster.model.security.User;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class SalaryCorrection implements Serializable, IAffiliateSinglePayment {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private Long id;

    @NotNull(message = "Salary is required!")
    @Column(nullable = false)
    private BigDecimal salaryToPay;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column
    private Date paid;

    @Column
    private String note;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private User affiliate;

    @NotNull(message = "Correction date is required!")
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column(nullable = false)
    private Date receivedDate;

    @Column
    private BigDecimal salaryPaid;

    @ManyToOne
    private AffiliatePayment affiliatePayment;

    @Transient
    private boolean canDelete;

    public SalaryCorrection(User affiliateUser) {
        this.affiliate = affiliateUser;
        this.setReceivedDate(new Date());
    }

    public SalaryCorrection(SalaryCorrectionJson salaryCorrectionJson, User affiliate) {
        this.salaryToPay = new BigDecimal(salaryCorrectionJson.getSalaryToPay());
        this.paid = null;
        this.note = salaryCorrectionJson.getNote();
        this.affiliate = affiliate;
        this.receivedDate = new Date();
    }

    public SalaryCorrection() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSalaryToPay() {
        return salaryToPay;
    }
    public void setSalaryToPay(BigDecimal salaryToPay) {
        this.salaryToPay = salaryToPay;
    }

    public Date getPaid() {
        return paid;
    }
    public void setPaid(Date paid) {
        this.paid = paid;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public User getAffiliate() {
        return affiliate;
    }
    public void setAffiliate(User affiliate) {
        this.affiliate = affiliate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }
    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public BigDecimal getSalaryPaid() { return salaryPaid; }
    public void setSalaryPaid(BigDecimal salaryPaid) { this.salaryPaid = salaryPaid; }

    public AffiliatePayment getAffiliatePayment() {
        return affiliatePayment;
    }
    public void setAffiliatePayment(AffiliatePayment affiliatePayment) {
        this.affiliatePayment = affiliatePayment;
    }

    public boolean getCanDelete() {
        if(affiliatePayment == null) return true;
        return affiliatePayment.getStatusPaid() == null;
    }
}