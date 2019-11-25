package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ritchie on 3/31/16.
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyMetaCategoryRestriction extends Restriction {

    @JsonIgnore
    @ManyToOne(optional = false)
    private PolicyMetaCategory policyMetaCategory;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(targetClass = CountryCode.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "policy_meta_category_restriction_country", joinColumns = @JoinColumn(name = "category_restriction_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private Set<CountryCode> countries = new HashSet<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(targetClass = StateCode.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "policy_meta_category_restriction_state", joinColumns = @JoinColumn(name = "category_restriction_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private Set<StateCode> states = new HashSet<>();

    public PolicyMetaCategoryRestriction() {
    }

    public PolicyMetaCategoryRestriction(PolicyMetaCategory policyMetaCategory) {
        this.policyMetaCategory = policyMetaCategory;
    }

    public PolicyMetaCategory getPolicyMetaCategory() {
        return policyMetaCategory;
    }

    public void setPolicyMetaCategory(PolicyMetaCategory policyMetaCategory) {
        this.policyMetaCategory = policyMetaCategory;
    }

    @Override
    public Set<CountryCode> getCountries() {
        return countries;
    }

    @Override
    public void setCountries(Set<CountryCode> countries) {
        this.countries = countries;
    }

    @Override
    public Set<StateCode> getStates() {
        return states;
    }

    @Override
    public void setStates(Set<StateCode> states) {
        this.states = states;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PolicyMetaCategoryRestriction)) {
            return false;
        }
        final PolicyMetaCategoryRestriction that = (PolicyMetaCategoryRestriction) obj;
        return new EqualsBuilder()
            .append(this.restrictionType, that.getRestrictionType())
            .append(this.restrictionPermit, that.getRestrictionPermit())
            .append(this.minValue, that.getMinValue())
            .append(this.maxValue, that.getMaxValue())
            .append(this.countries, that.getCountries())
            .append(this.states, that.getStates())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(this.restrictionType)
            .append(this.restrictionPermit)
            .append(this.minValue)
            .append(this.maxValue)
            .append(this.countries)
            .append(this.states)
            .toHashCode();
    }
}
