package com.travelinsurancemaster.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Alexander.Isaenco
 */
@Entity
@ToString
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyMetaCategory implements Serializable, Comparable<PolicyMetaCategory> {
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

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MetaParamType type = MetaParamType.SIMPLE;

    @ManyToOne(optional = false)
    private PolicyMeta policyMeta;

    @ManyToOne
    private Category category;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMetaCategory", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PolicyMetaCategoryValue> values = new ArrayList<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMetaCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyMetaCategoryRestriction> policyMetaCategoryRestrictions = new ArrayList<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMetaCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyMetaPackageValue> policyMetaPackageValues = new ArrayList<>();

    @Transient
    private List<PolicyMetaCategoryValue> valuesAfterConditions = new ArrayList<>();

    @Transient
    private boolean sortedValues = false;

    public PolicyMetaCategory() {
    }

    public PolicyMetaCategory(String description, MetaParamType type, PolicyMeta policyMeta, Category category) {
        this.description = description;
        this.type = type;
        this.policyMeta = policyMeta;
        this.category = category;
    }

    public List<PolicyMetaCategoryValue> getValues() {
        if (!sortedValues) {
            Collections.sort(values);
            sortedValues = true;
        }
        return values;
    }

    @Override
    public int compareTo(PolicyMetaCategory policyMetaCategory) {
        if ((CollectionUtils.isEmpty(this.getPolicyMetaCategoryRestrictions()) &&
                CollectionUtils.isEmpty(policyMetaCategory.getPolicyMetaCategoryRestrictions())) ||
                (CollectionUtils.isNotEmpty(this.getPolicyMetaCategoryRestrictions()) &&
                        CollectionUtils.isNotEmpty(policyMetaCategory.getPolicyMetaCategoryRestrictions()))) {
            if (this.getId() > policyMetaCategory.getId()) {
                return -1;
            } else {
                return 1;
            }
        } else if (CollectionUtils.isEmpty(policyMetaCategory.getPolicyMetaCategoryRestrictions())) {
            return -1;
        } else {
            return 1;
        }
    }

    public enum MetaParamType {
        SIMPLE, UP_SALE
    }

    public String getMinCostValue(BigDecimal minTripCost) {
        if (!values.isEmpty()) {
            if (getCategory().getValueType() == ValueType.NAN) {
                return values.get(0).getCaption();
            } else {
                return "$" + values.get(0).getValueByType(ValueType.FIX, minTripCost);
            }
        }
        return "";
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MetaParamType getType() {
        return type;
    }

    public void setType(MetaParamType type) {
        this.type = type;
    }

    public PolicyMeta getPolicyMeta() {
        return policyMeta;
    }

    public void setPolicyMeta(PolicyMeta policyMeta) {
        this.policyMeta = policyMeta;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setValues(List<PolicyMetaCategoryValue> values) {
        this.values = values;
    }

    public List<PolicyMetaCategoryRestriction> getPolicyMetaCategoryRestrictions() {
        return policyMetaCategoryRestrictions;
    }

    public void setPolicyMetaCategoryRestrictions(List<PolicyMetaCategoryRestriction> policyMetaCategoryRestrictions) {
        this.policyMetaCategoryRestrictions = policyMetaCategoryRestrictions;
    }

    public List<PolicyMetaPackageValue> getPolicyMetaPackageValues() {
        return policyMetaPackageValues;
    }

    public void setPolicyMetaPackageValues(List<PolicyMetaPackageValue> policyMetaPackageValues) {
        this.policyMetaPackageValues = policyMetaPackageValues;
    }

    public List<PolicyMetaCategoryValue> getValuesAfterConditions() {
        return valuesAfterConditions;
    }

    public void setValuesAfterConditions(List<PolicyMetaCategoryValue> valuesAfterConditions) {
        this.valuesAfterConditions = valuesAfterConditions;
    }

    public boolean isSortedValues() {
        return sortedValues;
    }

    public void setSortedValues(boolean sortedValues) {
        this.sortedValues = sortedValues;
    }
}
