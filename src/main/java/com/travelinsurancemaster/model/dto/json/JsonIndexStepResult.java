package com.travelinsurancemaster.model.dto.json;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 10.08.15.
 */
public class JsonIndexStepResult {

    private final String status;
    private List<FieldError> errors = new ArrayList<>();
    private String quoteStorageId;
    private boolean zeroCost = false;

    public JsonIndexStepResult(String status, List<FieldError> errors) {
        this.status = status;
        this.errors = errors;
    }

    public JsonIndexStepResult(String status, String quoteStorageId, boolean zeroCost) {
        this.quoteStorageId = quoteStorageId;
        this.status = status;
        this.errors = null;
        this.zeroCost = zeroCost;
    }

    public JsonIndexStepResult(String status) {
        this.status = status;
        this.errors = null;
    }

    public String getStatus() {
        return status;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public String getQuoteStorageId() {
        return quoteStorageId;
    }

    public boolean isZeroCost() {
        return zeroCost;
    }

    public void setZeroCost(boolean zeroCost) {
        this.zeroCost = zeroCost;
    }
}