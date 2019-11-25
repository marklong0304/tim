package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ritchie on 2/10/15.
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyMeta implements Serializable {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @CreationTimestamp
    private Date created;

    @Column
    @UpdateTimestamp
    private Date modified;

    @Column(nullable = false)
    @NotBlank(message = "Policy display name is required")
    private String displayName;

    @Column(nullable = false)
    @NotBlank
    private String uniqueCode;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean purchasable;

    @Column(nullable = false)
    private boolean supportsZeroCancellation;

    @Column
    private BigDecimal minimalTripCost;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column
    private Date deletedDate;

    @Column
    private boolean showOnQuotes;

    @ManyToOne(optional = false)
    private Vendor vendor;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMeta", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PolicyQuoteParam> policyQuoteParams = new ArrayList<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMeta", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PolicyMetaRestriction> policyMetaRestrictions = new ArrayList<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMeta", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PolicyMetaCategory> policyMetaCategories = new ArrayList<>();

    @JsonIgnore
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMeta", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<PolicyMetaCode> policyMetaCodes = new ArrayList<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMeta", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<PolicyMetaPackage> policyMetaPackages = new ArrayList<>();

    @Column
    @Enumerated(EnumType.STRING)
    private PercentType percentType = PercentType.NONE;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "policy_meta_percent_info", joinColumns = @JoinColumn(name = "policy_meta_id"))
    private List<PercentInfo> percentInfo = new ArrayList<>();

    @Deprecated // deposit date is mandatory field from Release 6
    @Column(nullable = false)
    private boolean requiredDepositDate = true;

    @OneToOne(fetch = FetchType.LAZY)
    private PolicyMetaPage policyMetaPage;

    @Deprecated
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Column(nullable = false, name = "plan_type")
    @ElementCollection(targetClass = PlanType.class, fetch = FetchType.LAZY)
    @JoinTable(name = "policy_meta_plan_type", joinColumns = {@JoinColumn(name = "policy_meta_id")})
    @Enumerated(EnumType.STRING)
    private Set<PlanType> planTypes = new HashSet<>();

    public PolicyMeta() {
    }

    public PolicyMeta(String displayName, boolean active, boolean purchasable, Vendor vendor, String uniqueCode,
                      boolean supportsZeroCancellation, boolean requiredDepositDate) {
        this.displayName = displayName;
        this.active = active;
        this.purchasable = purchasable;
        this.vendor = vendor;
        this.uniqueCode = uniqueCode;
        this.supportsZeroCancellation = supportsZeroCancellation;
        this.requiredDepositDate = requiredDepositDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() { return created; }

    public void setCreated(Date created) { this.created = created; }

    public Date getModified() { return modified; }

    public void setModified(Date modified) { this.modified = modified; }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPurchasable() {
        return purchasable;
    }

    public void setPurchasable(boolean purchasable) {
        this.purchasable = purchasable;
    }

    public boolean isSupportsZeroCancellation() {
        return supportsZeroCancellation;
    }

    public void setSupportsZeroCancellation(boolean supportsZeroCancellation) {
        this.supportsZeroCancellation = supportsZeroCancellation;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public List<PolicyQuoteParam> getPolicyQuoteParams() {
        return policyQuoteParams;
    }

    public void setPolicyQuoteParams(List<PolicyQuoteParam> policyQuoteParams) {
        this.policyQuoteParams = policyQuoteParams;
    }

    public List<PolicyMetaRestriction> getPolicyMetaRestrictions() {
        return policyMetaRestrictions;
    }

    public void setPolicyMetaRestrictions(List<PolicyMetaRestriction> policyMetaRestrictions) {
        this.policyMetaRestrictions = policyMetaRestrictions;
    }

    public List<PolicyMetaCategory> getPolicyMetaCategories() {
        return policyMetaCategories;
    }

    public void setPolicyMetaCategories(List<PolicyMetaCategory> policyMetaCategories) {
        this.policyMetaCategories = policyMetaCategories;
    }

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

    public BigDecimal getMinimalTripCost() {
        return minimalTripCost;
    }

    public boolean hasMinimalTripCost(){
        return Objects.nonNull(minimalTripCost );
    }

    public void setMinimalTripCost(BigDecimal minimalTripCost) {
        this.minimalTripCost = minimalTripCost;
    }

    public Date getDeletedDate() { return deletedDate; }

    public void setDeletedDate(Date deletedDate) { this.deletedDate = deletedDate; }

    public boolean isShowOnQuotes() {
        return showOnQuotes;
    }

    public void setShowOnQuotes(boolean showOnQuotes) {
        this.showOnQuotes = showOnQuotes;
    }

    @Deprecated
    public boolean isRequiredDepositDate() {
        return requiredDepositDate;
    }

    @Deprecated
    public void setRequiredDepositDate(boolean requiredDepositDate) {
        this.requiredDepositDate = requiredDepositDate;
    }

    public PolicyMetaPage getPolicyMetaPage() {
        return policyMetaPage;
    }

    public void setPolicyMetaPage(PolicyMetaPage policyMetaPage) {
        this.policyMetaPage = policyMetaPage;
    }

    public List<PolicyMetaCode> getPolicyMetaCodes() {
        return policyMetaCodes;
    }

    public void setPolicyMetaCodes(List<PolicyMetaCode> policyMetaCodes) {
        this.policyMetaCodes = policyMetaCodes;
    }

    public List<PolicyMetaPackage> getPolicyMetaPackages() {
        return policyMetaPackages;
    }

    public void setPolicyMetaPackages(List<PolicyMetaPackage> policyMetaPackages) {
        this.policyMetaPackages = policyMetaPackages;
    }

    public PolicyMetaCategory getCategoryValue(String code) {
        return policyMetaCategories.stream().filter(
                policyMetaCategory -> policyMetaCategory.getCategory().getCode().equals(code)
        ).findFirst().orElse(null);
    }

    public long getCategoryValuesCount(String code) {
        return policyMetaCategories.stream().filter(policyMetaCategory -> policyMetaCategory.getCategory().getCode().equals(code)).count();
    }

    public List<PolicyMetaRestriction> getRestrictions(PolicyMetaRestriction.RestrictionType restrictionType) {
        return policyMetaRestrictions.stream().filter(
                r -> r.getRestrictionType() == restrictionType
        ).collect(Collectors.toList());
    }

    @Deprecated
    public Set<PlanType> getPlanTypes() {
        return planTypes;
    }

    @Deprecated
    public void setPlanTypes(Set<PlanType> planTypes) {
        this.planTypes = planTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PolicyMeta that = (PolicyMeta) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(uniqueCode, that.uniqueCode)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(uniqueCode)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "PolicyMeta{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", uniqueCode='" + uniqueCode + '\'' +
                ", active=" + active +
                ", purchasable=" + purchasable +
                ", supportsZeroCancellation=" + supportsZeroCancellation +
                ", minimalTripCost=" + minimalTripCost +
                ", deletedDate=" + deletedDate +
                ", vendor=" + vendor +
                '}';
    }
}
