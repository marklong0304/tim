package com.travelinsurancemaster.model.webservice.sevencorners;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ValidationStatus {
    VALID  (1),
    WARNING(2),
    INVALID(3);

    private final int status;

    private ValidationStatus(int status) {
        this.status = status;
    }

    @JsonValue
    public int getValue() {
        return this.status;
    }

    @JsonCreator
    public static ValidationStatus forValue(Integer value) {
        if(value == WARNING.getValue()) {
            return WARNING;
        } if(value == INVALID.getValue()) {
            return INVALID;
        } else {
            return VALID;
        }
    }
}
