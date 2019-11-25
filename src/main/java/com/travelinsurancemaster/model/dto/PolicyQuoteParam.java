package com.travelinsurancemaster.model.dto;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Alexander.Isaenco
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyQuoteParam implements Serializable {
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

    @ManyToOne(optional = false)
    private PolicyMeta policyMeta;

    @Column(nullable = false)
    private Long value;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "type1")),
            @AttributeOverride(name = "valueFrom", column = @Column(name = "value_from_1")),
            @AttributeOverride(name = "valueTo", column = @Column(name = "value_to_1"))
    })
    private PolicyQuoteParamValue param1;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "type2")),
            @AttributeOverride(name = "valueFrom", column = @Column(name = "value_from_2")),
            @AttributeOverride(name = "valueTo", column = @Column(name = "value_to_2"))
    })
    private PolicyQuoteParamValue param2;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "type3")),
            @AttributeOverride(name = "valueFrom", column = @Column(name = "value_from_3")),
            @AttributeOverride(name = "valueTo", column = @Column(name = "value_to_3"))
    })
    private PolicyQuoteParamValue param3;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "type4")),
            @AttributeOverride(name = "valueFrom", column = @Column(name = "value_from_4")),
            @AttributeOverride(name = "valueTo", column = @Column(name = "value_to_4"))
    })
    private PolicyQuoteParamValue param4;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "type5")),
            @AttributeOverride(name = "valueFrom", column = @Column(name = "value_from_5")),
            @AttributeOverride(name = "valueTo", column = @Column(name = "value_to_5"))
    })
    private PolicyQuoteParamValue param5;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyQuoteParam", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyQuoteParamRestriction> policyQuoteParamRestrictions = new ArrayList<>();

    public PolicyQuoteParam() {
    }

    public PolicyQuoteParam(PolicyMeta policyMeta, long value, PolicyQuoteParamValue param1, PolicyQuoteParamValue param2, PolicyQuoteParamValue param3, PolicyQuoteParamValue param4, PolicyQuoteParamValue param5) {
        this.policyMeta = policyMeta;
        this.value = value;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.param5 = param5;
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

    public PolicyMeta getPolicyMeta() {
        return policyMeta;
    }

    public void setPolicyMeta(PolicyMeta policyMeta) {
        this.policyMeta = policyMeta;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public PolicyQuoteParamValue getParam1() {
        return param1;
    }

    public void setParam1(PolicyQuoteParamValue param1) {
        this.param1 = param1;
    }

    public PolicyQuoteParamValue getParam2() {
        return param2;
    }

    public void setParam2(PolicyQuoteParamValue param2) {
        this.param2 = param2;
    }

    public PolicyQuoteParamValue getParam3() {
        return param3;
    }

    public void setParam3(PolicyQuoteParamValue param3) {
        this.param3 = param3;
    }

    public PolicyQuoteParamValue getParam4() {
        return param4;
    }

    public void setParam4(PolicyQuoteParamValue param4) {
        this.param4 = param4;
    }

    public PolicyQuoteParamValue getParam5() {
        return param5;
    }

    public void setParam5(PolicyQuoteParamValue param5) {
        this.param5 = param5;
    }

    public List<PolicyQuoteParamRestriction> getPolicyQuoteParamRestrictions() {
        return policyQuoteParamRestrictions;
    }

    public void setPolicyQuoteParamRestrictions(List<PolicyQuoteParamRestriction> policyQuoteParamRestrictions) {
        this.policyQuoteParamRestrictions = policyQuoteParamRestrictions;
    }

    public List<PolicyQuoteParamValue> getListParams() {
        List<PolicyQuoteParamValue> policyQuoteParamValues = new ArrayList<>();
        policyQuoteParamValues.add(param1);
        policyQuoteParamValues.add(param2);
        policyQuoteParamValues.add(param3);
        policyQuoteParamValues.add(param4);
        policyQuoteParamValues.add(param5);
        return policyQuoteParamValues;
    }
}
