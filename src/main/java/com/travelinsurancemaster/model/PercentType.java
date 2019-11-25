package com.travelinsurancemaster.model;

import com.travelinsurancemaster.model.dto.CommissionValueType;

/**
 * Created by ritchie on 5/18/15.
 */
public enum PercentType {
    NONE("None", CommissionValueType.FIX, 0),
    PERCENT("Percent", CommissionValueType.PERCENT, 1),
    FIXED("Fixed", CommissionValueType.FIX, 2),
    RANGE_PERCENT("Range Percent", CommissionValueType.PERCENT, 3),
    RANGE_FIXED("Range Fixed", CommissionValueType.FIX, 4),
    CALCULATED("Calculated", CommissionValueType.CALCULATED, 5);

    private String type;
    private CommissionValueType commissionValueType;
    private int id;

    PercentType(String type, CommissionValueType commissionValueType, int id) {
        this.type = type;
        this.commissionValueType = commissionValueType;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public CommissionValueType getCommissionValueType() {
        return commissionValueType;
    }

    public int getId() {
        return id;
    }
}
