package com.travelinsurancemaster.model.dto.json;

import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 13.10.15.
 */
public class JsonSimpleResult {
    private final String status;
    private List<ObjectError> errors = new ArrayList<>();

    public JsonSimpleResult(String status, List<ObjectError> errors) {
        this.status = status;
        this.errors = errors;
    }

    public JsonSimpleResult(String status) {
        this.status = status;
        this.errors = null;
    }

    public String getStatus() {
        return status;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
