package com.travelinsurancemaster.model.dto;

import com.travelinsurancemaster.model.PaymentOption;
import com.travelinsurancemaster.model.security.User;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raman on 6/24/19.
 */
@Entity
public class AffiliatePayment implements Serializable {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column
    private LocalDate paymentDate;

    @ManyToOne
    @JoinColumn(name = "affiliate_user_id")
    private User affiliateUser;

    @ManyToOne
    @JoinColumn(name = "affiliate_company_id")
    private Company affiliateCompany;

    @Column
    private PaymentOption paymentOption;

    @Column
    private String bankName;

    @Column
    private String bankRouting;

    @Column
    private String account;

    @Column
    private String paypalEmailAddress;

    @Column
    private String checkNumber;

    @Column
    private LocalDate statusPaid;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "affiliatePayment", fetch = FetchType.LAZY)
    private List<AffiliateCommission> affiliateCommissions = new ArrayList<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "affiliatePayment", fetch = FetchType.LAZY)
    private List<SalaryCorrection> salaryCorrections = new ArrayList<>();

    @Column
    private BigDecimal total;

    public AffiliatePayment() {}

    public AffiliatePayment(Company affiliateCompany) {
        this(
                affiliateCompany.getPaymentOption(),
                affiliateCompany.getBankName(),
                affiliateCompany.getBankRouting(),
                affiliateCompany.getAccount(),
                affiliateCompany.getPaypalEmailAddress()
        );
        this.affiliateCompany = affiliateCompany;
    }

    public AffiliatePayment(User affiliateUser) {
        this(
                affiliateUser.getUserInfo().getPaymentOption(),
                affiliateUser.getUserInfo().getBankName(),
                affiliateUser.getUserInfo().getBankRouting(),
                affiliateUser.getUserInfo().getAccount(),
                affiliateUser.getUserInfo().getPaypalEmailAddress()
        );
        this.affiliateUser = affiliateUser;
    }

    public AffiliatePayment(PaymentOption paymentOption, String bankName, String bankRouting, String account, String paypalEmailAddress) {
        this.paymentDate = LocalDate.now();
        this.paymentOption = paymentOption;
        this.bankName = bankName;
        this.bankRouting = bankRouting;
        this.account = account;
        this.paypalEmailAddress = paypalEmailAddress;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public User getAffiliateUser() {
        return affiliateUser;
    }
    public void setAffiliateUser(User affiliateUser) {
        this.affiliateUser = affiliateUser;
    }

    public Company getAffiliateCompany() {
        return affiliateCompany;
    }
    public void setAffiliateCompany(Company affiliateCompany) {
        this.affiliateCompany = affiliateCompany;
    }

    public PaymentOption getPaymentOption() {
        return paymentOption;
    }
    public void setPaymentOption(PaymentOption paymentOption) {
        this.paymentOption = paymentOption;
    }

    public String getBankName() {
        return bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankRouting() {
        return bankRouting;
    }
    public void setBankRouting(String bankRouting) {
        this.bankRouting = bankRouting;
    }

    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }

    public String getPaypalEmailAddress() {
        return paypalEmailAddress;
    }
    public void setPaypalEmailAddress(String paypalEmailAddress) {
        this.paypalEmailAddress = paypalEmailAddress;
    }

    public String getCheckNumber() {
        return checkNumber;
    }
    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public LocalDate getStatusPaid() { return statusPaid; }
    public void setStatusPaid(LocalDate statusPaid) { this.statusPaid = statusPaid; }

    public List<AffiliateCommission> getAffiliateCommissions() { return affiliateCommissions; }
    public void setAffiliateCommissions(List<AffiliateCommission> affiliateCommissions) { this.affiliateCommissions = affiliateCommissions; }

    public List<SalaryCorrection> getSalaryCorrections() { return salaryCorrections; }
    public void setSalaryCorrections(List<SalaryCorrection> salaryCorrections) { this.salaryCorrections = salaryCorrections; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}