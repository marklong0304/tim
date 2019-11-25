package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ritchie on 7/6/16.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyMetaCategoryValue extends CategoryValueSuperclass {

    @JsonIgnore
    @ManyToOne(optional = false)
    private PolicyMetaCategory policyMetaCategory;

    @Column
    @CreationTimestamp
    private Date created;

    @Column
    @UpdateTimestamp
    private Date modified;

    @OneToMany(mappedBy = "policyMetaCategoryValue", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<SubcategoryValue> subcategoryValuesList = new ArrayList<>();

    @NotNull
    @Column(nullable = false)
    private boolean secondary;

    @NotNull
    @Column(nullable = false)
    private Integer sortOrder;

    @Column
    private BigDecimal fixedCost;

    public PolicyMetaCategoryValue() {
    }

    public PolicyMetaCategoryValue(String caption, String value, ValueType valueType) {
        super(caption, value, valueType);
    }

    public PolicyMetaCategoryValue(String caption, String value, ValueType valueType, Integer sortOrder) {
        super(caption, value, valueType);
        this.sortOrder = sortOrder;
    }


    public PolicyMetaCategory getPolicyMetaCategory() {
        return policyMetaCategory;
    }

    public void setPolicyMetaCategory(PolicyMetaCategory policyMetaCategory) {
        this.policyMetaCategory = policyMetaCategory;
    }

    public Date getCreated() { return created; }

    public void setCreated(Date created) { this.created = created; }

    public Date getModified() { return modified; }

    public void setModified(Date modified) { this.modified = modified; }

    public List<SubcategoryValue> getSubcategoryValuesList() {
        Collections.sort(subcategoryValuesList);
        return subcategoryValuesList;
    }

    public Integer getValueIndexBySubcategory(Long subcategoryId, Integer index) {
        List<SubcategoryValue> subcategoryValues = subcategoryValuesList.stream().filter(subcategoryValue -> subcategoryValue.getId() != null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(subcategoryValues)) {
            Collections.sort(subcategoryValues);
            return IntStream.range(0, subcategoryValues.size())
                    .filter(it -> Objects.equals(subcategoryValues.get(it).getSubcategory().getId(), subcategoryId))
                    .findFirst()
                    .orElse(subcategoryValuesList.size());
        }
        return index;
    }

    public void setSubcategoryValuesList(List<SubcategoryValue> subcategoryValuesList) {
        this.subcategoryValuesList = subcategoryValuesList;
    }

    public boolean isSecondary() {
        return secondary;
    }

    public void setSecondary(boolean secondary) {
        this.secondary = secondary;
    }

    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Integer getSortOrder() { return sortOrder; }

    public BigDecimal getFixedCost() {
		return fixedCost;
	}

    public void setFixedCost(BigDecimal fixedCost) {
		this.fixedCost = fixedCost;
	}

    @Override
    public int compareTo(Object aThat){
        if (aThat==null || this==null) return 0;
        if (this == aThat) return 0;
        final PolicyMetaCategoryValue that = (PolicyMetaCategoryValue) aThat;
        if (that.getSortOrder()==null || this.getSortOrder() == null) return 0;
        return Integer.compare(this.getSortOrder(),that.getSortOrder());
    }
}
