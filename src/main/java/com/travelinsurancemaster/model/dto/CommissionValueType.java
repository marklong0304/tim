package com.travelinsurancemaster.model.dto;

/**
 * Created by Chernov Artur on 18.08.15.
 */
public enum CommissionValueType {
    FIX(0),
    PERCENT(1),
    CALCULATED(2);

    private int id;

    CommissionValueType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
