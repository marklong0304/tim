package com.travelinsurancemaster.model.dto;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Created by ritchie on 3/31/16.
 */
@MappedSuperclass
public abstract class Restriction implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @CreationTimestamp
    private Date created;

    @Column
    @UpdateTimestamp
    private Date modified;

    @Column
    @Enumerated(EnumType.STRING)
    protected RestrictionPermit restrictionPermit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected RestrictionType restrictionType;

    @Column
    protected Integer minValue;

    @Column
    protected Integer maxValue;

    @Column(columnDefinition = "TEXT")
    protected String calculatedRestrictions;

    @Transient
    private String tempId;

    public Restriction() {
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

    public RestrictionPermit getRestrictionPermit() {
        return restrictionPermit;
    }

    public void setRestrictionPermit(RestrictionPermit restrictionPermit) {
        this.restrictionPermit = restrictionPermit;
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(RestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public abstract Set<CountryCode> getCountries();

    public abstract void setCountries(Set<CountryCode> countries);

    public abstract Set<StateCode> getStates();

    public abstract void setStates(Set<StateCode> states);

    public String getCalculatedRestrictions() {
        return calculatedRestrictions;
    }

    public void setCalculatedRestrictions(String calculatedRestrictions) {
        this.calculatedRestrictions = calculatedRestrictions;
    }

    public String getTempId() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    public enum RestrictionPermit {
        ENABLED, DISABLED
    }

    public enum RestrictionType {
        CITIZEN, DESTINATION, RESIDENT, AGE, TRIP_COST_PER_TRAVELER, TRIP_COST, LENGTH_OF_TRAVEL, CALCULATE
    }
}
