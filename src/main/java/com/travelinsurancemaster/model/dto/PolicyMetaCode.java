package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ritchie on 4/1/16.
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyMetaCode implements Serializable, Comparable<PolicyMetaCode> {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @CreationTimestamp
    private Date created;

    @Column
    @UpdateTimestamp
    private Date modified;

    @NotNull
    @Column(nullable = false)
    private String code;

    @NotNull
    @Column(nullable = false)
    private String name;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMetaCode", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PolicyMetaCodeRestriction> policyMetaCodeRestrictions = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(optional = false)
    private PolicyMeta policyMeta;

    public PolicyMetaCode() {
    }

    public PolicyMetaCode(PolicyMeta policyMeta) {
        this.policyMeta = policyMeta;
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

    public List<PolicyMetaCodeRestriction> getPolicyMetaCodeRestrictions() {
        return policyMetaCodeRestrictions;
    }

    public void setPolicyMetaCodeRestrictions(List<PolicyMetaCodeRestriction> policyMetaCodeRestrictions) {
        this.policyMetaCodeRestrictions = policyMetaCodeRestrictions;
    }

    public PolicyMeta getPolicyMeta() {
        return policyMeta;
    }

    public void setPolicyMeta(PolicyMeta policyMeta) {
        this.policyMeta = policyMeta;
    }

    @Override
    public int compareTo(PolicyMetaCode policyMetaCode) {
        if (CollectionUtils.isEmpty(this.getPolicyMetaCodeRestrictions()) &&
                CollectionUtils.isEmpty(policyMetaCode.getPolicyMetaCodeRestrictions())) {
            return 0;
        } else if (CollectionUtils.isEmpty(policyMetaCode.getPolicyMetaCodeRestrictions())){
            return -1;
        } else {
            return 1;
        }
    }
}
