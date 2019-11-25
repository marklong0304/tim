package com.travelinsurancemaster.model.dto.commission;

import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.CommissionValueType;
import com.travelinsurancemaster.model.dto.PercentInfo;

import java.util.List;

/**
 * Created by N.Kurennoy on 17.05.2016.
 */
public class CompensationWrapper {
    private CommissionValueType commissionValueType;
    private Long id;
    private String object;
    private String name;
    private List<PercentInfo> percentInfo;
    private PercentType percentType;
    private boolean allCompensationsEqual;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PercentInfo> getPercentInfo() {
        return percentInfo;
    }

    public void setPercentInfo(List<PercentInfo> percentInfo) {
        this.percentInfo = percentInfo;
    }

    public PercentType getPercentType() {
        return percentType;
    }

    public void setPercentType(PercentType percentType) {
        this.percentType = percentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public CommissionValueType getCommissionValueType() {
        return commissionValueType;
    }

    public void setCommissionValueType(CommissionValueType commissionValueType) {
        this.commissionValueType = commissionValueType;
    }

    public boolean getAllCompensationsEqual() { return allCompensationsEqual; }

    public void setAllCompensationsEqual(boolean allCompensationsEqual) { this.allCompensationsEqual = allCompensationsEqual; }
}
