package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.model.webservice.common.BeneficiaryType;
import com.travelinsurancemaster.model.webservice.common.CardType;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alexander.Isaenco
 */

@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Vendor implements Serializable {

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
    private String name;

    @Column(nullable = false)
    private String code;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "vendor", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<PolicyMeta> policyMetaList = new ArrayList<>();

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column
    private byte[] icon;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column
    private Date iconLastModified;

    @Column
    @Enumerated(EnumType.STRING)
    private PercentType percentType = PercentType.NONE;

    @ElementCollection(fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(name = "vendor_percent_info", joinColumns = @JoinColumn(name = "vendor_id"))
    private List<PercentInfo> percentInfo = new ArrayList<>();

    @ElementCollection(targetClass = CardType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "unsupported_card_types", joinColumns = @JoinColumn(name = "vendor_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "unsupported_card_type")
    private Set<CardType> unsupportedCardTypes = new HashSet<>();

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean ageFromDepartDate = false;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "vendor")
    private VendorPage vendorPage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BeneficiaryType beneficiaryType = BeneficiaryType.SINGLE_BENEFICIARY;

    @Transient
    private boolean canDeleted = true;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column
    private Date deletedDate;

    @Column
    private boolean termsAndConditionsIsActive;

    @Column
    @Enumerated(EnumType.STRING)
    private TermsAndConditionsType termsAndConditionsType;

    @Column(nullable = false)
    private boolean test = false;

    @Column
    private String testUserIds;

    @Column(nullable = false)
    private boolean showPureConsumers = false;

    @Transient
    private List<Long> longTestUserIds;

    @Column(length = 10000)
    private String termsAndConditionsText;

    public Vendor() {
    }

    @JsonIgnore
    public String getLogoSrcRandom() {
        return String.format("/vendorLogo/get/%s.png?rand=%s", code,
              String.valueOf(new Random().nextLong()));
    }

    public byte[] getIcon() {
        return icon;
    }
    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public List<PolicyMeta> getPolicyMetaList() {
        return policyMetaList;
    }
    public void setPolicyMetaList(List<PolicyMeta> policyMetaList) {
        this.policyMetaList = policyMetaList;
    }

    public Date getIconLastModified() {
        return iconLastModified;
    }
    public void setIconLastModified(Date iconLastModified) {
        this.iconLastModified = iconLastModified;
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

    public Set<CardType> getUnsupportedCardTypes() {
        return unsupportedCardTypes;
    }
    public void setUnsupportedCardTypes(Set<CardType> unsupportedCardTypes) {
        this.unsupportedCardTypes = unsupportedCardTypes;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAgeFromDepartDate() {
        return ageFromDepartDate;
    }
    public void setAgeFromDepartDate(boolean ageFromDepartDate) {
        this.ageFromDepartDate = ageFromDepartDate;
    }

    public VendorPage getVendorPage() {
        return vendorPage;
    }
    public void setVendorPage(VendorPage vendorPage) {
        this.vendorPage = vendorPage;
    }

    public BeneficiaryType getBeneficiaryType() {
        return beneficiaryType;
    }
    public void setBeneficiaryType(BeneficiaryType beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }

    public boolean isCanDeleted() {
        return canDeleted;
    }
    public void setCanDeleted(boolean canDeleted) { this.canDeleted = canDeleted; }

    public Date getDeletedDate() { return deletedDate; }
    public void setDeletedDate(Date deletedDate) { this.deletedDate = deletedDate; }

    public boolean isTermsAndConditionsIsActive() { return termsAndConditionsIsActive; }
    public void setTermsAndConditionsIsActive(boolean termsAndConditionsIsActive) { this.termsAndConditionsIsActive = termsAndConditionsIsActive; }

    public TermsAndConditionsType getTermsAndConditionsType() { return termsAndConditionsType; }
    public void setTermsAndConditionsType(TermsAndConditionsType termsAndConditionsType) { this.termsAndConditionsType = termsAndConditionsType; }

    public String getTermsAndConditionsText() { return termsAndConditionsText; }
    public void setTermsAndConditionsText(String termsAndConditionsText) { this.termsAndConditionsText = termsAndConditionsText; }

    public boolean isTest() { return test; }
    public void setTest(boolean test) { this.test = test; }

    public String getTestUserIds() { return testUserIds; }
    public void setTestUserIds(String testUserIds) { this.testUserIds = testUserIds; }

    public List<Long> getLongTestUserIds() {
        longTestUserIds = new ArrayList<>();
        if(testUserIds != null && testUserIds.length() > 0) {
            longTestUserIds = Arrays.asList(testUserIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        }
        return longTestUserIds;
    }
    public void setLongTestUserIds(List<Long> longTestUserIds) {
        this.longTestUserIds = longTestUserIds;
        testUserIds = longTestUserIds.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public boolean isShowPureConsumers() { return showPureConsumers; }
    public void setShowPureConsumers(boolean showPureConsumers) { this.showPureConsumers = showPureConsumers; }
}