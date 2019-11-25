package com.travelinsurancemaster.model.dto;

import com.travelinsurancemaster.model.PaymentOption;
import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
public class Company {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String taxId;

    @Column
    private String bankName;

    @Column
    private String bankRouting;

    @Column
    private String account;

    @Column
    private String paypalEmailAddress;

    @Column
    private PaymentOption paymentOption;

    @Column
    private String website;

    @Column
    private Date deleted;

    @Column
    private String deletedBy;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @Transient
    private User companyManager;

    @Transient
    private boolean canDelete;

    @Column
    @Enumerated(EnumType.STRING)
    private PercentType percentType = PercentType.NONE;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "company_percent_info", joinColumns = @JoinColumn(name = "company_id"))
    private List<PercentInfo> percentInfo = new ArrayList<>();

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTaxId() {
        return taxId;
    }
    public void setTaxId(String taxId) {
        this.taxId = taxId;
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

    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }

    public Date getDeleted() { return deleted; }
    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public String getDeletedBy() {
        return deletedBy;
    }
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public PercentType getPercentType() {
        return percentType;
    }
    public void setPercentType(PercentType percentType) {
        this.percentType = percentType;
    }

    public List<PercentInfo> getPercentInfo() {
        return percentInfo;
    }
    public void setPercentInfo(List<PercentInfo> percentInfo) {
        this.percentInfo = percentInfo;
    }

    public User getCompanyManager() {
        Optional<User> companyManagerOptional = users.stream().filter(u -> u.getRoles().contains(Role.ROLE_COMPANY_MANAGER)).findFirst();
        return companyManagerOptional.isPresent() ? companyManagerOptional.get() : null;
    }

    public boolean isCanDelete() {
        return users.isEmpty();
    }

    public String getUserNameList() {
        return users.stream().map(u -> u.getFullName()).collect(Collectors.joining(", "));
    }
}