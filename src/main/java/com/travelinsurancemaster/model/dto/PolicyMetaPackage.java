package com.travelinsurancemaster.model.dto;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Alexander.Isaenco
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyMetaPackage implements Serializable {
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
    private String code;

    @Column(nullable = false)
    private String name;

    @Column
    private BigDecimal fixedCost;

    @ManyToOne(optional = false)
    private PolicyMeta policyMeta;

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMetaPackage", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyMetaPackageValue> policyMetaPackageValues = new ArrayList<>();

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMetaPackage", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyMetaPackageRestriction> policyMetaPackageRestrictions = new ArrayList<>();

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getFixedCost() {
		return fixedCost;
	}

    public void setFixedCost(BigDecimal fixedCost) {
		this.fixedCost = fixedCost;
	}

    public PolicyMeta getPolicyMeta() {
        return policyMeta;
    }

    public void setPolicyMeta(PolicyMeta policyMeta) {
        this.policyMeta = policyMeta;
    }

    public List<PolicyMetaPackageValue> getPolicyMetaPackageValues() {
        return policyMetaPackageValues;
    }

    public void setPolicyMetaPackageValues(List<PolicyMetaPackageValue> policyMetaPackageValues) {
        this.policyMetaPackageValues = policyMetaPackageValues;
    }

    public List<PolicyMetaPackageRestriction> getPolicyMetaPackageRestrictions() {
        return policyMetaPackageRestrictions;
    }

    public void setPolicyMetaPackageRestrictions(List<PolicyMetaPackageRestriction> policyMetaPackageRestrictions) {
        this.policyMetaPackageRestrictions = policyMetaPackageRestrictions;
    }
}
