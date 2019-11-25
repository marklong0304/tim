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
 * Created by N.Kurennoy on 19.05.2016.
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyQuoteParamRestriction extends Restriction {

    @JsonIgnore
    @ManyToOne(optional = false)
    private PolicyQuoteParam policyQuoteParam;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(targetClass = CountryCode.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "policy_quote_param_restriction_country", joinColumns = @JoinColumn(name = "policy_quote_param_restriction_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private Set<CountryCode> countries = new HashSet<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(targetClass = StateCode.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "policy_quote_param_restriction_state", joinColumns = @JoinColumn(name = "policy_quote_param_restriction_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private Set<StateCode> states = new HashSet<>();

    public PolicyQuoteParam getPolicyQuoteParam() {
        return policyQuoteParam;
    }

    public void setPolicyQuoteParam(PolicyQuoteParam policyQuoteParam) {
        this.policyQuoteParam = policyQuoteParam;
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
