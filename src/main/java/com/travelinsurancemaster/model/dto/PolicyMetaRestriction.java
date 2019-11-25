package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander.Isaenco
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyMetaRestriction extends Restriction {
    private static final long serialVersionUID = -5829722143107441508L;

    @JsonIgnore
    @ManyToOne(optional = false)
    private PolicyMeta policyMeta;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(targetClass = CountryCode.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "policy_meta_restriction_country", joinColumns = @JoinColumn(name = "restriction_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private Set<CountryCode> countries = new HashSet<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(targetClass = StateCode.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "policy_meta_restriction_state", joinColumns = @JoinColumn(name = "restriction_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private Set<StateCode> states = new HashSet<>();

    public PolicyMeta getPolicyMeta() {
        return policyMeta;
    }

    public void setPolicyMeta(PolicyMeta policyMeta) {
        this.policyMeta = policyMeta;
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
