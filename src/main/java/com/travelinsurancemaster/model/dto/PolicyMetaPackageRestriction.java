package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander.Isaenco
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyMetaPackageRestriction extends Restriction {

    @JsonIgnore
    @ManyToOne(optional = false)
    private PolicyMetaPackage policyMetaPackage;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(targetClass = CountryCode.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "policy_meta_package_restriction_country", joinColumns = @JoinColumn(name = "package_restriction_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private Set<CountryCode> countries = new HashSet<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(targetClass = StateCode.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "policy_meta_package_restriction_state", joinColumns = @JoinColumn(name = "package_restriction_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private Set<StateCode> states = new HashSet<>();

    public PolicyMetaPackageRestriction()
    {
        // Do nothing
    }

    public PolicyMetaPackageRestriction(PolicyMetaPackage policyMetaPackage)
    {
        this.policyMetaPackage = policyMetaPackage;
    }

    public PolicyMetaPackage getPolicyMetaPackage() {
        return policyMetaPackage;
    }

    public void setPolicyMetaPackage(PolicyMetaPackage policyMetaPackage) {
        this.policyMetaPackage = policyMetaPackage;
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
}
