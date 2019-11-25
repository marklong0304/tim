package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.User;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ritchie on 5/14/15.
 */
@Entity
public class AffiliateCommission implements Serializable, IAffiliateSinglePayment {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(nullable = false)
    private BigDecimal salary = new BigDecimal(0);

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column
    private Date paid;

    @Column
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private User affiliate;

    @NotNull
    @Column
    private String affiliateCommValue = "0";

    @NotNull
    @Column
    private BigDecimal salaryToPay = new BigDecimal(0);

    @Column
    private BigDecimal salaryPaid = new BigDecimal(0);

    @Column
    @Enumerated(EnumType.STRING)
    private CommissionValueType valueType = CommissionValueType.FIX;

    @ManyToOne
    private AffiliatePayment affiliatePayment;

    @OneToOne(mappedBy = "affiliateCommission")
    private Purchase purchase;

    public AffiliateCommission() {
    }
    
    public AffiliateCommission(AffiliateCommission affiliateCommission, boolean full) {
        this.affiliate = affiliateCommission.getAffiliate();
        if(full) {
            this.salary = affiliateCommission.getSalary();
            this.paid = affiliateCommission.getPaid();
            this.description = affiliateCommission.getDescription();
            this.affiliateCommValue = affiliateCommission.getAffiliateCommValue();
            this.valueType = affiliateCommission.getValueType();
            this.salary = affiliateCommission.getSalary();
            this.salaryToPay = affiliateCommission.getSalaryToPay();
            this.salaryPaid = affiliateCommission.getSalaryPaid();
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSalary() {
        return salary;
    }
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Date getPaid() {
        return paid;
    }
    public void setPaid(Date paid) {
        this.paid = paid;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public User getAffiliate() {
        return affiliate;
    }
    public void setAffiliate(User affiliate) {
        this.affiliate = affiliate;
    }

    public CommissionValueType getValueType() {
        return valueType;
    }
    public void setValueType(CommissionValueType valueType) {
        this.valueType = valueType;
    }

    public String getAffiliateCommValue() {
        return affiliateCommValue;
    }
    public void setAffiliateCommValue(String affiliateCommValue) {
        this.affiliateCommValue = affiliateCommValue;
    }

    public BigDecimal getSalaryToPay() {
        return salaryToPay;
    }
    public void setSalaryToPay(BigDecimal salaryToPay) {
        this.salaryToPay = salaryToPay;
    }

    public BigDecimal getSalaryPaid() { return salaryPaid; }
    public void setSalaryPaid(BigDecimal salaryPaid) { this.salaryPaid = salaryPaid; }

    public AffiliatePayment getAffiliatePayment() {
        return affiliatePayment;
    }
    public void setAffiliatePayment(AffiliatePayment affiliatePayment) {
        this.affiliatePayment = affiliatePayment;
    }

    public Purchase getPurchase() { return purchase; }
    public void setPurchase(Purchase purchase) { this.purchase = purchase; }
}
